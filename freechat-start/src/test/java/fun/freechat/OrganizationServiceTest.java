package fun.freechat;

import fun.freechat.service.organization.OrgService;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestOrgUtils;
import fun.freechat.util.graph.Graph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("local")
@TestPropertySource(properties = "APP_HOME=${TMPDIR}")
@SuppressWarnings("unused")
public class OrganizationServiceTest {
    @Autowired
    private OrgService orgService;

    @Test
    public void testOwners() {
        String userId = "31";
        TestOrgUtils.addOwners(userId);
        TestCommonUtils.waitAWhile();
        try {
            Graph<String> owners = orgService.getOwners(userId, false);
            System.out.println("Owners DAG of " + userId + " :");
            System.out.println(owners.toDot());
            TestOrgUtils.assertEquals(owners, userId, "21", "22", "11");

            owners = orgService.getOwners(userId, true);
            System.out.println("Owners DIG of " + userId + " :");
            System.out.println(owners.toDot());
            TestOrgUtils.assertEquals(owners, userId, "21", "22", "11", "41");
        } finally {
            TestOrgUtils.removeOwners(userId);
        }

    }

    @Test
    public void testSubordinates() {
        String userId = "31";
        TestOrgUtils.addSubordinates(userId);
        TestCommonUtils.waitAWhile();
        try {
            Graph<String> subordinates = orgService.getSubordinates(userId, false);
            System.out.println("Subordinates DAG of " + userId + " :");
            System.out.println(subordinates.toDot());
            TestOrgUtils.assertEquals(subordinates, userId, "40", "41");

            subordinates = orgService.getSubordinates(userId, true);
            System.out.println("Subordinates DIG of " + userId + " :");
            System.out.println(subordinates.toDot());
            TestOrgUtils.assertEquals(subordinates, userId, "40", "41", "22", "32");
        } finally {
            TestOrgUtils.removeSubordinates(userId);
        }

    }
}
