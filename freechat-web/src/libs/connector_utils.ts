import { ConnectorWriteDTOTransportEnum } from 'freechat-sdk';
import type { ConnectorDetailsDTO, ConnectorWriteDTO } from 'freechat-sdk';

export type ConnectorHeader = { name: string; value: string | null };
export type ConnectorDraft = {
  name: string;
  transport: string;
  url: string;
  timeout: string;
  active: boolean;
  headers: ConnectorHeader[];
};
type SavedConnector = Pick<
  ConnectorDetailsDTO,
  'url' | 'headerNames' | 'revision'
>;

export const connectorErrorKeys: Record<string, string> = {
  CONNECTOR_INVALID_CONFIGURATION:
    'Check the connector configuration, URL and headers.',
  CONNECTOR_NOT_FOUND: 'This connector is no longer available.',
  CONNECTOR_ACCESS_DENIED: 'You do not have access to this private connector.',
  CONNECTOR_VERSION_CONFLICT:
    'This connector changed on the server. Cancel and reload before saving or testing again.',
  CONNECTOR_LIMIT_EXCEEDED:
    'Connector limits exceeded: at most 32 connectors, 16 headers and 16 KiB of headers.',
  CONNECTOR_UNAVAILABLE:
    'The remote server is unavailable or rejected the connection. Review the URL and credentials.',
  CONNECTOR_UNSAFE_ADDRESS:
    'The destination is blocked for safety. Use a public remote server, not a local or private address.',
  CONNECTOR_TIMEOUT:
    'The connection timed out. Check the remote server or adjust the timeout.',
};

function isObject(value: unknown): value is Record<string, unknown> {
  return value !== null && typeof value === 'object' && !Array.isArray(value);
}

function errorObject(value: unknown): Record<string, unknown> {
  if (typeof value === 'string') {
    try {
      return errorObject(JSON.parse(value));
    } catch {
      return {};
    }
  }
  return isObject(value) ? value : {};
}

export class ConnectorValidationError extends Error {
  readonly messageKey: string;
  constructor(messageKey: string) {
    super(messageKey);
    this.messageKey = messageKey;
  }
}

// Do not forward raw SDK errors: request bodies may contain credentials.
export function normalizeConnectorError(reason: unknown): {
  code: number;
  messageKey: string;
  known: boolean;
} {
  if (reason instanceof ConnectorValidationError) {
    return { code: -1, messageKey: reason.messageKey, known: true };
  }
  const outer = errorObject(reason);
  const body = errorObject(outer.body);
  const errorCode = body.errorCode ?? outer.errorCode;
  const known =
    typeof errorCode === 'string' &&
    Object.prototype.hasOwnProperty.call(connectorErrorKeys, errorCode);
  return {
    code: Number(outer.code ?? outer.status ?? body.code) || -1,
    messageKey: known
      ? connectorErrorKeys[errorCode as string]
      : 'The connector request failed. Please try again.',
    known,
  };
}

function invalid(key: string): never {
  throw new ConnectorValidationError(key);
}

export function connectorDraft(record?: ConnectorDetailsDTO): ConnectorDraft {
  return {
    name: record?.name ?? '',
    transport: record?.transport ?? 'http',
    url: record?.url ?? '',
    timeout: record?.timeout === undefined ? '' : String(record.timeout),
    active: record?.active ?? true,
    headers: Array.from(record?.headerNames ?? []).map((name) => ({
      name,
      value: null,
    })),
  };
}

function hasUnpairedSurrogate(raw: string): boolean {
  return Array.from(raw).some((ch) => {
    const codePoint = ch.codePointAt(0)!;
    return codePoint >= 0xd800 && codePoint <= 0xdfff;
  });
}

export function connectorUrl(raw: string): URL {
  if (
    raw.length > 2048 ||
    hasUnpairedSurrogate(raw) ||
    !/^https?:\/\//i.test(raw) ||
    /[\s\\?#\p{Cc}]/u.test(raw) ||
    /%(?![0-9a-f]{2})/i.test(raw)
  ) {
    return invalid(
      'Use an HTTP or HTTPS URL without credentials, query parameters or fragments.'
    );
  }
  try {
    const authority = raw.slice(raw.indexOf('://') + 3).split('/')[0];
    const url = new URL(raw);
    if (
      !authority ||
      /[@%]/.test(authority) ||
      url.username ||
      url.password ||
      !url.hostname ||
      url.port === '0' ||
      authority.endsWith(':')
    )
      throw new Error();
    if (
      !url.hostname.startsWith('[') &&
      !url.hostname
        .replace(/\.$/, '')
        .split('.')
        .every((label) => /^[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?$/i.test(label))
    )
      throw new Error();
    return url;
  } catch {
    return invalid(
      'Use an HTTP or HTTPS URL without credentials, query parameters or fragments.'
    );
  }
}

export function connectorOriginChanged(
  url: string,
  saved?: SavedConnector
): boolean {
  if (!saved?.url) return false;
  try {
    return connectorUrl(url).origin !== connectorUrl(saved.url).origin;
  } catch {
    return false;
  }
}

const forbiddenHeaders = new Set([
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
  'expect',
  'via',
  'http2-settings',
]);

export function validateConnectorDraft(
  draft: ConnectorDraft,
  saved?: SavedConnector
): ConnectorWriteDTO {
  if (
    !draft.name.trim() ||
    draft.name.length > 64 ||
    hasUnpairedSurrogate(draft.name) ||
    /[\p{Cc}\p{Cf}]/u.test(draft.name)
  ) {
    invalid('Enter a name of 1 to 64 characters without control characters.');
  }
  if (draft.transport !== 'http' && draft.transport !== 'sse')
    invalid('Only HTTP and SSE transports are supported.');
  const url = connectorUrl(draft.url);
  let timeout: number | undefined;
  if (draft.timeout !== '') {
    timeout = Number(draft.timeout);
    if (
      !/^\d+$/.test(draft.timeout) ||
      !Number.isInteger(timeout) ||
      timeout < 1000 ||
      timeout > 120000
    ) {
      invalid(
        'Timeout must be a whole number from 1000 to 120000 milliseconds, or left empty.'
      );
    }
  }
  if (typeof draft.active !== 'boolean')
    invalid('Active must be true or false.');
  if (draft.headers.length > 16)
    invalid(connectorErrorKeys.CONNECTOR_LIMIT_EXCEEDED);
  if (draft.headers.length && url.protocol !== 'https:')
    invalid('Configured headers require HTTPS.');
  const names = new Set<string>();
  const entries: [string, string][] = [];
  const keepHeaderNames: string[] = [];
  let bytes = 0;
  const encoder = new TextEncoder();
  for (const header of draft.headers) {
    const name = header.name.toLowerCase();
    if (
      !header.name ||
      /[^!#$%&'*+.^_`|~0-9a-z-]/i.test(header.name) ||
      header.name.length > 128 ||
      // The generated SDK would lose this exact key in its object map.
      header.name === '__proto__' ||
      forbiddenHeaders.has(name) ||
      name.startsWith('proxy-') ||
      name.startsWith('x-forwarded-') ||
      name.startsWith('mcp-') ||
      names.has(name)
    ) {
      invalid(
        'Use unique valid header names. Transport and routing headers are not allowed.'
      );
    }
    names.add(name);
    bytes += encoder.encode(header.name).length;
    if (header.value === null) {
      if (!saved?.headerNames?.includes(header.name))
        invalid(
          'Only an existing saved header can use null to keep its value.'
        );
      if (connectorOriginChanged(draft.url, saved))
        invalid(
          'The URL origin changed. Replace or delete every saved header before saving.'
        );
      keepHeaderNames.push(header.name);
    } else {
      if (typeof header.value !== 'string' || /[^\x20-\x7e]/.test(header.value))
        invalid(
          'Header values must contain only printable ASCII characters (spaces allowed, no tabs or newlines).'
        );
      bytes += encoder.encode(header.value).length;
      entries.push([header.name, header.value]);
    }
  }
  // The server also counts retained secret values, which are never returned.
  if (bytes > 16 * 1024) invalid(connectorErrorKeys.CONNECTOR_LIMIT_EXCEEDED);
  return {
    name: draft.name,
    transport:
      draft.transport === 'http'
        ? ConnectorWriteDTOTransportEnum.Http
        : ConnectorWriteDTOTransportEnum.Sse,
    url: draft.url,
    timeout,
    active: draft.active,
    headers: Object.fromEntries(entries),
    keepHeaderNames,
    ...(saved?.revision ? { expectedRevision: saved.revision } : {}),
  };
}

function connectorJsonValue(text: string): Record<string, unknown> {
  let value: unknown;
  try {
    value = JSON.parse(text);
  } catch {
    return invalid('Invalid JSON. Enter one connector object.');
  }
  if (!isObject(value)) invalid('Invalid JSON. Enter one connector object.');
  if (Object.prototype.hasOwnProperty.call(value, 'mcpServers')) {
    if (
      Object.keys(value).length !== 1 ||
      !isObject(value.mcpServers) ||
      Object.keys(value.mcpServers).length !== 1
    ) {
      invalid(
        'mcpServers must contain exactly one connector. Bulk import is not supported.'
      );
    }
    const [name, server] = Object.entries(value.mcpServers)[0];
    if (
      !isObject(server) ||
      Object.keys(server).some(
        (key) => !['type', 'url', 'timeout', 'headers', 'active'].includes(key)
      )
    ) {
      invalid(
        'Unknown JSON fields are not supported. STDIO and commands are not supported.'
      );
    }
    value = {
      name,
      transport: server.type,
      url: server.url,
      timeout: server.timeout,
      headers: server.headers,
      active: server.active,
    };
  }
  if (!isObject(value)) invalid('Invalid JSON. Enter one connector object.');
  if (
    Object.keys(value).some(
      (key) =>
        !['name', 'transport', 'url', 'timeout', 'headers', 'active'].includes(
          key
        )
    )
  ) {
    invalid(
      'Unknown JSON fields are not supported. STDIO and commands are not supported.'
    );
  }
  return value;
}

function connectorFormDraft(value: Record<string, unknown>): ConnectorDraft {
  if (
    (value.name !== undefined && typeof value.name !== 'string') ||
    (value.url !== undefined && typeof value.url !== 'string')
  )
    invalid('Name and URL must be strings.');
  if (value.transport !== undefined && typeof value.transport !== 'string')
    invalid('Only HTTP and SSE transports are supported.');
  if (
    value.timeout !== undefined &&
    typeof value.timeout !== 'number' &&
    typeof value.timeout !== 'string'
  )
    invalid(
      'Timeout must be a whole number from 1000 to 120000 milliseconds, or left empty.'
    );
  if (value.headers !== undefined && !isObject(value.headers))
    invalid(
      'Headers must be an object containing text values, or null for saved values.'
    );
  const headers = Object.entries(value.headers ?? {}).map(
    ([name, headerValue]) => {
      if (typeof headerValue !== 'string' && headerValue !== null)
        invalid(
          'Headers must be an object containing text values, or null for saved values.'
        );
      return { name, value: headerValue };
    }
  );
  if (value.active !== undefined && typeof value.active !== 'boolean')
    invalid('Active must be true or false.');
  return {
    name: value.name ?? '',
    transport: value.transport ?? 'http',
    url: value.url ?? '',
    timeout: value.timeout === undefined ? '' : String(value.timeout),
    active: value.active === undefined ? true : value.active,
    headers,
  };
}

export function parseConnectorJson(
  text: string,
  saved?: SavedConnector
): ConnectorDraft {
  const value = connectorJsonValue(text);
  if (typeof value.name !== 'string' || typeof value.url !== 'string')
    invalid('Name and URL must be strings.');
  if (value.transport !== 'http' && value.transport !== 'sse')
    invalid('Only HTTP and SSE transports are supported.');
  if (value.timeout !== undefined && typeof value.timeout !== 'number')
    invalid(
      'Timeout must be a whole number from 1000 to 120000 milliseconds, or left empty.'
    );
  const draft = connectorFormDraft(value);
  validateConnectorDraft(draft, saved);
  return draft;
}

export function connectorDraftJson(draft: ConnectorDraft): string {
  return JSON.stringify(
    {
      name: draft.name,
      transport: draft.transport,
      url: draft.url,
      ...(draft.timeout === ''
        ? {}
        : {
            timeout:
              /^\d+$/.test(draft.timeout) &&
              Number.isSafeInteger(Number(draft.timeout))
                ? Number(draft.timeout)
                : draft.timeout,
          }),
      headers: Object.fromEntries(
        draft.headers.map(({ name, value }) => [name, value])
      ),
      active: draft.active,
    },
    null,
    2
  );
}

export function changeConnectorMode(
  mode: 'form' | 'json',
  draft: ConnectorDraft,
  json: string,
  source: 'form' | 'json'
): { draft: ConnectorDraft; json: string } {
  if (source === 'form') return { draft, json: connectorDraftJson(draft) };
  if (mode === 'form') {
    try {
      return { draft: connectorFormDraft(connectorJsonValue(json)), json };
    } catch {
      // Preserve JSON that cannot be represented in the form until Save or an explicit edit.
      return { draft, json };
    }
  }
  return { draft, json };
}

export function isConnectorId(value: unknown): value is string {
  return (
    typeof value === 'string' &&
    value.length === 32 &&
    /^[0-9a-f]{32}$/.test(value)
  );
}

export function parseCharacterConnectorExt(raw: unknown): {
  ext: Record<string, unknown>;
  enabled: string[];
  toolFree: boolean;
} {
  let ext: unknown;
  try {
    ext =
      raw === undefined ||
      raw === null ||
      (typeof raw === 'string' && !raw.trim())
        ? {}
        : typeof raw === 'string'
          ? JSON.parse(raw)
          : null;
  } catch {
    return invalid(
      'Character extension data is malformed. Saving is disabled to avoid overwriting it.'
    );
  }
  if (!isObject(ext))
    invalid(
      'Character extension data is malformed. Saving is disabled to avoid overwriting it.'
    );
  const connectors = ext.connectors;
  if (
    connectors !== undefined &&
    connectors !== null &&
    (!isObject(connectors) ||
      Object.keys(connectors).some((key) => key !== 'enabled'))
  ) {
    invalid(
      'Character extension data is malformed. Saving is disabled to avoid overwriting it.'
    );
  }
  const enabled = isObject(connectors) ? connectors.enabled : undefined;
  if (
    enabled !== undefined &&
    (!Array.isArray(enabled) ||
      enabled.length > 8 ||
      new Set(enabled).size !== enabled.length ||
      !enabled.every(isConnectorId))
  ) {
    invalid('Select at most 8 unique connector IDs.');
  }
  const tools = ext.tools;
  return {
    ext,
    enabled: (enabled as string[] | undefined) ?? [],
    toolFree:
      isObject(tools) &&
      Array.isArray(tools.enabled) &&
      tools.enabled.length === 0,
  };
}

export function setCharacterConnectorIds(
  raw: string | undefined,
  enabled: string[]
): string {
  const { ext } = parseCharacterConnectorExt(raw);
  const result = JSON.stringify({ ...ext, connectors: { enabled } });
  parseCharacterConnectorExt(result);
  return result;
}
