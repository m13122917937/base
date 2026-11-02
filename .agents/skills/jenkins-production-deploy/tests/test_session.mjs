import assert from 'node:assert/strict';
import { EventEmitter } from 'node:events';
import test from 'node:test';
import { start, wait, stop, run, next } from '../scripts/jenkins_session.mjs';

function fakeProcess(events = [], exitCode = 0, finish = true) {
  const child = new EventEmitter();
  child.stdout = new EventEmitter();
  child.stderr = new EventEmitter();
  child.stdin = new EventEmitter();
  child.stdin.end = value => { child.payload = value; };
  child.kill = () => { child.emit('close', null); };
  const spawn = (command, args, options) => {
    child.invocation = { command, args, options };
    queueMicrotask(() => {
      for (const event of events) child.stdout.emit('data', Buffer.from(JSON.stringify(event) + '\n'));
      if (finish) child.emit('close', exitCode);
    });
    return child;
  };
  return { child, spawn };
}

test('one process gets credentials once via stdin, and wait returns compact terminal events', async () => {
  const fake = fakeProcess([{ state: 'QUEUED', queue_id: 321 }, { state: 'SUCCESS', build: 123 }]);
  const session = start({ target: 'ui', auth: { username: 'offline-user', password: 'offline-secret' } }, fake.spawn);
  const result = await wait(session.id);
  assert.equal(result.done, true);
  assert.equal(result.exit_code, 0);
  assert.equal(result.state, 'SUCCESS');
  assert.equal(result.events.length, 2);
  assert.equal(JSON.parse(fake.child.payload).password, 'offline-secret');
  assert.ok(!JSON.stringify(fake.child.invocation).includes('offline-secret'));
  assert.equal(fake.child.invocation.options.shell, false);
  assert.equal(fake.child.invocation.args.filter(arg => arg === 'deploy').length, 1);
  assert.ok(!fake.child.invocation.args.includes('--wait-result'));
  await assert.rejects(wait(session.id), /not found/);
});

test('default completion is deployment submitted; waiting for the final result is opt in', async () => {
  const submitted = fakeProcess([{ state: 'DEPLOYMENT_SUBMITTED', build: 123, result_checked: false }]);
  const session = start({ target: 'ui' }, submitted.spawn);
  const result = await wait(session.id);
  assert.equal(result.done, true);
  assert.equal(result.exit_code, 0);
  assert.equal(result.state, 'DEPLOYMENT_SUBMITTED');
  assert.equal(result.events[0].result_checked, false);
  const full = fakeProcess([{ state: 'SUCCESS', build: 123 }]);
  const monitoring = start({ target: 'ui', build: 123, waitResult: true }, full.spawn);
  assert.ok(full.child.invocation.args.includes('--wait-result'));
  assert.equal((await wait(monitoring.id)).state, 'SUCCESS');
});

test('resume names the exact build and duplicate start reuses the active process', async () => {
  const fake = fakeProcess([], 0, false);
  const session = start({ target: 'web', build: 82 }, fake.spawn);
  const again = start({ target: 'web' }, () => { throw new Error('Must not start a second process.'); });
  assert.equal(again.id, session.id);
  assert.ok(fake.child.invocation.args.includes('--build'));
  assert.ok(fake.child.invocation.args.includes('82'));
  const idle = await wait(session.id, 0);
  assert.equal(idle.done, false);
  assert.deepEqual(idle.events, []);
  stop(session.id);
  const stopped = await wait(session.id, 0);
  assert.equal(stopped.exit_code, null);
});

test('invalid output is suppressed, and failed subprocess is never success', async () => {
  const fake = fakeProcess([], 1, false);
  const session = start({ target: 'admin' }, fake.spawn);
  fake.child.stderr.emit('data', Buffer.from('unstructured confidential error\n'));
  fake.child.emit('close', 1);
  const result = await wait(session.id, 0);
  assert.equal(result.exit_code, 1);
  assert.equal(result.state, 'ERROR');
  assert.equal(result.events[0].state, 'ERROR');
  assert.ok(!JSON.stringify(result).includes('confidential'));
});

test('unknown targets, secret CLI parameters and overly long waits are refused', async () => {
  assert.throws(() => start({ target: 'other' }), /Unsupported/);
  assert.throws(() => start({ target: 'api', params: { PASSWORD: 'offline-value' } }), /Secret/);
  assert.throws(() => start({ target: 'api', build: 12, params: { BRANCH: 'other' } }), /Cannot change/);
  const fake = fakeProcess([], 0, false);
  const session = start({ target: 'api' }, fake.spawn);
  await assert.rejects(wait(session.id, 60000), /45000/);
  stop(session.id);
  await wait(session.id, 0);
});

test('normal run starts and waits once, returning no duplicate event history', async () => {
  const fake = fakeProcess([{ state: 'QUEUED', queue_id: 321 }, { state: 'BUILDING', build: 123 },
    { state: 'DEPLOYMENT_SUBMITTED', target: 'web', build: 123, url: 'offline-build', result_checked: false }]);
  const result = await run({ target: 'web' }, fake.spawn);
  assert.equal(result.done, true);
  assert.equal(result.state, 'DEPLOYMENT_SUBMITTED');
  assert.equal(result.build, 123);
  assert.equal(result.url, 'offline-build');
  assert.ok(!('events' in result));
  assert.ok(!('tokens' in result));
});

test('unavailable optional token statistics cannot mask a successful submission', async () => {
  const fake = fakeProcess([{ state: 'DEPLOYMENT_SUBMITTED', build: 123 }]);
  const result = await run({ target: 'web', usage: { logPath: '__missing_jenkins_usage_log__.jsonl' } }, fake.spawn);
  assert.equal(result.exit_code, 0);
  assert.equal(result.state, 'DEPLOYMENT_SUBMITTED');
  assert.equal(result.tokens.available, false);
});

test('continuation reuses the existing process and returns only the latest compact outcome', async () => {
  const fake = fakeProcess([], 0, false);
  const session = start({ target: 'api' }, fake.spawn);
  const pending = await next(session.id, 0);
  assert.equal(pending.done, false);
  assert.ok(!('events' in pending));
  fake.child.stdout.emit('data', Buffer.from(JSON.stringify({ state: 'DEPLOYMENT_SUBMITTED', build: 12 }) + '\n'));
  fake.child.emit('close', 0);
  const result = await next(session.id, 0);
  assert.equal(result.build, 12);
  assert.equal(result.state, 'DEPLOYMENT_SUBMITTED');
  assert.ok(!('events' in result));
});
