package fun.freechat.service.skills;

import static fun.freechat.service.skills.SkillException.Code.SKILL_INVALID_FORMAT;
import static fun.freechat.service.skills.SkillException.Code.SKILL_REVIEW_UNAVAILABLE;
import static fun.freechat.service.skills.SkillException.Code.SKILL_UNSAFE_INSTRUCTIONS;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.model.chat.ChatModel;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Dedicated management-only review; no chat context, tool service, user credentials or model-selected paths. */
@Component
public final class ModelSkillReviewer implements SkillReviewer, DisposableBean {
    public static final String BUILTIN_CLASSIFICATION = "MANAGEMENT";
    static final String SYSTEM_RESOURCE = "/skills/install-skill/SKILL.md";
    static final String SNAPSHOT_START = "BEGIN_UNTRUSTED_SKILL_SNAPSHOT_JSON\n";
    static final String SNAPSHOT_END = "\nEND_UNTRUSTED_SKILL_SNAPSHOT_JSON";
    static final int CONTEXT_TOKENS = SkillManagementModel.CONTEXT_TOKENS;
    static final int MAX_INPUT_BYTES = SkillManagementModel.MAX_INPUT_BYTES;
    static final int MAX_RESPONSE_BYTES = SkillManagementModel.MAX_RESPONSE_BYTES;
    static final int MAX_OUTPUT_TOKENS = SkillManagementModel.MAX_OUTPUT_TOKENS;
    static final int MAX_CONCURRENT_REVIEWS = SkillManagementModel.MAX_CONCURRENT_CALLS;

    private final SkillManagementModel managementModel;
    private final boolean ownsModel;
    private final String instructions;

    @Autowired
    ModelSkillReviewer(SkillManagementModel managementModel) {
        this(managementModel, false);
    }

    public ModelSkillReviewer(String modelId, String apiKey, String baseUrl) {
        this(new SkillManagementModel(modelId, apiKey, baseUrl), true);
    }

    public ModelSkillReviewer(ChatModel model) {
        this(model, SkillManagementModel.DEADLINE);
    }

    ModelSkillReviewer(ChatModel model, Duration deadline) {
        this(new SkillManagementModel(model, deadline), true);
    }

    private ModelSkillReviewer(SkillManagementModel managementModel, boolean ownsModel) {
        this.managementModel = managementModel;
        this.ownsModel = ownsModel;
        this.instructions = loadInstructions();
    }

    @Override
    public void requireAvailable() {
        managementModel.requireAvailable(SKILL_REVIEW_UNAVAILABLE);
        if (instructions == null) throw new SkillException(SKILL_REVIEW_UNAVAILABLE);
    }

    @Override
    public void review(SkillBundle bundle) {
        long started = System.nanoTime();
        requireAvailable();
        JsonNode result = managementModel.decide(instructions, snapshot(bundle), started, SKILL_REVIEW_UNAVAILABLE);
        JsonNode allowed = result.get("allowed");
        if (allowed == null || !allowed.isBoolean()) {
            throw new SkillException(SKILL_REVIEW_UNAVAILABLE);
        }
        if (!allowed.booleanValue()) {
            throw new SkillException(SKILL_UNSAFE_INSTRUCTIONS);
        }
    }

    private static String snapshot(SkillBundle bundle) {
        if (bundle == null) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        SkillBundle checked = new SkillBundleValidator().validate(bundle.files());
        if (!checked.name().equals(bundle.name())
                || !checked.description().equals(bundle.description())
                || !checked.digest().equals(bundle.digest())) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("name", checked.name());
        snapshot.put("description", checked.description());
        snapshot.put("tags", List.of("user", "prompt-only"));
        snapshot.put("digest", checked.digest());
        snapshot.put("files", checked.files());
        return SNAPSHOT_START + SkillManagementModel.json(snapshot, SKILL_REVIEW_UNAVAILABLE) + SNAPSHOT_END;
    }

    private static String loadInstructions() {
        // Exact classpath resource only. It is not registered as a character skill or a tool.
        try (InputStream stream = ModelSkillReviewer.class.getResourceAsStream(SYSTEM_RESOURCE)) {
            if (stream == null) {
                return null;
            }
            byte[] bytes = stream.readNBytes(8193);
            if (bytes.length == 0 || bytes.length > 8192) {
                return null;
            }
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void destroy() {
        if (ownsModel) {
            managementModel.destroy();
        }
    }
}
