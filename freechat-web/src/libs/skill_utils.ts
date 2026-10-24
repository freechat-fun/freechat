import type { SkillDetailsDTO } from 'freechat-sdk';

const skillErrorMessages: Record<string, string> = {
  SKILL_INVALID_FORMAT:
    'The skill bundle is invalid. Include a root SKILL.md and avoid conflicting file paths.',
  SKILL_UNSUPPORTED_CONTENT:
    'The skill contains script or executable content, non-text data, or unsafe file paths.',
  SKILL_UNSAFE_INSTRUCTIONS:
    'The skill was rejected because its instructions may be unsafe.',
  SKILL_SOURCE_NOT_ALLOWED:
    'This source is not allowed. Use a URL on an operator-approved host.',
  SKILL_SOURCE_UNAVAILABLE:
    'The skill source is unavailable. Check the URL and try again.',
  SKILL_SOURCE_RATE_LIMITED:
    'The skill source rate limit has been reached. Try again later. For GitHub, ask an administrator to configure skills.sources.github-token.',
  SKILL_SOURCE_AMBIGUOUS:
    'Could not identify one skill. Provide a specific repository or skill-directory link.',
  SKILL_SOURCE_ANALYSIS_UNAVAILABLE:
    'Skill link analysis is temporarily unavailable. Please try again later.',
  SKILL_NOT_FOUND: 'This skill could not be found or is no longer available.',
  SKILL_VERSION_CONFLICT:
    'This skill has changed on the server. Keep a copy of your edits, then cancel and reload before trying again.',
  SKILL_DRAFT_EXPIRED:
    'This preview has expired. Prepare the skill again before confirming.',
  SKILL_LIMIT_EXCEEDED:
    'The skill exceeds a storage, file, or usage limit. Reduce its size or remove unused skills.',
  SKILL_REVIEW_UNAVAILABLE:
    'Skill review is temporarily unavailable. Nothing was saved; please try again later.',
  SKILL_MODEL_NOT_CONFIGURED:
    'The server-side skill management model is missing or invalid. Ask an administrator to configure skills.review.model-id, skills.review.base-url and, for cloud providers, skills.review.api-key, then restart the server.',
  SKILL_STORAGE_UNAVAILABLE:
    'Skill storage is temporarily unavailable. Please try again later.',
  SKILL_ACCESS_DENIED: 'You do not have permission to access this skill.',
};

function isObject(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
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

// Never forward SDK exception messages: they can contain headers and raw bodies.
export function normalizeSkillError(
  reason: unknown,
  translate: (key: string) => string
): { code: number; messageKey: string; message: string } {
  const outer = errorObject(reason);
  const body = errorObject(outer.body);
  const status = outer.code ?? outer.status ?? body.code;
  const code = Number(status) || -1;
  const errorCode = body.errorCode ?? outer.errorCode;
  const message =
    typeof errorCode === 'string' &&
    Object.prototype.hasOwnProperty.call(skillErrorMessages, errorCode)
      ? skillErrorMessages[errorCode]
      : 'The skill request failed. Please try again.';
  return { code, messageKey: message, message: translate(message) };
}

export function isSkillFileName(name: string): boolean {
  const parts = name.split('/');
  // Storage safety only, not an extension or naming-style policy.
  return (
    name !== '__proto__' &&
    name.length <= 128 &&
    parts.length <= 8 &&
    !/[\\:%<>"|?*\p{Cc}\p{Cf}\p{Cs}]/u.test(name) &&
    parts.every(
      (part) =>
        part !== '' &&
        new TextEncoder().encode(part).length <= 255 &&
        !part.startsWith('.') &&
        !/[. ]$/.test(part) &&
        !/^(con|prn|aux|nul|com[0-9]|lpt[0-9])(\.|$)/i.test(part)
    )
  );
}

function isSkippableHiddenSkillFile(name: string): boolean {
  // Malformed paths must still reach storage-path validation.
  if (name.includes('\\')) return false;
  const parts = name.split('/');
  return (
    parts.every((part) => part !== '' && part !== '.' && part !== '..') &&
    parts.some((part) => part.startsWith('.'))
  );
}

export async function readSkillFiles(
  files: File[]
): Promise<NonNullable<SkillDetailsDTO['files']>> {
  const visibleFiles = files.filter(
    (file) => !isSkippableHiddenSkillFile(file.name)
  );
  if (
    visibleFiles.length > 16 ||
    visibleFiles.some(
      (file) => file.size > (file.name === 'SKILL.md' ? 32 : 64) * 1024
    ) ||
    visibleFiles.reduce((total, file) => total + file.size, 0) > 256 * 1024
  ) {
    throw { errorCode: 'SKILL_LIMIT_EXCEEDED' };
  }
  const entries = await Promise.all(
    visibleFiles.map(async (file): Promise<[string, string]> => {
      if (!isSkillFileName(file.name)) {
        throw { errorCode: 'SKILL_UNSUPPORTED_CONTENT' };
      }
      // Decode text only; the server is authoritative for script-content review.
      try {
        const text = new TextDecoder('utf-8', {
          fatal: true,
          ignoreBOM: true,
        }).decode(await file.arrayBuffer());
        if (text.includes('\0')) {
          throw new Error();
        }
        return [file.name, text];
      } catch {
        throw { errorCode: 'SKILL_UNSUPPORTED_CONTENT' };
      }
    })
  );
  // Compare storage identities without changing the uploaded names.
  const canonicalNames = entries.map(([name]) =>
    name.normalize('NFC').toLowerCase()
  );
  if (
    new Set(canonicalNames).size !== entries.length ||
    canonicalNames.some((name) =>
      canonicalNames.some((other) => other.startsWith(`${name}/`))
    ) ||
    !entries.some(([name]) => name === 'SKILL.md')
  ) {
    throw { errorCode: 'SKILL_INVALID_FORMAT' };
  }
  return Object.fromEntries(entries);
}

export function parseCharacterSkillExt(raw: unknown): {
  ext: Record<string, unknown>;
  enabled: string[];
} {
  const ext: unknown =
    raw === undefined ||
    raw === null ||
    (typeof raw === 'string' && !raw.trim())
      ? {}
      : typeof raw === 'string'
        ? JSON.parse(raw)
        : null;
  if (!isObject(ext)) {
    throw new Error('Invalid character extension data');
  }
  const skills = ext.skills;
  if (skills === undefined || skills === null) {
    return { ext, enabled: [] };
  }
  if (
    !isObject(skills) ||
    Object.keys(skills).some((key) => key !== 'enabled')
  ) {
    throw new Error('Invalid character skills');
  }
  const enabled = skills.enabled;
  if (
    enabled !== undefined &&
    enabled !== null &&
    (!Array.isArray(enabled) ||
      enabled.length > 16 ||
      new Set(enabled).size !== enabled.length ||
      !enabled.every(
        (id) => typeof id === 'string' && /^[0-9a-f]{32}$/.test(id)
      ))
  ) {
    throw new Error('Invalid character skill selection');
  }
  return { ext, enabled: (enabled as string[] | undefined) ?? [] };
}

export function setCharacterSkillIds(
  raw: string | undefined,
  enabled: string[]
): string {
  const { ext } = parseCharacterSkillExt(raw);
  return JSON.stringify({
    ...ext,
    skills: { ...(ext.skills as Record<string, unknown> | undefined), enabled },
  });
}
