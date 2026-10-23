package fun.freechat.service.skills;

@FunctionalInterface
public interface SkillBinder {
    SkillInvocation bind();
}
