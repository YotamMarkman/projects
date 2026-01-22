package unit_tests;

import src.ClientManager;
import java.util.ArrayList;

/**
 * Tests for the ClientManager class
 * Testing group management functionality
 * 
 * Note: We can't test client handlers without real sockets,
 * so we focus on testing the group management functionality
 */
public class ClientManagerTest {
    
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== ClientManager Tests ===\n");
        
        // Client tests that don't require handlers
        testIsOnlineForNonExistent();
        testGetHandlerForNonExistent();
        
        // Group tests (these don't need socket connections)
        testGroupCreation();
        testGroupMembers();
        testUserGroups();
        testUserInMultipleGroups();
        testNonExistentGroup();
        testUserInNoGroups();
        
        System.out.println("\n=== Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        
        System.exit(failed > 0 ? 1 : 0);
    }
    
    // Test that non-existent user shows as offline
    static void testIsOnlineForNonExistent() {
        System.out.print("Non-existent user is offline... ");
        
        if (!ClientManager.isOnline("doesNotExist")) {
            pass();
        } else {
            fail("Non-existent user should not be online");
        }
    }
    
    // Test that getting handler for non-existent user returns null
    static void testGetHandlerForNonExistent() {
        System.out.print("Get handler for non-existent user... ");
        
        if (ClientManager.getHandler("ghostUser") == null) {
            pass();
        } else {
            fail("Should return null for non-existent user");
        }
    }
    
    // Test group creation
    static void testGroupCreation() {
        System.out.print("Group creation... ");
        
        ArrayList<String> members = new ArrayList<>();
        members.add("alice");
        members.add("bob");
        members.add("charlie");
        
        ClientManager.createGroup("TestGroup", members);
        
        ArrayList<String> retrieved = ClientManager.getGroupMembers("TestGroup");
        
        if (retrieved != null && retrieved.size() == 3) {
            pass();
        } else {
            fail("Group not created properly");
        }
    }
    
    // Test getting group members
    static void testGroupMembers() {
        System.out.print("Get group members... ");
        
        ArrayList<String> members = new ArrayList<>();
        members.add("dave");
        members.add("eve");
        
        ClientManager.createGroup("SecretGroup", members);
        
        ArrayList<String> retrieved = ClientManager.getGroupMembers("SecretGroup");
        
        if (retrieved.contains("dave") && retrieved.contains("eve")) {
            pass();
        } else {
            fail("Members not found");
        }
    }
    
    // Test finding groups a user belongs to
    static void testUserGroups() {
        System.out.print("Get user's groups... ");
        
        ArrayList<String> group1 = new ArrayList<>();
        group1.add("frank");
        group1.add("grace");
        
        ClientManager.createGroup("FrankGroup1", group1);
        
        ArrayList<String> franksGroups = ClientManager.getUserGroups("frank");
        
        if (franksGroups.contains("FrankGroup1")) {
            pass();
        } else {
            fail("Frank should be in FrankGroup1");
        }
    }
    
    // Test user in multiple groups
    static void testUserInMultipleGroups() {
        System.out.print("User in multiple groups... ");
        
        ArrayList<String> groupA = new ArrayList<>();
        groupA.add("multiUser");
        groupA.add("other1");
        
        ArrayList<String> groupB = new ArrayList<>();
        groupB.add("multiUser");
        groupB.add("other2");
        
        ClientManager.createGroup("MultiGroupA", groupA);
        ClientManager.createGroup("MultiGroupB", groupB);
        
        ArrayList<String> userGroups = ClientManager.getUserGroups("multiUser");
        
        if (userGroups.contains("MultiGroupA") && userGroups.contains("MultiGroupB")) {
            pass();
        } else {
            fail("User should be in both groups");
        }
    }
    
    // Test non-existent group returns null
    static void testNonExistentGroup() {
        System.out.print("Get non-existent group... ");
        
        if (ClientManager.getGroupMembers("FakeGroup123") == null) {
            pass();
        } else {
            fail("Should return null for non-existent group");
        }
    }
    
    // Test user in no groups returns empty list
    static void testUserInNoGroups() {
        System.out.print("User in no groups... ");
        
        ArrayList<String> nobodyGroups = ClientManager.getUserGroups("nobodySpecial123");
        
        if (nobodyGroups.isEmpty()) {
            pass();
        } else {
            fail("Should return empty list");
        }
    }
    
    // Helpers
    static void pass() {
        System.out.println("PASS");
        passed++;
    }
    
    static void fail(String reason) {
        System.out.println("FAIL - " + reason);
        failed++;
    }
}
