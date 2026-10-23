package fun.freechat.service.skills;

@FunctionalInterface
public interface SkillReviewer {
    void review(SkillBundle bundle);
}
