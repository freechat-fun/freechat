package fun.freechat.service.skills;

public final class SkillException extends RuntimeException {
    public enum Code {
        SKILL_INVALID_FORMAT,
        SKILL_UNSUPPORTED_CONTENT,
        SKILL_UNSAFE_INSTRUCTIONS,
        SKILL_SOURCE_NOT_ALLOWED,
        SKILL_SOURCE_UNAVAILABLE,
        SKILL_SOURCE_RATE_LIMITED,
        SKILL_SOURCE_AMBIGUOUS,
        SKILL_SOURCE_ANALYSIS_UNAVAILABLE,
        SKILL_NOT_FOUND,
        SKILL_VERSION_CONFLICT,
        SKILL_DRAFT_EXPIRED,
        SKILL_LIMIT_EXCEEDED,
        SKILL_REVIEW_UNAVAILABLE,
        SKILL_MODEL_NOT_CONFIGURED,
        SKILL_STORAGE_UNAVAILABLE,
        SKILL_ACCESS_DENIED
    }

    private final Code code;

    public SkillException(Code code) {
        super(code.name());
        this.code = code;
    }

    public Code code() {
        return code;
    }
}
