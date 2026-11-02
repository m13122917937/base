/** Keep one deployment process and its private stdin across model tool calls. */
import { spawn } from 'node:child_process';
import { randomUUID } from 'node:crypto';
import { fileURLToPath } from 'node:url';
import { readUsage } from './jenkins_usage.mjs';

const sessions = new Map();
const targets = new Set(['ui', 'web', 'api', 'admin']);
const script = fileURLToPath(new URL('./jenkins_api.py', import.meta.url));

function receive(session, chunk) {
  session.buffer += chunk.toString('utf8');
  let boundary;
  while ((boundary = session.buffer.indexOf('\n')) !== -1) {
    const line = session.buffer.slice(0, boundary).trim();
    session.buffer = session.buffer.slice(boundary + 1);
    if (!line) continue;
    try {
      const event = JSON.parse(line);
      if (!event || typeof event !== 'object' || (!event.state && !event.error)) throw new Error('Invalid event.');
      if (event.error) event.state = 'ERROR';
      session.events.push(event);
      session.latest = event;
    } catch {
      session.latest = { state: 'ERROR', error: 'Invalid deployment event; output suppressed.' };
      session.events.push(session.latest);
    }
  }
}

export function start({ target, auth, build, params = {}, timeout = 1800, python = 'python', waitResult = false }, spawnProcess = spawn) {
  if (!targets.has(target)) throw new Error('Unsupported production target.');
  if (build !== undefined && (!Number.isInteger(build) || build <= 0)) throw new Error('Invalid build number.');
  for (const [id, session] of sessions) {
    if (!session.done && session.target === target) return { id, state: session.latest.state, existing: true };
  }
  if (Object.keys(params).some(name => /password|passwd|secret|token|credential|private.?key/i.test(name))) {
    throw new Error('Secret parameters cannot be passed on the command line.');
  }
  if (build !== undefined && Object.keys(params).length) throw new Error('Cannot change parameters when resuming a build.');
  const payload = auth ? JSON.stringify(auth) : '';
  const args = ['-X', 'utf8', '-B', script, 'deploy', target, '--confirmed', '--timeout', String(timeout)];
  if (build !== undefined) args.push('--build', String(build));
  if (waitResult) args.push('--wait-result');
  if (auth) args.push('--auth-stdin');
  for (const [name, value] of Object.entries(params)) args.push('--param', `${name}=${value}`);
  const child = spawnProcess(python, args, { windowsHide: true, shell: false, stdio: ['pipe', 'pipe', 'pipe'] });
  const id = randomUUID();
  const session = { child, target, events: [], buffer: '', latest: { state: 'STARTING', target }, done: false };
  session.completion = new Promise(resolve => { session.resolve = resolve; });
  sessions.set(id, session);
  child.stdout.on('data', chunk => receive(session, chunk));
  child.stderr.on('data', chunk => receive(session, chunk));
  child.stdin.on('error', () => {});
  child.once('error', () => {
    session.latest = { state: 'ERROR', error: 'Deployment process could not start.' };
    session.events.push(session.latest);
  });
  child.once('close', exitCode => {
    if (session.buffer.trim()) {
      session.latest = { state: 'ERROR', error: 'Incomplete deployment output; details suppressed.' };
      session.events.push(session.latest);
    }
    session.done = true;
    session.exitCode = exitCode;
    session.resolve();
  });
  child.stdin.end(payload);
  return { id, state: 'STARTING', target };
}

export async function wait(id, timeoutMs = 45000) {
  const session = sessions.get(id);
  if (!session) throw new Error('Deployment session not found; resume by exact Jenkins build number.');
  if (!Number.isInteger(timeoutMs) || timeoutMs < 0 || timeoutMs > 45000) throw new Error('Wait must be 0–45000 ms.');
  let timer;
  try {
    await Promise.race([session.completion, new Promise(resolve => { timer = setTimeout(resolve, timeoutMs); })]);
  } finally {
    clearTimeout(timer);
  }
  const result = { id, done: session.done, state: session.latest.state, events: session.events.splice(0) };
  if (session.done) {
    result.exit_code = session.exitCode;
    sessions.delete(id);
  }
  return result;
}

export function stop(id) {
  const session = sessions.get(id);
  if (!session || session.done) return { id, stopped: false };
  session.child.kill();
  return { id, stopped: true, note: 'Only the local watcher stopped; Jenkins build was not cancelled.' };
}

/** Normal entry: start and wait once, returning one compact outcome. */
export async function run({ usage, ...options }, spawnProcess = spawn) {
  const handle = start(options, spawnProcess);
  if (usage) sessions.get(handle.id).usage = usage;
  return next(handle.id);
}

/** Continue only if the first bounded wait did not finish the submission. */
export async function next(id, timeoutMs = 45000) {
  const usage = sessions.get(id)?.usage;
  const result = await wait(id, timeoutMs);
  const last = result.events.at(-1) || {};
  const fields = ['target', 'build', 'url', 'queue_id', 'result_checked', 'error', 'pending_inputs'];
  const compact = { id, done: result.done, state: result.state };
  for (const key of fields) if (last[key] !== undefined) compact[key] = last[key];
  if (result.done) {
    compact.exit_code = result.exit_code;
    if (usage) compact.tokens = await readUsage(usage);
  }
  return compact;
}
