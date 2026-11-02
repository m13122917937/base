import assert from 'node:assert/strict';
import { mkdtemp, mkdir, writeFile, rm } from 'node:fs/promises';
import { join, resolve } from 'node:path';
import { tmpdir } from 'node:os';
import test from 'node:test';
import { readUsage } from '../scripts/jenkins_usage.mjs';

async function fixture(t) {
  const root = await mkdtemp(join(tmpdir(), 'jenkins-usage-'));
  t.after(async () => {
    assert.ok(resolve(root).startsWith(resolve(tmpdir()) + '\\') || resolve(root).startsWith(resolve(tmpdir()) + '/'));
    assert.ok(root.includes('jenkins-usage-'));
    await rm(root, { recursive: true, force: true });
  });
  return root;
}

function event(type, total, timestamp = 'offline') {
  const payload = { type };
  if (total) payload.info = { total_token_usage: total };
  return JSON.stringify({ type: 'event_msg', payload, timestamp });
}

function log() {
  return [event('token_count', { input_tokens: 100, cached_input_tokens: 80, output_tokens: 10, total_tokens: 110 }),
    event('task_started'), JSON.stringify({ type: 'response_item', payload: { text: 'private content must stay local' } }),
    event('token_count', { input_tokens: 160, cached_input_tokens: 130, output_tokens: 14, total_tokens: 174 }),
    '{incomplete tail'].join('\n');
}

test('usage contains only the current turn numeric snapshot, with cache separated', async t => {
  const root = await fixture(t);
  const path = join(root, 'current.jsonl');
  await writeFile(path, log());
  const result = await readUsage({ logPath: path });
  assert.equal(result.available, true);
  assert.equal(result.snapshot, true);
  assert.equal(result.total_tokens, 64);
  assert.equal(result.cached_input_tokens, 50);
  assert.equal(result.uncached_input_tokens, 10);
  assert.equal(result.output_tokens, 4);
  assert.ok(!JSON.stringify(result).includes('private content'));
});

test('only the identified chat is selected, despite another chat in the same directory', async t => {
  const root = await fixture(t);
  const threadId = '01a16832-446c-7192-baba-74216e3fbbba';
  const directory = join(root, '.codex', 'sessions', '2026', '10', '23');
  await mkdir(directory, { recursive: true });
  await writeFile(join(directory, `rollout-test-${threadId}.jsonl`), log());
  await writeFile(join(directory, 'rollout-other-chat.jsonl'), 'do not inspect');
  const result = await readUsage({ homeDir: root, threadId });
  assert.equal(result.total_tokens, 64);
});

test('missing logs, missing baseline and invalid counters are unavailable, never invented', async t => {
  const root = await fixture(t);
  assert.equal((await readUsage({ logPath: join(root, 'missing.jsonl') })).available, false);
  const path = join(root, 'invalid.jsonl');
  await writeFile(path, event('task_started'));
  assert.equal((await readUsage({ logPath: path })).available, false);
  await writeFile(path, log().replace('"input_tokens":160', '"input_tokens":90'));
  assert.equal((await readUsage({ logPath: path })).available, false);
});
