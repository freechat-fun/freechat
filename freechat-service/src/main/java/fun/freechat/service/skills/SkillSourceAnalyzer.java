package fun.freechat.service.skills;

import java.util.List;

@FunctionalInterface
public interface SkillSourceAnalyzer {
    int select(String input, List<String> candidates);
}
