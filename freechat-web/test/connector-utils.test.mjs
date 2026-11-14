import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';
import test from 'node:test';
import i18next from 'i18next';
import ts from 'typescript';
import {
  changeConnectorMode,
  connectorDraft,
  connectorDraftJson,
  connectorErrorKeys,
  connectorOriginChanged,
  connectorUrl,
  ConnectorValidationError,
  isConnectorId,
  normalizeConnectorError,
  parseCharacterConnectorExt,
  parseConnectorJson,
  setCharacterConnectorIds,
  validateConnectorDraft,
} from '../src/libs/connector_utils.ts';
import {
  parseCharacterSkillExt,
  setCharacterSkillIds,
} from '../src/libs/skill_utils.ts';
import {
  characterExportUrl,
  exportCharacter,
} from '../src/libs/character_utils.ts';

const en = JSON.parse(
  await readFile(
    new URL('../src/i18n/locales/en/connector.json', import.meta.url)
  )
);
const zh = JSON.parse(
  await readFile(
    new URL('../src/i18n/locales/zh/connector.json', import.meta.url)
  )
);
const ta = JSON.parse(
  await readFile(
    new URL('../src/i18n/locales/ta/connector.json', import.meta.url)
  )
);
const id = 'a'.repeat(32);
const saved = {
  connectorId: id,
  revision: 'revision-1',
  name: 'Docs',
  transport: 'http',
  url: 'https://example.com/mcp',
  active: true,
  headerNames: ['Authorization', 'X-Project'],
};
const valid = (patch = {}) => ({
  ...connectorDraft(),
  name: 'Docs',
  url: 'https://example.com/mcp',
  ...patch,
});
const canonical = (patch = {}) =>
  JSON.stringify({
    name: 'Docs',
    transport: 'http',
    url: 'https://example.com/mcp',
    ...patch,
  });
function rejects(action, key) {
  assert.throws(action, (error) => {
    assert.ok(error instanceof ConnectorValidationError);
    assert.ok(Object.hasOwn(en, error.messageKey));
    assert.ok(Object.hasOwn(zh, error.messageKey));
    if (key) assert.equal(error.messageKey, key);
    return true;
  });
}

test('HTTP without headers works, both transports work and create never retains headers', () => {
  for (const transport of ['http', 'sse']) {
    const request = validateConnectorDraft(
      valid({ transport, url: 'http://example.com/mcp' })
    );
    assert.equal(request.transport, transport);
    assert.equal(request.active, true);
    assert.equal(request.timeout, undefined);
    assert.deepEqual(request.headers, {});
    assert.deepEqual(request.keepHeaderNames, []);
    assert.equal(request.expectedRevision, undefined);
  }
});

test('name and transport validation rejects commands and STDIO', () => {
  for (const name of ['', ' ', 'x'.repeat(129), 'bad\nname', 'bad\u200bname'])
    rejects(() => validateConnectorDraft(valid({ name })));
  for (const transport of ['stdio', 'command', 'HTTP', '', 'websocket']) {
    rejects(() => validateConnectorDraft(valid({ transport })));
    rejects(() => parseConnectorJson(canonical({ transport })));
  }
  assert.equal(
    validateConnectorDraft(valid({ name: '文档工具' })).name,
    '文档工具'
  );
});

test('names accept 1 to 64 characters and reject 65 in form and JSON', () => {
  for (const name of ['x', 'x'.repeat(64)]) {
    assert.equal(validateConnectorDraft(valid({ name })).name, name);
    assert.equal(parseConnectorJson(canonical({ name })).name, name);
  }
  const name = 'x'.repeat(65);
  const key = 'Enter a name of 1 to 64 characters without control characters.';
  rejects(() => validateConnectorDraft(valid({ name })), key);
  rejects(() => parseConnectorJson(canonical({ name })), key);
});

test('names and URLs reject unpaired UTF-16 surrogates but accept valid pairs', () => {
  for (const malformed of [
    '\ud800',
    '\udbff',
    '\udc00',
    '\udfff',
    '\ud800x',
    'x\udc00',
    '\udc00\ud800',
  ]) {
    const name = `Docs${malformed}`;
    const url = `https://example.com/${malformed}`;
    rejects(() => validateConnectorDraft(valid({ name })));
    rejects(() => parseConnectorJson(canonical({ name })));
    rejects(() => connectorUrl(url));
    rejects(() => validateConnectorDraft(valid({ url })));
    rejects(() => parseConnectorJson(canonical({ url })));
  }
  const name = 'Docs\ud800\udc00';
  const url = 'https://example.com/\ud800\udc00';
  assert.equal(validateConnectorDraft(valid({ name, url })).name, name);
  assert.equal(parseConnectorJson(canonical({ name, url })).url, url);
  assert.equal(connectorUrl(url).pathname, '/%F0%90%80%80');
});

test('timeout is optional, bounded, integer and never silently coerced from JSON strings', () => {
  for (const timeout of ['', '1000', '60000', '120000'])
    assert.doesNotThrow(() => validateConnectorDraft(valid({ timeout })));
  for (const timeout of [
    '0',
    '999',
    '120001',
    '-1000',
    '1000.5',
    '1e3',
    'Infinity',
    'NaN',
    ' ',
    '1000ms',
  ])
    rejects(() => validateConnectorDraft(valid({ timeout })));
  for (const timeout of [null, '1000', 999, 120001, 1000.5, true])
    rejects(() => parseConnectorJson(canonical({ timeout })));
  assert.equal(
    parseConnectorJson(canonical({ timeout: 1000 })).timeout,
    '1000'
  );
});

test('URL syntax rejects schemes, authority tricks, credentials, query, fragment and normalization ambiguities', () => {
  for (const url of [
    '',
    '/mcp',
    'file:///tmp/mcp',
    'ftp://example.com/mcp',
    'javascript:alert(1)',
    'https:/example.com/mcp',
    'https:///example.com/mcp',
    'https://',
    'https://user:secret@example.com/mcp',
    'https://@example.com',
    'https://example.com/mcp?',
    'https://example.com/mcp?key=secret',
    'https://example.com/mcp#',
    'https://example.com/mcp#secret',
    'https://example.com\\@evil.example/mcp',
    'https://exa\nmple.com',
    ' https://example.com',
    'https://example.com/a b',
    'https://example.com:0/mcp',
    'https://example.com:99999',
    'https://example.com:',
    'https://%65xample.com/mcp',
    'https://example..com/mcp',
    'https://-example.com',
    'https://example.com/%zz',
    'https://example.com/' + 'x'.repeat(2048),
  ])
    rejects(
      () => validateConnectorDraft(valid({ url })),
      'Use an HTTP or HTTPS URL without credentials, query parameters or fragments.'
    );
  for (const url of [
    'https://example.com:8443/mcp',
    'https://example.com/a%20b',
    'http://example.com/sse',
  ])
    assert.equal(connectorUrl(url).href, url);
  // Syntax checks cannot resolve DNS or authorize destinations; the server must do that at connect time.
  assert.doesNotThrow(() => connectorUrl('http://127.0.0.1/mcp'));
  assert.doesNotThrow(() => connectorUrl('http://[::1]/mcp'));
});

test('headers require HTTPS, unique safe names and printable ASCII values', () => {
  rejects(
    () =>
      validateConnectorDraft(
        valid({
          url: 'http://example.com',
          headers: [{ name: 'Authorization', value: 'secret' }],
        })
      ),
    'Configured headers require HTTPS.'
  );
  for (const name of [
    '',
    'Invalid Name',
    'bad:name',
    'bad\nname',
    'bad\tname',
    'X-Key\n',
    'X-Key\r',
    'X-文档',
    'Host',
    'Content-Length',
    'Connection',
    'Transfer-Encoding',
    'Proxy-Authorization',
    'X'.repeat(129),
  ]) {
    rejects(() =>
      validateConnectorDraft(valid({ headers: [{ name, value: 'secret' }] }))
    );
  }
  for (const value of [
    'secret\r\nInjected: true',
    'a\0b',
    'a\u007fb',
    'a\u200bb',
    'a\tb',
    'a\u0080b',
    '\u00a0',
    'café',
    '文档',
    '\ud800\udc00',
    '\ud800',
    '\udfff',
  ]) {
    const key =
      'Header values must contain only printable ASCII characters (spaces allowed, no tabs or newlines).';
    rejects(
      () =>
        validateConnectorDraft(valid({ headers: [{ name: 'X-Key', value }] })),
      key
    );
    rejects(
      () => parseConnectorJson(canonical({ headers: { 'X-Key': value } })),
      key
    );
  }
  rejects(() =>
    validateConnectorDraft(
      valid({
        headers: [
          { name: 'Authorization', value: 'one' },
          { name: 'authorization', value: 'two' },
        ],
      })
    )
  );
  assert.deepEqual(
    validateConnectorDraft(
      valid({ headers: [{ name: 'Authorization', value: 'Bearer token' }] })
    ).headers,
    { Authorization: 'Bearer token' }
  );
  const name = 'X'.repeat(128);
  const printable = Array.from({ length: 95 }, (_, index) =>
    String.fromCharCode(0x20 + index)
  ).join('');
  for (const value of ['', ' ', printable]) {
    assert.deepEqual(
      validateConnectorDraft(valid({ headers: [{ name, value }] })).headers,
      { [name]: value }
    );
  }
});

test('transport and routing headers and prefixes are blocked case-insensitively', () => {
  const key =
    'Use unique valid header names. Transport and routing headers are not allowed.';
  for (const reserved of [
    'host',
    'connection',
    'content-length',
    'transfer-encoding',
    'upgrade',
    'keep-alive',
    'proxy-connection',
    'proxy-authorization',
    'proxy-authenticate',
    'te',
    'trailer',
    'accept',
    'accept-encoding',
    'content-type',
    'origin',
    'referer',
    'forwarded',
    'last-event-id',
    'proxy-custom',
    'x-forwarded-for',
    'x-forwarded-custom',
    'mcp-session-id',
    'mcp-custom',
    'proxy-',
    'x-forwarded-',
    'mcp-',
  ]) {
    for (const name of [reserved, reserved.toUpperCase()]) {
      rejects(
        () =>
          validateConnectorDraft(
            valid({ headers: [{ name, value: 'secret' }] })
          ),
        key
      );
      rejects(
        () => parseConnectorJson(canonical({ headers: { [name]: 'secret' } })),
        key
      );
    }
  }
});

test('exact __proto__ header keys are rejected before SDK map serialization', () => {
  const name = '__proto__';
  const record = { ...saved, headerNames: [name] };
  for (const value of ['secret', null]) {
    rejects(() =>
      validateConnectorDraft(valid({ headers: [{ name, value }] }), record)
    );
    rejects(() =>
      parseConnectorJson(canonical({ headers: { [name]: value } }), record)
    );
  }
  assert.deepEqual(
    validateConnectorDraft(
      valid({ headers: [{ name: '__PROTO__', value: 'text' }] })
    ).headers,
    { __PROTO__: 'text' }
  );
});

test('header count and UTF-8 size are bounded without needing stored secret values', () => {
  const headers = Array.from({ length: 16 }, (_, index) => ({
    name: `X-${index}`,
    value: 'text',
  }));
  assert.doesNotThrow(() => validateConnectorDraft(valid({ headers })));
  rejects(() =>
    validateConnectorDraft(
      valid({ headers: [...headers, { name: 'X-17', value: '' }] })
    )
  );
  assert.doesNotThrow(() =>
    validateConnectorDraft(
      valid({ headers: [{ name: 'X', value: 'x'.repeat(16383) }] })
    )
  );
  rejects(() =>
    validateConnectorDraft(
      valid({ headers: [{ name: 'X', value: 'x'.repeat(16384) }] })
    )
  );
  rejects(() =>
    validateConnectorDraft(
      valid({ headers: [{ name: 'X', value: '界'.repeat(6000) }] })
    )
  );
});

test('saved values are represented only by null and never by masking text in writes', () => {
  const draft = connectorDraft(saved);
  assert.deepEqual(draft.headers, [
    { name: 'Authorization', value: null },
    { name: 'X-Project', value: null },
  ]);
  const request = validateConnectorDraft(draft, saved);
  assert.deepEqual(request.headers, {});
  assert.deepEqual(request.keepHeaderNames, saved.headerNames);
  assert.equal(request.expectedRevision, saved.revision);
  assert.doesNotMatch(JSON.stringify(request), /••|\*\*/);
  const json = connectorDraftJson(draft);
  assert.deepEqual(JSON.parse(json).headers, {
    Authorization: null,
    'X-Project': null,
  });
  assert.deepEqual(parseConnectorJson(json, saved), draft);
});

test('replace and delete are explicit; new or renamed null headers cannot retain secrets', () => {
  const draft = connectorDraft(saved);
  draft.headers = [{ name: 'Authorization', value: 'replacement' }];
  const request = validateConnectorDraft(draft, saved);
  assert.deepEqual(request.headers, { Authorization: 'replacement' });
  assert.deepEqual(request.keepHeaderNames, []);
  rejects(() =>
    parseConnectorJson(canonical({ headers: { Authorization: null } }))
  );
  rejects(() =>
    parseConnectorJson(canonical({ headers: { Unknown: null } }), saved)
  );
  rejects(() =>
    parseConnectorJson(canonical({ headers: { authorization: null } }), saved)
  );
  const deleted = validateConnectorDraft(
    parseConnectorJson(canonical({ headers: {} }), saved),
    saved
  );
  assert.deepEqual(deleted.headers, {});
  assert.deepEqual(deleted.keepHeaderNames, []);
});

test('origin changes require reentry or deletion, but same-origin path changes retain headers', () => {
  for (const url of [
    'https://other.example/mcp',
    'https://example.com:8443/mcp',
  ]) {
    const draft = { ...connectorDraft(saved), url };
    assert.equal(connectorOriginChanged(url, saved), true);
    rejects(
      () => validateConnectorDraft(draft, saved),
      'The URL origin changed. Replace or delete every saved header before saving.'
    );
    rejects(() => parseConnectorJson(connectorDraftJson(draft), saved));
    assert.doesNotThrow(() =>
      validateConnectorDraft({ ...draft, headers: [] }, saved)
    );
    assert.doesNotThrow(() =>
      validateConnectorDraft(
        { ...draft, headers: [{ name: 'Authorization', value: 'new-secret' }] },
        saved
      )
    );
  }
  assert.equal(
    connectorOriginChanged('https://example.com:443/new-path', saved),
    false
  );
  assert.deepEqual(
    validateConnectorDraft(
      { ...connectorDraft(saved), url: 'https://example.com/new' },
      saved
    ).keepHeaderNames,
    saved.headerNames
  );
  rejects(() =>
    validateConnectorDraft(
      { ...connectorDraft(saved), url: 'http://example.com/mcp' },
      saved
    )
  );
});

test('canonical and one-entry mcpServers JSON roundtrip without exposing saved values', () => {
  const draft = parseConnectorJson(
    JSON.stringify({
      mcpServers: {
        Docs: {
          type: 'sse',
          url: saved.url,
          timeout: 1000,
          headers: { Authorization: null, 'X-Key': 'new' },
          active: false,
        },
      },
    }),
    saved
  );
  assert.equal(draft.name, 'Docs');
  assert.equal(draft.transport, 'sse');
  assert.equal(draft.active, false);
  assert.deepEqual(parseConnectorJson(connectorDraftJson(draft), saved), draft);
  const request = validateConnectorDraft(draft, saved);
  assert.deepEqual(request.keepHeaderNames, ['Authorization']);
  assert.deepEqual(request.headers, { 'X-Key': 'new' });
});

test('invalid JSON, unknown fields, bulk and STDIO are rejected rather than dropped', () => {
  for (const value of [
    '',
    '{',
    'null',
    '[]',
    '42',
    '{"name":"Docs",}',
    canonical({ command: 'npx', args: [] }),
    canonical({ env: {} }),
    canonical({ keepHeaderNames: [] }),
    canonical({ type: 'http' }),
    canonical({ unexpected: true }),
    canonical({ active: 'true' }),
    canonical({ headers: null }),
    canonical({ headers: [] }),
    canonical({ headers: { A: 1 } }),
    canonical({ name: 3 }),
    canonical({ url: null }),
    JSON.stringify({ mcpServers: {} }),
    JSON.stringify({ mcpServers: { a: {}, b: {} } }),
    JSON.stringify({ mcpServers: { a: { type: 'stdio', command: 'npx' } } }),
    JSON.stringify({
      mcpServers: { a: { type: 'http', url: saved.url, command: 'npx' } },
    }),
    JSON.stringify({
      mcpServers: { a: { type: 'http', url: saved.url } },
      ignored: true,
    }),
    '{"name":"Docs","transport":"http","url":"https://example.com","__proto__":{"bad":true}}',
  ])
    rejects(() => parseConnectorJson(value));
  assert.equal({}.bad, undefined);
});

test('empty and incomplete forms switch modes freely and validate only on save', () => {
  for (const draft of [
    connectorDraft(),
    valid({ url: '' }),
    valid({ timeout: 'invalid' }),
  ]) {
    const jsonView = changeConnectorMode('json', draft, '', 'form');
    assert.deepEqual(
      changeConnectorMode('form', draft, jsonView.json, 'form').draft,
      draft
    );
    assert.deepEqual(
      changeConnectorMode('form', draft, jsonView.json, 'json').draft,
      draft
    );
    rejects(() => validateConnectorDraft(draft));
    rejects(() => parseConnectorJson(jsonView.json));
  }
  assert.equal(
    JSON.parse(connectorDraftJson(valid({ timeout: 'invalid' }))).timeout,
    'invalid'
  );
});

test('unrepresentable JSON is preserved across mode switches without validation errors', () => {
  const draft = valid({
    headers: [{ name: 'Authorization', value: 'transient-secret' }],
  });
  const before = structuredClone(draft);
  for (const json of [
    '',
    '{',
    'null',
    '[]',
    '{"headers":{"Authorization":"transient-secret"},',
    canonical({ command: 'npx' }),
    canonical({ active: 'true' }),
    canonical({ headers: { Authorization: 42 } }),
    JSON.stringify({ mcpServers: { first: {}, second: {} } }),
  ]) {
    const form = changeConnectorMode('form', draft, json, 'json');
    assert.deepEqual(form.draft, draft);
    assert.equal(
      changeConnectorMode('json', form.draft, form.json, 'json').json,
      json
    );
    rejects(() => parseConnectorJson(json));
  }
  assert.deepEqual(draft, before);
});

test('JSON field conversion accepts unfinished values without granting valid submission', () => {
  for (const [patch, field, expected] of [
    [{ name: '' }, 'name', ''],
    [{ url: 'not-a-url' }, 'url', 'not-a-url'],
    [{ transport: 'stdio' }, 'transport', 'stdio'],
    [{ timeout: 999 }, 'timeout', '999'],
    [{ timeout: 'invalid' }, 'timeout', 'invalid'],
  ]) {
    const json = canonical(patch);
    const form = changeConnectorMode('form', connectorDraft(), json, 'json');
    assert.equal(form.draft[field], expected);
    assert.equal(
      changeConnectorMode('json', form.draft, form.json, 'json').json,
      json
    );
    rejects(() => parseConnectorJson(json));
  }
  const partial = changeConnectorMode(
    'form',
    valid(),
    '{"name":"New draft"}',
    'json'
  ).draft;
  assert.equal(partial.name, 'New draft');
  assert.equal(partial.url, '');
});

test('valid JSON, mcpServers input and retained secrets survive mode changes', () => {
  const json = JSON.stringify(
    {
      mcpServers: {
        Docs: {
          type: 'sse',
          url: saved.url,
          headers: { Authorization: null, 'X-Key': 'transient-secret' },
          active: false,
        },
      },
    },
    null,
    4
  );
  const form = changeConnectorMode('form', connectorDraft(saved), json, 'json');
  assert.equal(form.draft.transport, 'sse');
  assert.equal(form.draft.active, false);
  assert.deepEqual(form.draft.headers, [
    { name: 'Authorization', value: null },
    { name: 'X-Key', value: 'transient-secret' },
  ]);
  assert.equal(
    changeConnectorMode('json', form.draft, json, 'json').json,
    json
  );
  assert.deepEqual(parseConnectorJson(json, saved), form.draft);
  const edited = { ...form.draft, name: 'Edited in form' };
  const regenerated = changeConnectorMode('json', edited, json, 'form').json;
  assert.equal(JSON.parse(regenerated).name, edited.name);
  assert.equal(JSON.parse(regenerated).headers.Authorization, null);
});

test('invalid form drafts and duplicate header rows are not lost by switching views', () => {
  const draft = valid({
    timeout: 'invalid',
    headers: [
      { name: 'Authorization', value: 'transient-secret' },
      { name: 'Authorization', value: 'another-secret' },
    ],
  });
  const before = structuredClone(draft);
  const json = changeConnectorMode('json', draft, '', 'form').json;
  assert.deepEqual(
    changeConnectorMode('form', draft, json, 'form').draft,
    before
  );
  rejects(() => validateConnectorDraft(draft));
  assert.deepEqual(draft, before);
});

test('character extension roundtrip preserves unrelated data, skills and explicit empty tools', () => {
  const skillId = 'b'.repeat(32);
  const ext = JSON.stringify({
    unknown: { nested: ['keep', 1] },
    skills: { enabled: [skillId] },
    tools: { enabled: [], other: true },
    connectors: { enabled: [] },
  });
  const updated = setCharacterConnectorIds(ext, [id]);
  assert.deepEqual(parseCharacterConnectorExt(updated).enabled, [id]);
  assert.equal(parseCharacterConnectorExt(updated).toolFree, true);
  assert.deepEqual(parseCharacterSkillExt(updated).enabled, [skillId]);
  const changedSkills = setCharacterSkillIds(updated, []);
  assert.deepEqual(parseCharacterConnectorExt(changedSkills).enabled, [id]);
  assert.deepEqual(JSON.parse(changedSkills).unknown, { nested: ['keep', 1] });
  assert.deepEqual(JSON.parse(changedSkills).tools, {
    enabled: [],
    other: true,
  });
  assert.deepEqual(JSON.parse(setCharacterConnectorIds(updated, [])).skills, {
    enabled: [skillId],
  });
  assert.equal(parseCharacterConnectorExt(undefined).toolFree, false);
  assert.equal(
    parseCharacterConnectorExt('{"tools":{"enabled":["memory"]}}').toolFree,
    false
  );
  assert.deepEqual(parseCharacterConnectorExt('').enabled, []);
});

test('connector IDs must be exactly 32 lowercase hexadecimal characters', () => {
  for (const value of [id, '0123456789abcdef'.repeat(2), '0'.repeat(32)]) {
    assert.equal(isConnectorId(value), true);
    assert.deepEqual(
      parseCharacterConnectorExt(setCharacterConnectorIds(undefined, [value]))
        .enabled,
      [value]
    );
  }
  for (const value of [
    'A'.repeat(32),
    '0123456789ABCDEF'.repeat(2),
    'a'.repeat(31) + 'F',
    'g'.repeat(32),
    'z'.repeat(32),
    'a'.repeat(31),
    'a'.repeat(33),
    'a'.repeat(32) + '\n',
    'a'.repeat(32) + '\r',
    '',
    null,
    123,
  ]) {
    assert.equal(isConnectorId(value), false);
    rejects(() => setCharacterConnectorIds('{}', [value]));
    rejects(() =>
      parseCharacterConnectorExt(
        JSON.stringify({ connectors: { enabled: [value] } })
      )
    );
  }
});

test('character connector refs are limited, opaque IDs and malformed ext is not overwritten', () => {
  assert.equal(isConnectorId(id), true);
  const eight = Array.from({ length: 8 }, (_, index) => `${index}`.repeat(32));
  assert.deepEqual(
    parseCharacterConnectorExt(setCharacterConnectorIds(undefined, eight))
      .enabled,
    eight
  );
  for (const enabled of [
    [id, id],
    [...eight, 'z'.repeat(32)],
    [...eight, 'f'.repeat(32)],
    ['../owner-id'],
    ['short'],
    [null],
  ])
    rejects(() => setCharacterConnectorIds('{}', enabled));
  for (const raw of [
    '{',
    'null',
    '[]',
    'true',
    '{}x',
    '{"connectors":[]}',
    '{"connectors":{"enabled":"all"}}',
    '{"connectors":{"enabled":[],"allowlist":[]}}',
  ]) {
    rejects(() => parseCharacterConnectorExt(raw));
    rejects(() => setCharacterConnectorIds(raw, []));
  }
});

test('export includes the query only on explicit opt-in and never serializes IDs or credentials', () => {
  assert.equal(characterExportUrl(42), '/api/v2/character/export/42');
  assert.equal(characterExportUrl(42, false), '/api/v2/character/export/42');
  assert.equal(
    characterExportUrl(42, true),
    '/api/v2/character/export/42?includeConnectors=true'
  );
});

function mockCharacterDownload(t, { status = 200, disposition = null } = {}) {
  const events = [];
  const blob = new Blob(['archive']);
  const response = {
    ok: status >= 200 && status < 300,
    status,
    statusText: 'secret-status',
    headers: new Headers({
      'Content-Type': 'application/gzip',
      ...(disposition ? { 'Content-Disposition': disposition } : {}),
    }),
    blob: t.mock.fn(async () => blob),
    text: t.mock.fn(async () => 'secret-response'),
  };
  const anchor = {
    href: '',
    download: '',
    click: t.mock.fn(() => events.push('click')),
    remove: t.mock.fn(() => events.push('remove')),
  };
  const document = {
    createElement: t.mock.fn((tag) => {
      assert.equal(tag, 'a');
      return anchor;
    }),
    body: {
      appendChild: t.mock.fn((child) => {
        assert.equal(child, anchor);
        events.push('append');
      }),
    },
  };
  const originalDocument = Object.getOwnPropertyDescriptor(
    globalThis,
    'document'
  );
  Object.defineProperty(globalThis, 'document', {
    configurable: true,
    value: document,
  });
  t.after(() => {
    if (originalDocument)
      Object.defineProperty(globalThis, 'document', originalDocument);
    else delete globalThis.document;
  });
  const fetch = t.mock.method(globalThis, 'fetch', async () => response);
  const createObjectURL = t.mock.method(URL, 'createObjectURL', (value) => {
    assert.equal(value, blob);
    events.push('createURL');
    return 'blob:character-export';
  });
  const revokeObjectURL = t.mock.method(URL, 'revokeObjectURL', (value) => {
    assert.equal(value, 'blob:character-export');
    events.push('revokeURL');
  });
  return {
    events,
    blob,
    response,
    anchor,
    document,
    fetch,
    createObjectURL,
    revokeObjectURL,
  };
}

test('export waits for fetch and blob, downloads the filename and releases DOM and objectURL', async (t) => {
  const download = mockCharacterDownload(t, {
    disposition: 'attachment; filename="published-character.tar.gz"',
  });
  const responseReady = Promise.withResolvers();
  const blobReady = Promise.withResolvers();
  download.fetch.mock.mockImplementation(() => responseReady.promise);
  download.response.blob.mock.mockImplementation(() => blobReady.promise);
  let finished = false;
  const pending = exportCharacter(42).then(() => {
    finished = true;
  });
  assert.equal(finished, false);
  assert.deepEqual(download.events, []);
  responseReady.resolve(download.response);
  await Promise.resolve();
  assert.equal(download.response.blob.mock.callCount(), 1);
  assert.equal(finished, false);
  assert.deepEqual(download.events, []);
  blobReady.resolve(download.blob);
  await pending;
  assert.equal(finished, true);
  assert.deepEqual(download.fetch.mock.calls[0].arguments, [
    '/api/v2/character/export/42',
    { credentials: 'same-origin', redirect: 'manual' },
  ]);
  assert.equal(download.anchor.href, 'blob:character-export');
  assert.equal(download.anchor.download, 'published-character.tar.gz');
  assert.deepEqual(download.events, [
    'createURL',
    'append',
    'click',
    'remove',
    'revokeURL',
  ]);
  assert.equal(download.revokeObjectURL.mock.callCount(), 1);
});

test('export opts into connectors only when requested and uses a default archive name', async (t) => {
  const download = mockCharacterDownload(t);
  await exportCharacter(42, true);
  assert.equal(
    download.fetch.mock.calls[0].arguments[0],
    '/api/v2/character/export/42?includeConnectors=true'
  );
  assert.equal(download.anchor.download, 'character-42.tar.gz');
});

test('export accepts safe Content-Disposition names and rejects unsafe or malformed names', async (t) => {
  const fallback = 'character-42.tar.gz';
  for (const [disposition, expected] of [
    ['attachment; filename=plain.tar.gz', 'plain.tar.gz'],
    ['attachment; filename="semi;colon.tar.gz"', 'semi;colon.tar.gz'],
    [
      "attachment; filename=plain.tar.gz; filename*=UTF-8''%E8%A7%92%E8%89%B2.tar.gz",
      '角色.tar.gz',
    ],
    ["attachment; FILENAME*=utf-8'en'%E6%96%87%E6%A1%A3.tar.gz", '文档.tar.gz'],
    ["attachment; filename=plain.tar.gz; filename*=UTF-8''%zz", 'plain.tar.gz'],
    ["attachment; filename*=UTF-8''%zz", fallback],
    ['attachment; filename="../../secret.tar.gz"', fallback],
    ['attachment; filename="C:\\secret.tar.gz"', fallback],
    ["attachment; filename*=UTF-8''..%2Fsecret.tar.gz", fallback],
    ["attachment; filename*=UTF-8''bad%00.tar.gz", fallback],
    ["attachment; filename*=UTF-8''bad%0D%0A.tar.gz", fallback],
    ["attachment; filename*=UTF-8''bad%E2%80%AE.tar.gz", fallback],
    ['attachment; filename=".hidden.tar.gz"', fallback],
    ['attachment; filename="payload.html"', fallback],
    ['attachment; filename=""', fallback],
    [`attachment; filename="${'a'.repeat(241)}.tar.gz"`, fallback],
    [
      `attachment; filename*=UTF-8''${encodeURIComponent('文'.repeat(81))}.tar.gz`,
      fallback,
    ],
    ['attachment; note="ignore; filename=not-a-parameter.tar.gz"', fallback],
  ]) {
    await t.test(disposition, async (t) => {
      const download = mockCharacterDownload(t, { disposition });
      await exportCharacter(42);
      assert.equal(download.anchor.download, expected);
      assert.equal(download.revokeObjectURL.mock.callCount(), 1);
    });
  }
});

test('HTTP failures reject with safe status and message, without reading or downloading the error body', async (t) => {
  for (const status of [400, 403, 404, 500]) {
    await t.test(String(status), async (t) => {
      const download = mockCharacterDownload(t, { status });
      await assert.rejects(exportCharacter(42), (error) => {
        assert.equal(error.status, status);
        assert.equal(
          error.message,
          'Character export failed. Please try again.'
        );
        assert.equal(normalizeConnectorError(error).code, status);
        assert.doesNotMatch(String(error) + JSON.stringify(error), /secret/);
        return true;
      });
      assert.equal(download.response.blob.mock.callCount(), 0);
      assert.equal(download.response.text.mock.callCount(), 0);
      assert.equal(download.document.createElement.mock.callCount(), 0);
      assert.deepEqual(download.events, []);
    });
  }
});

test('export preserves 401 for the existing normalized authentication handler', async (t) => {
  const download = mockCharacterDownload(t, { status: 401 });
  await assert.rejects(exportCharacter(42), (error) => {
    assert.equal(error.status, 401);
    assert.equal(normalizeConnectorError(error).code, 401);
    assert.equal(error.message, 'Character export failed. Please try again.');
    return true;
  });
  assert.deepEqual(download.events, []);
});

test('login redirects never become successful archive downloads', async (t) => {
  for (const patch of [
    { type: 'opaqueredirect', status: 0, ok: false },
    { status: 302, ok: false },
    { redirected: true, status: 200, ok: true },
  ]) {
    await t.test(JSON.stringify(patch), async (t) => {
      const download = mockCharacterDownload(t);
      Object.assign(download.response, patch);
      download.response.headers.set('Content-Type', 'text/html');
      await assert.rejects(exportCharacter(42), (error) => {
        assert.equal(normalizeConnectorError(error).code, 401);
        return true;
      });
      assert.equal(download.response.blob.mock.callCount(), 0);
      assert.deepEqual(download.events, []);
    });
  }
});

test('successful non-archive responses are rejected before creating a download', async (t) => {
  for (const contentType of ['text/html', 'application/json', '']) {
    await t.test(contentType || 'missing MIME type', async (t) => {
      const download = mockCharacterDownload(t);
      download.response.headers.set('Content-Type', contentType);
      await assert.rejects(
        exportCharacter(42),
        (error) => error.status === 502
      );
      assert.equal(download.response.blob.mock.callCount(), 0);
      assert.deepEqual(download.events, []);
    });
  }
});

test('network and blob failures reject safely without starting a download', async (t) => {
  for (const phase of ['fetch', 'blob']) {
    await t.test(phase, async (t) => {
      const download = mockCharacterDownload(t);
      const mock = phase === 'fetch' ? download.fetch : download.response.blob;
      mock.mock.mockImplementation(async () => {
        throw new Error('secret-network-details');
      });
      await assert.rejects(exportCharacter(42), (error) => {
        assert.equal(error.status, phase === 'fetch' ? -1 : 200);
        assert.equal(
          error.message,
          'Character export failed. Please try again.'
        );
        assert.doesNotMatch(String(error) + JSON.stringify(error), /secret/);
        return true;
      });
      assert.equal(download.createObjectURL.mock.callCount(), 0);
      assert.deepEqual(download.events, []);
    });
  }
});

test('download failures still remove the anchor and revoke the objectURL', async (t) => {
  const download = mockCharacterDownload(t);
  download.anchor.click.mock.mockImplementation(() => {
    download.events.push('click');
    throw new Error('download blocked');
  });
  await assert.rejects(exportCharacter(42), /Character export failed/);
  assert.deepEqual(download.events, [
    'createURL',
    'append',
    'click',
    'remove',
    'revokeURL',
  ]);
  assert.equal(download.revokeObjectURL.mock.callCount(), 1);
});

test('the objectURL is revoked even if creating the anchor fails', async (t) => {
  const download = mockCharacterDownload(t);
  download.document.createElement.mock.mockImplementation(() => {
    throw new Error('DOM unavailable');
  });
  await assert.rejects(exportCharacter(42), /Character export failed/);
  assert.deepEqual(download.events, ['createURL', 'revokeURL']);
  assert.equal(download.revokeObjectURL.mock.callCount(), 1);
});

test('all server error codes are sanitized, translated at render time, and preserve 401', async () => {
  const i18n = i18next.createInstance();
  await i18n.init({
    lng: 'en',
    fallbackLng: 'en',
    defaultNS: 'connector',
    resources: { en: { connector: en }, zh: { connector: zh } },
  });
  for (const [errorCode, key] of Object.entries(connectorErrorKeys)) {
    const error = normalizeConnectorError({
      code: 422,
      body: JSON.stringify({ errorCode, message: 'secret-response' }),
      message: 'secret-request',
    });
    assert.equal(error.code, 422);
    assert.equal(error.messageKey, key);
    assert.equal(error.known, true);
    assert.doesNotMatch(JSON.stringify(error), /secret/);
    await i18n.changeLanguage('zh');
    assert.equal(i18n.t(error.messageKey), zh[key]);
    assert.notEqual(i18n.t(error.messageKey), key);
    await i18n.changeLanguage('en');
    assert.equal(i18n.t(error.messageKey), en[key]);
  }
  for (const reason of [
    { code: 401, body: 'secret', message: 'private-header' },
    { status: 401 },
    { body: { code: 401 } },
  ]) {
    const error = normalizeConnectorError(reason);
    assert.equal(error.code, 401);
    assert.equal(error.known, false);
    assert.doesNotMatch(JSON.stringify(error), /secret|private-header/);
  }
});

test('all connector UI and validation strings have matching en/zh/ta resources', async () => {
  assert.deepEqual(Object.keys(en).sort(), Object.keys(zh).sort());
  assert.deepEqual(Object.keys(en).sort(), Object.keys(ta).sort());
  const files = [
    'components/connector/ConnectorEditor.tsx',
    'components/connector/ConnectorDialog.tsx',
    'pages/connector/Connectors.tsx',
    'pages/connector/ConnectorInfo.tsx',
    'components/character/CharacterConnectorSelector.tsx',
    'components/character/CharacterExportDialog.tsx',
    'pages/character/Characters.tsx',
    'libs/connector_utils.ts',
  ];
  for (const file of files) {
    const source = await readFile(
      new URL(`../src/${file}`, import.meta.url),
      'utf8'
    );
    const ast = ts.createSourceFile(
      file,
      source,
      ts.ScriptTarget.Latest,
      true,
      file.endsWith('tsx') ? ts.ScriptKind.TSX : ts.ScriptKind.TS
    );
    assert.equal(ast.parseDiagnostics.length, 0, file);
    const visit = (node) => {
      if (
        ts.isCallExpression(node) &&
        ['t', 'invalid'].includes(node.expression.getText(ast)) &&
        node.arguments[0] &&
        ts.isStringLiteral(node.arguments[0])
      ) {
        const rawKey = node.arguments[0].text;
        if (
          file !== 'pages/character/Characters.tsx' ||
          rawKey.startsWith('connector:')
        ) {
          const key = rawKey.replace(/^connector:/, '');
          assert.ok(Object.hasOwn(en, key), `${file}: English ${key}`);
          assert.ok(Object.hasOwn(zh, key), `${file}: Chinese ${key}`);
          assert.ok(Object.hasOwn(ta, key), `${file}: Tamil ${key}`);
        }
      }
      ts.forEachChild(node, visit);
    };
    visit(ast);
  }
});

test('connector entry is desktop-only, CRUD/test use SDK, credentials are not persisted or logged', async () => {
  const read = (path) =>
    readFile(new URL(`../src/${path}`, import.meta.url), 'utf8');
  const sidebar = await read('components/ThinSidebar.tsx');
  assert.match(
    sidebar,
    /<ListItem disablePadding sx=\{\{ display: \{ xs: 'none', md: 'flex' \} \}\}>\s*<ItemTooltip title=\{t\('Connectors'\)\}>/
  );
  assert.doesNotMatch(
    await read('components/FooterSidebar.tsx'),
    /[Cc]onnector/
  );
  const dialog = await read('components/connector/ConnectorDialog.tsx');
  const detail = await read('pages/connector/ConnectorInfo.tsx');
  assert.match(dialog, /connectorApi\.updateConnector/);
  assert.match(dialog, /connectorApi\.createConnector/);
  assert.doesNotMatch(dialog, /testConnector/);
  assert.match(detail, /connectorApi\.testConnector\(id, record\.revision\)/);
  assert.match(detail, /connectorApi\.deleteConnector\(id, record\.revision\)/);
  assert.match(
    detail,
    /disabled=\{busy \|\| !record\.revision\}\s+onClick=\{\(\) => void run\('test'\)\}/
  );
  assert.match(detail, /\(result\.tools \?\? \[\]\)\.slice\(0, 32\)/);
  const catalogKey =
    'Tool names and descriptions are untrusted server text. Showing at most 32 tools and 1000 characters per description.';
  assert.ok(detail.includes(catalogKey));
  assert.equal(en[catalogKey], catalogKey);
  assert.match(zh[catalogKey], /32 个工具/);
  for (const source of [
    dialog,
    detail,
    await read('components/connector/ConnectorEditor.tsx'),
    await read('pages/connector/Connectors.tsx'),
  ]) {
    assert.doesNotMatch(
      source,
      /localStorage|sessionStorage|indexedDB|console\.|fetch\(|dangerouslySetInnerHTML|tools\/call/
    );
  }
  assert.match(detail, /tool\.description\?\.slice\(0, 1000\)/);
  assert.match(
    await read('components/character/CharacterExportDialog.tsx'),
    /useState\(false\)/
  );
});
