package fun.freechat;

import fun.freechat.service.organization.OrgService;
import fun.freechat.util.TestCommonUtils;
import fun.freechat.util.TestOrgUtils;
import fun.freechat.util.graph.Graph;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
public class OrganizationServiceIT extends AbstractIntegrationTest {
    @Autowired
    private OrgService orgService;

    @Test
    public void should_get_owners() {
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
    public void should_get_subordinates() {
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
