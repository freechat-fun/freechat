import type {
  ChatContentDTO,
  ChatMessageDTO,
  LlmResultDTO,
} from 'freechat-sdk';
import { fetchEventSource } from '@microsoft/fetch-event-source';

export type ChatRunPhase =
  | 'sending'
  | 'queued'
  | 'preparing'
  | 'waiting_model'
  | 'thinking'
  | 'responding'
  | 'tool_execution'
  | 'finalizing'
  | 'cancelling';

export type ChatRunStatus = ChatRunPhase | 'failed' | 'cancelled';
export type ChatRunError = {
  code: string;
  message: string;
  retry_after?: number;
};

type Envelope = {
  v: 1;
  event: string;
  run_id: string;
  session_id: string;
  timestamp: number;
  seq: number;
};

// Hermes-style FreeChat protocol, separate from the prompt SSE protocol.
export type ChatRunEvent = Envelope &
  (
    | { event: 'run.queued'; phase: 'queued' }
    | { event: 'run.started'; phase: 'preparing' }
    | {
        event: 'run.status';
        phase: Exclude<ChatRunPhase, 'sending' | 'queued' | 'preparing'>;
      }
    | { event: 'message.delta' | 'reasoning.delta'; delta: string }
    | {
        event: 'tool.started' | 'tool.completed';
        tool: string;
        tool_call_id: string;
        error?: unknown;
      }
    | {
        event: 'run.completed';
        result: LlmResultDTO;
        completed: true;
        partial: false;
      }
    | {
        event: 'run.failed';
        error: ChatRunError;
        completed: false;
        partial: boolean;
      }
    | {
        event: 'run.cancelled';
        completed: false;
        partial: boolean;
        interrupted: true;
      }
  );

export type ChatRunSnapshot = {
  phase: ChatRunPhase;
  text: string;
  thinking: string;
  result: LlmResultDTO;
};

export type ChatRunOutcome = {
  status: 'completed' | 'failed' | 'cancelled';
  result: LlmResultDTO;
  partial: boolean;
  error?: ChatRunError;
};

export const chatRunStatusKeys: Record<ChatRunStatus, string> = {
  sending: 'Sending your message',
  queued: 'Your request is queued',
  preparing: 'Preparing your request',
  waiting_model: 'Waiting for the model',
  thinking: 'Thinking about your message',
  responding: 'Writing a response',
  tool_execution: 'Processing your request',
  finalizing: 'Finishing the response',
  cancelling: 'Cancelling your request',
  failed: 'The request failed',
  cancelled: 'The request was cancelled',
};

export const chatRunErrorKeys = {
  CHAT_STREAM_INVALID: 'The response stream was invalid. Please try again.',
  CHAT_STREAM_EOF: 'The response was interrupted. Please try again.',
  CHAT_STREAM_CONNECTION: 'The connection failed. Please try again.',
  CHAT_MEMORY_PREPARING:
    'Conversation memory is still being prepared. Please retry your message.',
  HTTP_401: 'Please sign in again to continue chatting.',
  HTTP_403: 'You do not have permission to send this message.',
  HTTP_429:
    'Quota has been used up, you can use your API key to continue chatting.',
};

export function chatRunErrorKey(error?: ChatRunError): string {
  return (
    chatRunErrorKeys[error?.code as keyof typeof chatRunErrorKeys] ??
    'The request failed. Please try again.'
  );
}

export class ChatRunStreamError extends Error {
  code: string;

  constructor(code: keyof typeof chatRunErrorKeys) {
    super(chatRunErrorKeys[code]);
    this.code = code;
  }
}

const events = new Set([
  'run.queued',
  'run.started',
  'run.status',
  'message.delta',
  'reasoning.delta',
  'tool.started',
  'tool.completed',
  'run.completed',
  'run.failed',
  'run.cancelled',
]);
const phases = new Set([
  'waiting_model',
  'thinking',
  'responding',
  'tool_execution',
  'finalizing',
  'cancelling',
]);

function resultWithText(
  text: string,
  thinking: string,
  result?: LlmResultDTO
): LlmResultDTO {
  const message = result?.message;
  const contents =
    message?.contents?.filter((content) => !!content.content?.trim()) ?? [];
  const hasText = contents.some((content) => content.type === 'text');
  const fallback = result?.text?.trim() ? result.text : text;
  return {
    ...result,
    message: {
      ...message,
      role: message?.role ?? 'assistant',
      contents:
        hasText || !fallback
          ? contents
          : [...contents, { type: 'text', content: fallback }],
      thinking: message?.thinking || thinking || undefined,
    },
  };
}

export function createChatRun(sessionId: string) {
  let runId: string | undefined;
  let seq = -1;
  let outcome: ChatRunOutcome | undefined;
  let snapshot: ChatRunSnapshot = {
    phase: 'sending',
    text: '',
    thinking: '',
    result: resultWithText('', ''),
  };

  const invalid = () => {
    throw new ChatRunStreamError('CHAT_STREAM_INVALID');
  };

  return {
    get snapshot() {
      return snapshot;
    },
    get outcome() {
      return outcome;
    },
    accept(frame: { event: string; data: string }): boolean {
      if (outcome || !events.has(frame.event)) return false;
      let value: ChatRunEvent;
      try {
        value = JSON.parse(frame.data);
      } catch {
        return invalid();
      }
      if (
        !value ||
        value.v !== 1 ||
        value.event !== frame.event ||
        typeof value.run_id !== 'string' ||
        !value.run_id ||
        value.session_id !== sessionId ||
        !Number.isFinite(value.timestamp) ||
        !Number.isSafeInteger(value.seq) ||
        value.seq < 0 ||
        (runId !== undefined && runId !== value.run_id) ||
        value.seq <= seq
      ) {
        return invalid();
      }
      switch (value.event) {
        case 'run.queued':
          if (value.phase !== 'queued') return invalid();
          snapshot = { ...snapshot, phase: value.phase };
          break;
        case 'run.started':
          if (value.phase !== 'preparing') return invalid();
          snapshot = { ...snapshot, phase: value.phase };
          break;
        case 'run.status':
          if (!phases.has(value.phase)) return invalid();
          snapshot = { ...snapshot, phase: value.phase };
          break;
        case 'message.delta':
        case 'reasoning.delta': {
          if (typeof value.delta !== 'string') return invalid();
          const field = value.event === 'message.delta' ? 'text' : 'thinking';
          snapshot = { ...snapshot, [field]: snapshot[field] + value.delta };
          snapshot.result = resultWithText(snapshot.text, snapshot.thinking);
          break;
        }
        case 'run.completed':
          if (
            value.completed !== true ||
            value.partial !== false ||
            !value.result ||
            typeof value.result !== 'object'
          )
            return invalid();
          snapshot = {
            ...snapshot,
            result: resultWithText(
              snapshot.text,
              snapshot.thinking,
              value.result
            ),
          };
          outcome = {
            status: 'completed',
            result: snapshot.result,
            partial: false,
          };
          break;
        case 'run.failed':
          if (
            value.completed !== false ||
            typeof value.partial !== 'boolean' ||
            typeof value.error?.code !== 'string' ||
            typeof value.error?.message !== 'string'
          )
            return invalid();
          outcome = {
            status: 'failed',
            result: snapshot.result,
            partial: value.partial,
            error: value.error,
          };
          break;
        case 'run.cancelled':
          if (
            value.completed !== false ||
            typeof value.partial !== 'boolean' ||
            value.interrupted !== true
          )
            return invalid();
          outcome = {
            status: 'cancelled',
            result: snapshot.result,
            partial: value.partial,
          };
          break;
        case 'tool.started':
        case 'tool.completed':
          break;
      }
      runId = value.run_id;
      seq = value.seq;
      return true;
    },
    close() {
      if (!outcome) throw new ChatRunStreamError('CHAT_STREAM_EOF');
    },
    fail(error: unknown): ChatRunOutcome {
      outcome ??= {
        status: 'failed',
        result: snapshot.result,
        partial: !!(snapshot.text || snapshot.thinking),
        error:
          error instanceof ChatRunStreamError
            ? { code: error.code, message: error.message }
            : {
                code: 'CHAT_STREAM_CONNECTION',
                message: chatRunErrorKeys.CHAT_STREAM_CONNECTION,
              },
      };
      return outcome;
    },
  };
}

export type ChatRunListenerOptions = {
  url: string;
  body?: string;
  sessionId: string;
  signal: AbortSignal;
  isCurrent: () => boolean;
  onUpdate: (snapshot: ChatRunSnapshot) => void;
  onFinish: (outcome: ChatRunOutcome) => void;
};

export async function listenChatRun(
  options: ChatRunListenerOptions,
  connect = fetchEventSource
): Promise<void> {
  const run = createChatRun(options.sessionId);
  const controller = new AbortController();
  const active = () => !options.signal.aborted && options.isCurrent();
  let finished = false;
  const finish = (outcome: ChatRunOutcome) => {
    if (!active() || finished) return;
    finished = true;
    options.onFinish(outcome);
    controller.abort();
  };
  const abort = () => controller.abort();
  if (!active()) return;
  options.signal.addEventListener('abort', abort, { once: true });
  options.onUpdate(run.snapshot);
  try {
    await connect(options.url, {
      method: options.body === undefined ? 'GET' : 'POST',
      headers: {
        Accept: 'text/event-stream',
        ...(options.body === undefined
          ? {}
          : { 'Content-Type': 'application/json' }),
      },
      body: options.body,
      signal: controller.signal,
      openWhenHidden: true,
      async onopen(response) {
        if (!active()) {
          controller.abort();
          return;
        }
        if (!response.ok) {
          if (response.status === 503) {
            const error = await response.json().catch(() => null);
            if (error?.errorCode === 'CHAT_MEMORY_PREPARING')
              throw new ChatRunStreamError('CHAT_MEMORY_PREPARING');
          }
          const code =
            `HTTP_${response.status}` as keyof typeof chatRunErrorKeys;
          throw new ChatRunStreamError(
            code in chatRunErrorKeys ? code : 'CHAT_STREAM_CONNECTION'
          );
        }
        if (
          !response.headers
            .get('content-type')
            ?.toLowerCase()
            .startsWith('text/event-stream')
        ) {
          throw new ChatRunStreamError('CHAT_STREAM_INVALID');
        }
      },
      onmessage(frame) {
        if (!active() || finished) return;
        if (!run.accept(frame)) return;
        options.onUpdate(run.snapshot);
        if (run.outcome) finish(run.outcome);
      },
      onclose() {
        if (active() && !finished) run.close();
      },
      onerror(error) {
        throw error;
      },
    });
    if (active() && !finished) {
      run.close();
    }
  } catch (error) {
    if (active() && !finished) finish(run.fail(error));
  } finally {
    controller.abort();
    options.signal.removeEventListener('abort', abort);
  }
}

type ReplyRenderer = (
  text: string,
  debug: boolean,
  noLinks: boolean,
  thinking?: string
) => string;

export function getChatRunContents(
  message: ChatMessageDTO | undefined,
  debugMode: boolean,
  render: ReplyRenderer
): ChatContentDTO[] {
  const contents =
    message?.contents?.filter((content) =>
      content.type === 'image'
        ? !!content.content?.trim()
        : !!render(
            content.content ?? '',
            debugMode,
            false,
            message.thinking
          ).trim()
    ) ?? [];
  if (
    debugMode &&
    !contents.some((content) => content.type !== 'image') &&
    render('', true, false, message?.thinking).trim()
  ) {
    return [...contents, { type: 'text', content: '' }];
  }
  return contents;
}

export function hasChatRunContent(
  message: ChatMessageDTO | undefined,
  debugMode: boolean,
  render: ReplyRenderer
): boolean {
  return getChatRunContents(message, debugMode, render).length > 0;
}
