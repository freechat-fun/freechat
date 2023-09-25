package fun.freechat.util;

import fun.freechat.service.organization.OrgService;
import fun.freechat.util.graph.Graph;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
// See: https://seaking-image-yun.oss-cn-hangzhou.aliyuncs.com/CAF0AF99-6820-4DEF-A64A-6A978B53252B.png
public class TestOrgUtils implements ApplicationContextAware {
    private static OrgService orgService;

    public static void addSubordinates(String activeUserId) {
        orgService.addSubordinates("11", List.of("20", "21", "22", "23"));
        orgService.addSubordinates("21", List.of("30", activeUserId));
        orgService.addSubordinates("22", List.of(activeUserId, "32"));
        orgService.addSubordinates(activeUserId, List.of("40", "41"));

        orgService.addSubordinates("41", List.of("22"));
    }

    public static void removeSubordinates(String activeUserId) {
        orgService.removeSubordinates("41", List.of("22"));

        orgService.removeSubordinates(activeUserId, List.of("40", "41"));
        orgService.removeSubordinates("22", List.of(activeUserId, "32"));
        orgService.removeSubordinates("21", List.of("30", activeUserId));
        orgService.removeSubordinates("11", List.of("20", "21", "22", "23"));
    }

    public static void addOwners(String activeUserId) {
        orgService.addOwners("40", List.of(activeUserId));
        orgService.addOwners("41", List.of(activeUserId));
        orgService.addOwners("30", List.of("21"));
        orgService.addOwners(activeUserId, List.of("21", "22"));
        orgService.addOwners("32", List.of("22"));
        orgService.addOwners("20", List.of("11"));
        orgService.addOwners("21", List.of("11"));
        orgService.addOwners("22", List.of("11"));
        orgService.addOwners("23", List.of("11"));

        orgService.addOwners("22", List.of("41"));
    }

    public static void removeOwners(String activeUserId) {
        orgService.removeOwners("22", List.of("41"));

        orgService.removeOwners("23", List.of("11"));
        orgService.removeOwners("22", List.of("11"));
        orgService.removeOwners("21", List.of("11"));
        orgService.removeOwners("20", List.of("11"));
        orgService.removeOwners("32", List.of("22"));
        orgService.removeOwners(activeUserId, List.of("21", "22"));
        orgService.removeOwners("30", List.of("21"));
        orgService.removeOwners("41", List.of(activeUserId));
        orgService.removeOwners("40", List.of(activeUserId));
    }

    public static void cleanTestTree() {
        orgService.removeSubordinatesTree("11");
    }

    public static void assertEquals(Graph<String> graph, String... expected) {
        assertTrue(CollectionUtils.isEqualCollection(List.of(expected), graph));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        orgService = applicationContext.getBean(OrgService.class);
    }
}
