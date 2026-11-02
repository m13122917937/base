/** Read numeric usage only from the caller's explicitly identified local chat. */
import { createReadStream } from 'node:fs';
import { readdir } from 'node:fs/promises';
import { join } from 'node:path';
import { createInterface } from 'node:readline';

async function locateLog({ logPath, homeDir, threadId }) {
  if (logPath) return logPath;
  if (!homeDir || !/^[a-f0-9]{8}-[a-f0-9]{4}-7[a-f0-9]{3}-[a-f0-9]{4}-[a-f0-9]{12}$/i.test(threadId || '')) {
    throw new Error('Current chat identity unavailable.');
  }
  const created = Number.parseInt(threadId.replaceAll('-', '').slice(0, 12), 16);
  const matches = [];
  for (const offset of [-1, 0, 1]) {
    const date = new Date(created + offset * 86400000).toISOString().slice(0, 10).split('-');
    const directory = join(homeDir, '.codex', 'sessions', ...date);
    try {
      const names = await readdir(directory);
      for (const name of names) {
        if (name.startsWith('rollout-') && name.endsWith(`-${threadId}.jsonl`)) matches.push(join(directory, name));
      }
    } catch (error) {
      if (error.code !== 'ENOENT') throw error;
    }
  }
  if (matches.length !== 1) throw new Error('Current chat usage log not uniquely identified.');
  return matches[0];
}

async function snapshot(logPath) {
  const input = createReadStream(logPath, { encoding: 'utf8' });
  const lines = createInterface({ input, crlfDelay: Infinity });
  let latest, baseline, timestamp, streamError;
  input.on('error', error => { streamError = error; lines.close(); });
  try {
    for await (const line of lines) {
      let record;
      try { record = JSON.parse(line); } catch { continue; }
      if (record.type !== 'event_msg') continue;
      const event = record.payload || {};
      if (event.type === 'task_started') baseline = latest;
      if (event.type === 'token_count' && event.info?.total_token_usage) {
        latest = event.info.total_token_usage;
        timestamp = record.timestamp;
      }
    }
  } finally {
    lines.close();
    input.destroy();
  }
  if (streamError) throw streamError;
  if (!baseline || !latest) throw new Error('Current turn usage baseline unavailable.');
  const fields = ['input_tokens', 'cached_input_tokens', 'output_tokens', 'total_tokens'];
  const counts = Object.fromEntries(fields.map(key => [key, latest[key] - baseline[key]]));
  if (Object.values(counts).some(value => !Number.isSafeInteger(value) || value < 0)
      || counts.cached_input_tokens > counts.input_tokens) throw new Error('Usage counters are incomplete.');
  return { available: true, snapshot: true, timestamp, ...counts,
    uncached_input_tokens: counts.input_tokens - counts.cached_input_tokens };
}

export async function readUsage(options) {
  try {
    return await snapshot(await locateLog(options));
  } catch {
    return { available: false, reason: 'Current turn token snapshot unavailable.' };
  }
}
