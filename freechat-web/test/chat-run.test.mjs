import assert from 'node:assert/strict';
import { Buffer } from 'node:buffer';
import { readFile } from 'node:fs/promises';
import test from 'node:test';
import ts from 'typescript';
import * as React from 'react';
import { createRequire } from 'node:module';
import { renderToStaticMarkup } from 'react-dom/server';
import {
  chatRunErrorKeys,
  chatRunStatusKeys,
  createChatRun,
  getChatRunContents,
  hasChatRunContent,
  listenChatRun,
} from '../src/libs/chat_run.ts';

const source = await readFile(
  new URL('../src/libs/chat_utils.ts', import.meta.url),
  'utf8'
);
const ast = ts.createSourceFile(
  'chat_utils.ts',
  source,
  ts.ScriptTarget.Latest,
  true
);
const replySource = ast.statements
  .find(
    (node) =>
      ts.isFunctionDeclaration(node) && node.name.text === 'getSenderReply'
  )
  .getText(ast);
const { getSenderReply } = await import(
  `data:text/javascript;base64,${Buffer.from(
    ts.transpileModule(replySource, {
      compilerOptions: {
        target: ts.ScriptTarget.ES2020,
        module: ts.ModuleKind.ESNext,
      },
    }).outputText
  ).toString('base64')}`
);
const visible = (run, debug = false) =>
  hasChatRunContent(run.snapshot.result.message, debug, getSenderReply);
const frame = (event, seq, fields = {}) => ({
  event,
  data: JSON.stringify({
    v: 1,
    event,
    run_id: 'run-1',
    session_id: 'chat-1',
    timestamp: 1700000000,
    seq,
    ...fields,
  }),
});
const completed = (seq, result = {}) =>
  frame('run.completed', seq, { result, completed: true, partial: false });
const textResult = (content) => ({
  message: { role: 'assistant', contents: [{ type: 'text', content }] },
});
const textOf = (result) =>
  result.message.contents
    .filter((content) => content.type === 'text')
    .map((content) => content.content)
    .join('\n');
const tick = () => new Promise((resolve) => queueMicrotask(resolve));

function transportHarness(connect, overrides = {}) {
  const updates = [];
  const outcomes = [];
  const controller = new AbortController();
  const promise = listenChatRun(
    {
      url: '/api/v2/chat/send/stream/chat-1',
      body: '{}',
      sessionId: 'chat-1',
      signal: controller.signal,
      isCurrent: () => true,
      onUpdate: (snapshot) => updates.push(snapshot),
      onFinish: (outcome) => outcomes.push(outcome),
      ...overrides,
    },
    connect
  );
  return { updates, outcomes, controller, promise };
}

test('control frames and tools never create a bubble; only server phases change status', () => {
  const run = createChatRun('chat-1');
  assert.equal(run.snapshot.phase, 'sending');
  assert.equal(visible(run), false);
  for (const item of [
    frame('run.queued', 1, { phase: 'queued' }),
    frame('run.started', 2, { phase: 'preparing' }),
    frame('run.status', 3, { phase: 'waiting_model' }),
    frame('tool.started', 4, {
      tool: 'secret-tool-name',
      tool_call_id: 'tool-1',
    }),
    frame('tool.completed', 5, {
      tool: 'secret-tool-name',
      tool_call_id: 'tool-1',
    }),
  ]) {
    run.accept(item);
    assert.equal(visible(run), false);
    assert.equal(visible(run, true), false);
  }
  assert.equal(run.snapshot.phase, 'waiting_model');
  assert.equal(chatRunStatusKeys.tool_execution, 'Processing your request.');
});

test('first visible text creates a bubble without inventing a phase', () => {
  const run = createChatRun('chat-1');
  run.accept(frame('run.status', 1, { phase: 'waiting_model' }));
  run.accept(frame('message.delta', 2, { delta: '  ' }));
  assert.equal(visible(run), false);
  run.accept(frame('message.delta', 3, { delta: 'Hello' }));
  assert.equal(visible(run), true);
  assert.equal(run.snapshot.phase, 'waiting_model');
  run.accept(frame('run.status', 4, { phase: 'responding' }));
  assert.equal(run.snapshot.phase, 'responding');
});

test('reasoning is hidden in normal chat and visible only in permitted debug display', () => {
  const run = createChatRun('chat-1');
  run.accept(frame('reasoning.delta', 1, { delta: 'Let me think.' }));
  assert.equal(visible(run), false);
  assert.equal(visible(run, true), true);
  run.accept(frame('message.delta', 2, { delta: 'Answer.' }));
  assert.equal(visible(run), true);
  assert.equal(textOf(run.snapshot.result), 'Answer.');
});

test('raw nonempty chunks hidden by think/markdown preprocessing do not create a row', () => {
  for (const content of [
    ' ',
    '""',
    '<think>unfinished',
    '<think>private</think>',
    '[link](https://unfinished',
    '![image](https://unfinished',
  ]) {
    const run = createChatRun('chat-1');
    run.accept(frame('message.delta', 1, { delta: content }));
    assert.equal(visible(run), false, content);
  }
  const run = createChatRun('chat-1');
  run.accept(frame('message.delta', 1, { delta: '<think>private' }));
  run.accept(frame('message.delta', 2, { delta: '</think>Visible' }));
  assert.equal(visible(run), true);
});

test('visibility and bubble contents share one projection, including debug-only reasoning', () => {
  const message = {
    thinking: 'Reasoning',
    contents: [{ type: 'text', content: '<think>unfinished' }],
  };
  assert.equal(hasChatRunContent(message, false, getSenderReply), false);
  const debug = getChatRunContents(message, true, getSenderReply);
  assert.equal(debug.length, 1);
  assert.equal(
    getSenderReply(debug[0].content, true, false, message.thinking),
    '> Reasoning'
  );
});

test('a first terminal result can supply final text or media without deltas', () => {
  for (const result of [
    textResult('Final answer'),
    { text: 'Final answer' },
    { message: { contents: [{ type: 'image', content: '/image.png' }] } },
  ]) {
    const run = createChatRun('chat-1');
    run.accept(completed(1, result));
    assert.equal(visible(run), true);
    assert.equal(run.outcome.status, 'completed');
    assert.doesNotThrow(() => run.close());
  }
});

test('empty final results retain streamed text; authoritative final content replaces rather than appends', () => {
  for (const [result, expected] of [
    [{}, 'Hello'],
    [textResult('Hello!'), 'Hello!'],
  ]) {
    const run = createChatRun('chat-1');
    run.accept(frame('message.delta', 1, { delta: 'Hello' }));
    run.accept(completed(2, result));
    assert.equal(textOf(run.outcome.result), expected);
    assert.equal(run.accept(completed(3, textResult('duplicate'))), false);
    assert.equal(
      run.accept(frame('message.delta', 4, { delta: 'late' })),
      false
    );
    assert.equal(textOf(run.outcome.result), expected);
  }
});

test('failed/cancelled partial bodies survive without being reported as success', () => {
  for (const status of ['failed', 'cancelled']) {
    const run = createChatRun('chat-1');
    run.accept(frame('message.delta', 1, { delta: 'Partial answer' }));
    run.accept(
      frame(`run.${status}`, 2, {
        completed: false,
        partial: true,
        interrupted: true,
        error: { code: 'UPSTREAM', message: 'unavailable', retry_after: 3 },
      })
    );
    assert.equal(run.outcome.status, status);
    assert.equal(run.outcome.partial, true);
    assert.equal(textOf(run.outcome.result), 'Partial answer');
    assert.doesNotThrow(() => run.close());
  }
});

test('finishReason inside a delta is not a terminal; EOF is an error with retained content', () => {
  const run = createChatRun('chat-1');
  run.accept(
    frame('message.delta', 1, { delta: 'Partial', finishReason: 'STOP' })
  );
  assert.equal(run.outcome, undefined);
  assert.throws(() => run.close(), { code: 'CHAT_STREAM_EOF' });
  try {
    run.close();
  } catch (error) {
    run.fail(error);
  }
  assert.equal(run.outcome.status, 'failed');
  assert.equal(run.outcome.error.code, 'CHAT_STREAM_EOF');
  assert.equal(textOf(run.outcome.result), 'Partial');
});

test('unknown frames, including invalid JSON, are ignored without output or binding run identity', () => {
  const run = createChatRun('chat-1');
  assert.equal(run.accept({ event: 'future.event', data: '{' }), false);
  assert.equal(
    run.accept(frame('future.event', 999, { run_id: 'other' })),
    false
  );
  assert.equal(visible(run), false);
  run.accept(completed(1, textResult('ok')));
  assert.equal(textOf(run.outcome.result), 'ok');
});

test('wrong session/run, duplicate or decreasing sequence and malformed known envelopes fail closed', () => {
  for (const patch of [
    { session_id: 'other' },
    { run_id: 'other' },
    { seq: 1 },
    { seq: 0 },
    { seq: 1.5 },
    { seq: '2' },
    { v: 2 },
    { timestamp: 'now' },
    { event: 'run.completed' },
  ]) {
    const run = createChatRun('chat-1');
    run.accept(frame('run.queued', 1, { phase: 'queued' }));
    assert.throws(
      () => run.accept(frame('message.delta', 2, { delta: 'bad', ...patch })),
      { code: 'CHAT_STREAM_INVALID' }
    );
    assert.equal(visible(run), false);
  }
  const run = createChatRun('chat-1');
  assert.throws(() => run.accept({ event: 'message.delta', data: '{' }), {
    code: 'CHAT_STREAM_INVALID',
  });
});

test('transport reports terminal exactly once, aborts and never reconnects on visibility changes', async () => {
  let calls = 0;
  let signal;
  const harness = transportHarness(async (_url, options) => {
    calls++;
    signal = options.signal;
    assert.equal(options.openWhenHidden, true);
    options.onmessage(frame('message.delta', 1, { delta: 'one' }));
    options.onmessage(completed(2));
    options.onmessage(completed(3));
    options.onclose();
  });
  await harness.promise;
  assert.equal(calls, 1);
  assert.equal(harness.outcomes.length, 1);
  assert.equal(signal.aborted, true);
  assert.equal(textOf(harness.outcomes[0].result), 'one');
});

test('EOF and network failures do not retry POST or finish successfully', async () => {
  for (const failure of ['eof', 'network']) {
    let calls = 0;
    const harness = transportHarness(async (_url, options) => {
      calls++;
      options.onmessage(frame('message.delta', 1, { delta: 'keep' }));
      if (failure === 'eof') options.onclose();
      else options.onerror(new Error('network failure'));
    });
    await harness.promise;
    assert.equal(calls, 1);
    assert.equal(harness.outcomes.length, 1);
    assert.equal(harness.outcomes[0].status, 'failed');
    assert.equal(textOf(harness.outcomes[0].result), 'keep');
    assert.equal(
      harness.outcomes[0].error.code,
      failure === 'eof' ? 'CHAT_STREAM_EOF' : 'CHAT_STREAM_CONNECTION'
    );
  }
});

test('HTTP preflight errors are failures, never bubbles, and do not retry', async () => {
  for (const status of [401, 403, 429]) {
    let calls = 0;
    const harness = transportHarness(async (_url, options) => {
      calls++;
      await options.onopen(new Response('', { status }));
    });
    await harness.promise;
    assert.equal(calls, 1);
    assert.equal(harness.outcomes[0].error.code, `HTTP_${status}`);
    assert.equal(
      hasChatRunContent(
        harness.outcomes[0].result.message,
        false,
        getSenderReply
      ),
      false
    );
  }
});

test('assistant GET uses the same protocol and keeps partial suggestion text on failure', async () => {
  const harness = transportHarness(
    async (url, options) => {
      assert.equal(
        url,
        '/api/v2/chat/send/stream/assistant/chat-1/assistant-1'
      );
      assert.equal(options.method, 'GET');
      options.onmessage(frame('reasoning.delta', 1, { delta: 'hidden' }));
      options.onmessage(frame('message.delta', 2, { delta: 'Suggestion' }));
      options.onclose();
    },
    {
      body: undefined,
      url: '/api/v2/chat/send/stream/assistant/chat-1/assistant-1',
    }
  );
  await harness.promise;
  assert.equal(harness.outcomes[0].status, 'failed');
  assert.equal(textOf(harness.outcomes[0].result), 'Suggestion');
  assert.equal(getSenderReply(textOf(harness.updates[1].result), false), '');
});

test('aborted and superseded callbacks cannot mutate the current chat', async () => {
  let oldOptions;
  let release;
  let current = true;
  const old = transportHarness(
    async (_url, options) => {
      oldOptions = options;
      await new Promise((resolve) => {
        release = resolve;
      });
    },
    { isCurrent: () => current }
  );
  current = false;
  const fresh = transportHarness(async (_url, options) => {
    options.onmessage(completed(1, textResult('new chat')));
  });
  await fresh.promise;
  oldOptions.onmessage(completed(1, textResult('old chat')));
  oldOptions.onclose();
  old.controller.abort();
  release();
  await old.promise;
  assert.equal(old.updates.length, 1);
  assert.equal(old.outcomes.length, 0);
  assert.equal(textOf(fresh.outcomes[0].result), 'new chat');
});

async function hookHarness() {
  const hookSource = await readFile(
    new URL('../src/libs/use_chat_run.ts', import.meta.url),
    'utf8'
  );
  const compiled = ts.transpileModule(hookSource, {
    compilerOptions: {
      target: ts.ScriptTarget.ES2020,
      module: ts.ModuleKind.CommonJS,
    },
  }).outputText;
  const refs = [];
  const requests = [];
  let cursor = 0;
  let dependencies;
  let cleanup;
  const react = {
    useRef: function mockRef(value) {
      const index = cursor++;
      return (refs[index] ??= { current: value });
    },
    useEffect: function mockEffect(effect, next) {
      if (
        dependencies &&
        next.every((value, index) => Object.is(value, dependencies[index]))
      )
        return;
      cleanup?.();
      dependencies = next;
      cleanup = effect();
    },
  };
  const exports = {};
  new Function('require', 'exports', compiled)(
    (name) =>
      name === 'react'
        ? react
        : {
            listenChatRun: async (options) => {
              if (!options.signal.aborted && options.isCurrent())
                requests.push(options);
            },
          },
    exports
  );
  return {
    requests,
    render(options) {
      cursor = 0;
      exports.useChatRun(options);
    },
    unmount() {
      cleanup?.();
    },
    strictReplay() {
      cleanup?.();
      dependencies = undefined;
    },
  };
}

test('hook callback rerenders do not resend and invoke latest callbacks', async () => {
  const hook = await hookHarness();
  let first = 0;
  let latest = 0;
  const options = {
    enabled: true,
    sessionId: 'chat-1',
    url: '/chat-1',
    body: '{}',
    onUpdate() {},
    onFinish() {
      first++;
    },
  };
  hook.render(options);
  await tick();
  hook.render({
    ...options,
    onFinish() {
      latest++;
    },
  });
  await tick();
  assert.equal(hook.requests.length, 1);
  hook.requests[0].onFinish({});
  assert.equal(first, 0);
  assert.equal(latest, 1);
  hook.unmount();
  assert.equal(hook.requests[0].signal.aborted, true);
});

test('StrictMode discarded effect sends no request; chat changes abort and invalidate old callbacks', async () => {
  const hook = await hookHarness();
  const options = {
    enabled: true,
    sessionId: 'chat-1',
    url: '/chat-1',
    body: '{}',
    onUpdate() {},
    onFinish() {},
  };
  hook.render(options);
  hook.strictReplay();
  hook.render(options);
  await tick();
  assert.equal(hook.requests.length, 1);
  const old = hook.requests[0];
  hook.render({ ...options, sessionId: 'chat-2', url: '/chat-2' });
  await tick();
  assert.equal(old.signal.aborted, true);
  assert.equal(old.isCurrent(), false);
  assert.equal(hook.requests.length, 2);
  hook.unmount();
  assert.equal(hook.requests[1].isCurrent(), false);
});

test('real fetch-event-source parses named SSE and stops on EOF without retrying', async () => {
  const previousWindow = globalThis.window;
  const previousDocument = globalThis.document;
  try {
    for (const terminal of [false, true]) {
      let calls = 0;
      const frames = [
        frame('run.queued', 1, { phase: 'queued' }),
        frame('message.delta', 2, { delta: 'body' }),
      ];
      if (terminal) frames.push(completed(3));
      globalThis.window = {
        fetch: async () => {
          calls++;
          return new Response(
            frames
              .map((item) => `event: ${item.event}\ndata: ${item.data}\n\n`)
              .join(''),
            { headers: { 'Content-Type': 'text/event-stream' } }
          );
        },
        setTimeout() {
          throw new Error('Retries must not be scheduled');
        },
        clearTimeout() {},
      };
      globalThis.document = { addEventListener() {}, removeEventListener() {} };
      const harness = transportHarness(undefined);
      await harness.promise;
      assert.equal(calls, 1);
      assert.equal(harness.outcomes.length, 1);
      assert.equal(
        harness.outcomes[0].status,
        terminal ? 'completed' : 'failed'
      );
      assert.equal(textOf(harness.outcomes[0].result), 'body');
    }
  } finally {
    globalThis.window = previousWindow;
    globalThis.document = previousDocument;
  }
});

async function paneHarness({ debug = false, permitted = false } = {}) {
  const paneSource = await readFile(
    new URL('../src/components/chat/MessagesPane.tsx', import.meta.url),
    'utf8'
  );
  const compiled = ts.transpileModule(paneSource, {
    compilerOptions: {
      target: ts.ScriptTarget.ES2020,
      module: ts.ModuleKind.CommonJS,
      jsx: ts.JsxEmit.ReactJSX,
    },
  }).outputText;
  const states = [];
  const refs = [];
  const requests = [];
  const successes = [];
  let stateIndex = 0;
  let refIndex = 0;
  let input;
  const react = {
    ...React,
    useState: function mockState(initial) {
      const index = stateIndex++;
      if (!(index in states)) states[index] = initial;
      return [
        states[index],
        (value) => {
          states[index] =
            typeof value === 'function' ? value(states[index]) : value;
        },
      ];
    },
    useRef: function mockRef(value) {
      const index = refIndex++;
      return (refs[index] ??= { current: value });
    },
    useEffect: function mockEffect() {},
    useCallback: function mockCallback(fn) {
      return fn;
    },
  };
  const element = (tag) =>
    function Element({ children }) {
      return React.createElement(tag, null, children);
    };
  const localRequire = createRequire(import.meta.url);
  const modules = {
    react,
    'react-i18next': {
      useTranslation: function mockTranslation() {
        return { t: (key) => key };
      },
    },
    '@mui/material': {
      Alert: element('aside'),
      Box: element('div'),
      Stack: element('section'),
      Button: element('button'),
      useTheme: function mockTheme() {
        return {
          palette: { mode: 'light', background: { paper: 'white' } },
        };
      },
    },
    '.': {
      AvatarWithStatus: () => React.createElement('i', { 'data-avatar': true }),
      ChatBubble: ({ record, variant }) =>
        React.createElement(
          'p',
          { 'data-bubble': variant },
          JSON.stringify(record.message)
        ),
      MessageInput: (props) => {
        input = props;
        return null;
      },
      MessagesPaneHeader: ({ runStatus }) =>
        React.createElement('header', null, runStatus),
    },
    'freechat-sdk': {
      ChatContentDTO: class {},
      ChatMessageDTO: class {},
      ChatMessageRecordDTO: class {},
    },
    '../../contexts': {
      useErrorMessageBusContext: function mockErrorContext() {
        return { handleError() {} };
      },
      useFreeChatApiContext: function mockApiContext() {
        return { serverUrl: '' };
      },
    },
    '../../libs/chat_utils': {
      getSenderReply,
      getSenderStatus: () => 'online',
      PROACTIVE_CHAT_PROMPT_EN: 'proactive',
      PROACTIVE_CHAT_PROMPT_ZH: 'proactive',
    },
    '../../libs/ui_utils': {},
    '../../libs/template_utils': {
      getMessageText: (message) =>
        message?.contents
          ?.filter((content) => content.type === 'text')
          .map((content) => content.content)
          .join('\n'),
    },
    '../../libs/chat_run': {
      hasChatRunContent,
      chatRunErrorKey: () => 'The response was interrupted. Please try again.',
    },
    '../../libs/use_chat_run': {
      useChatRun: function mockChatRun(options) {
        return requests.push(options);
      },
    },
  };
  const exports = {};
  new Function('require', 'exports', compiled)(
    (name) => modules[name] ?? localRequire(name),
    exports
  );
  const props = {
    session: { context: { chatId: 'chat-1' }, isDebugEnabled: permitted },
    defaultDebugMode: debug,
    onReceivedMessage: (result) => successes.push(result),
  };
  const render = () => {
    stateIndex = 0;
    refIndex = 0;
    const descriptor = Object.getOwnPropertyDescriptor(
      globalThis,
      'localStorage'
    );
    Object.defineProperty(globalThis, 'localStorage', {
      configurable: true,
      value: { getItem: () => null },
    });
    try {
      return renderToStaticMarkup(React.createElement(exports.default, props));
    } finally {
      if (descriptor)
        Object.defineProperty(globalThis, 'localStorage', descriptor);
      else delete globalThis.localStorage;
    }
  };
  return {
    render,
    successes,
    get request() {
      return requests.at(-1);
    },
    send() {
      render();
      input.setTextAreaValue('Hello');
      render();
      input.onSubmit();
      return render();
    },
  };
}

test('MessagesPane mounts the connection without a receive row, then commits body history once', async () => {
  const pane = await paneHarness();
  const sending = pane.send();
  assert.equal(pane.request.enabled, true);
  assert.doesNotMatch(sending, /data-bubble="received"|data-avatar/);
  assert.match(sending, /sending/);
  const run = createChatRun('chat-1');
  run.accept(frame('run.queued', 1, { phase: 'queued' }));
  pane.request.onUpdate(run.snapshot);
  assert.doesNotMatch(pane.render(), /data-bubble="received"|data-avatar/);
  run.accept(frame('message.delta', 2, { delta: 'Answer' }));
  pane.request.onUpdate(run.snapshot);
  assert.match(pane.render(), /data-bubble="received"/);
  run.accept(completed(3));
  pane.request.onFinish(run.outcome);
  const history = pane.render();
  assert.equal(pane.request.enabled, false);
  assert.equal((history.match(/data-bubble="received"/g) ?? []).length, 1);
  assert.match(history, /Answer/);
  assert.equal(pane.successes.length, 1);
  assert.doesNotMatch(history, /<header>sending|<header>queued/);
});

test('MessagesPane debug requires permission and normal thinking never creates avatars or rows', async () => {
  for (const [debug, permitted, expected] of [
    [false, true, false],
    [true, false, false],
    [true, true, true],
  ]) {
    const pane = await paneHarness({ debug, permitted });
    pane.send();
    const run = createChatRun('chat-1');
    run.accept(frame('reasoning.delta', 1, { delta: 'Private reasoning' }));
    pane.request.onUpdate(run.snapshot);
    const html = pane.render();
    assert.equal(html.includes('data-bubble="received"'), expected);
    assert.equal(html.includes('data-avatar'), expected);
  }
});

test('MessagesPane preserves partial history on interruption without success and uses an alert', async () => {
  const pane = await paneHarness();
  pane.send();
  const run = createChatRun('chat-1');
  run.accept(frame('message.delta', 1, { delta: 'Partial answer' }));
  pane.request.onFinish(run.fail(new Error('disconnected')));
  const html = pane.render();
  assert.match(html, /Partial answer/);
  assert.match(html, /<aside>/);
  assert.equal((html.match(/data-bubble="received"/g) ?? []).length, 1);
  assert.equal(pane.successes.length, 0);
  assert.equal(pane.request.enabled, false);
});

test('header uses one live status Chip for run phases and restores presence when idle', async () => {
  const source = await readFile(
    new URL('../src/components/chat/MessagesPaneHeader.tsx', import.meta.url),
    'utf8'
  );
  const compiled = ts.transpileModule(source, {
    compilerOptions: {
      target: ts.ScriptTarget.ES2020,
      module: ts.ModuleKind.CommonJS,
      jsx: ts.JsxEmit.ReactJSX,
    },
  }).outputText;
  const require = createRequire(import.meta.url);
  for (const locale of ['en', 'zh', 'ta']) {
    const translations = JSON.parse(
      await readFile(
        new URL(`../src/i18n/locales/${locale}/chat.json`, import.meta.url)
      )
    );
    const exports = {};
    new Function('require', 'exports', compiled)((name) => {
      if (name === 'react-i18next')
        return {
          useTranslation: function mockTranslation() {
            return { t: (key) => translations[key] ?? key };
          },
        };
      if (name === '../../libs/chat_run') return { chatRunStatusKeys };
      if (name === '../../libs/chat_utils')
        return {
          getSenderName: () => 'Emmy',
          getSenderStatus: (session) => session.senderStatus,
          getSenderStatusColor: () => 'success',
        };
      if (name === '..') return { FlexBox: require('@mui/material').Box };
      return require(name);
    }, exports);
    for (const runStatus of [undefined, ...Object.keys(chatRunStatusKeys)]) {
      const label =
        translations[runStatus ? chatRunStatusKeys[runStatus] : 'online'];
      const html = renderToStaticMarkup(
        React.createElement(exports.default, {
          session: { senderStatus: 'online', character: { name: 'Emily' } },
          runStatus,
        })
      );
      assert.equal((html.match(/role="status"/g) ?? []).length, 1);
      const status = html.match(/<[^>]+role="status"[^>]*>/)?.[0];
      assert.match(status, /MuiChip-root/);
      assert.match(status, /aria-live="polite"/);
      assert.ok(status.includes(`aria-label="${label}"`));
      assert.ok(status.includes(`title="${label}"`));
    }
  }
});

test('all request statuses and error sentences have en/zh/ta translations', async () => {
  for (const locale of ['en', 'zh', 'ta']) {
    const translations = JSON.parse(
      await readFile(
        new URL(`../src/i18n/locales/${locale}/chat.json`, import.meta.url)
      )
    );
    for (const key of [
      ...Object.values(chatRunStatusKeys),
      ...Object.values(chatRunErrorKeys),
      'The request failed. Please try again.',
      'Retry the request.',
    ]) {
      assert.equal(typeof translations[key], 'string', `${locale}: ${key}`);
      assert.ok(translations[key].length > 0);
    }
  }
});
