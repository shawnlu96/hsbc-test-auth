package com.sunstriker;

import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.models.domains.User;
import com.sunstriker.models.domains.UserRole;
import com.sunstriker.services.UserRoleService;
import com.sunstriker.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


public class UserRoleServiceTest {
    UserRoleService userRoleService = new UserRoleService();

    @BeforeEach
    public void cleanUp() {
        Storage.getInstance().userMap.clear();
        Storage.getInstance().roleMap.clear();
        Storage.getInstance().userRoles.clear();
    }

    @Test
    public void testCreateUser() throws ForbiddenException {
        final String userName = "shawn", password = "123456";
        // first create
        userRoleService.createUser(userName, password);
        assertTrue(Storage.getInstance().userMap.containsKey(userName), "Failed creating.");
        User created = Storage.getInstance().userMap.get(userName);
        // verify password
        assertTrue(created.verifyPassword(password), "Password mismatch.");
        // duplicated create
        assertThrows(ForbiddenException.class, () -> userRoleService.createUser(userName, password), "Not throwing when duplicated creating.");
        assertThrows(ForbiddenException.class, () -> userRoleService.createUser(userName, "234567"), "Not throwing when duplicated creating.");
    }

    @Test
    public void testDeleteUser() throws ForbiddenException {
        final String userName = "shawn";
        assertThrows(ForbiddenException.class, () -> userRoleService.deleteUser(userName), "Not throwing user not found.");

        userRoleService.createUser(userName, "123456");
        userRoleService.deleteUser(userName);


        assertFalse(Storage.getInstance().userMap.containsKey(userName), "Failed deleting.");
    }

    @Test
    public void testCreateRole() throws ForbiddenException {
        final String roleName = "admin";
        // first create
        userRoleService.createRole(roleName);
        assertTrue(Storage.getInstance().roleMap.containsKey(roleName), "Failed creating.");
        // duplicated creating test
        assertThrows(ForbiddenException.class, ()->userRoleService.createRole(roleName), "Not throwing when duplicated creating.");
    }

    @Test
    public void testDeleteRole() throws ForbiddenException{
        final String roleName = "admin";
        assertThrows(ForbiddenException.class, () -> userRoleService.deleteRole(roleName), "Not throwing role not found.");

        userRoleService.createRole(roleName);
        userRoleService.deleteRole(roleName);
        assertFalse(Storage.getInstance().roleMap.containsKey(roleName), "Failed deleting.");
    }

    @Test
    public void testAddRoleToUser() throws ForbiddenException{
        final String roleName = "admin", username = "shawn";
        assertThrows(ForbiddenException.class, ()-> userRoleService.addUserRole(username, roleName),"Not throwing correctly when no such user.");
        userRoleService.createUser(username, "123456");
        assertThrows(ForbiddenException.class, ()-> userRoleService.addUserRole(username, roleName),"Not throwing correctly when no such role.");
        userRoleService.createRole(roleName);
        userRoleService.addUserRole(username, roleName);
        assertTrue(Storage.getInstance().userRoles.contains(new UserRole(username,roleName)), "Failed creating userRole.");
    }

    @Test
    public void testCheckRole() throws ForbiddenException {
        final  String roleName = "admin", username = "shawn";
        userRoleService.createUser(username, "123456");
        User user = Storage.getInstance().userMap.get(username);
        assertThrows(ForbiddenException.class, ()-> userRoleService.checkRole(user, roleName), "Not judging if role exists.");
        userRoleService.createRole(roleName);
        assertFalse(userRoleService.checkRole(user, roleName));
        userRoleService.addUserRole(username, roleName);
        assertTrue(userRoleService.checkRole(user, roleName));
    }

    @Test
    public void testGetAllRoles() throws ForbiddenException {
        final String username = "shawn", role1 = "admin", role2 = "guest";
        userRoleService.createUser(username, "123456");
        User user = Storage.getInstance().userMap.get(username);
        assertEquals(0, userRoleService.getAllRoles(user).length, "Incorrect role count result.");
        // add role1 to user
        userRoleService.createRole(role1);
        userRoleService.addUserRole(username, role1);
        assertArrayEquals(new String[]{role1}, userRoleService.getAllRoles(user), "Incorrect roles.");
        // add role2 to user
        userRoleService.createRole(role2);
        userRoleService.addUserRole(username, role2);
        String[] res = userRoleService.getAllRoles(user);
        // adjust order for comparison
        Arrays.sort(res);
        assertArrayEquals(new String[]{role1,role2}, res, "Incorrect roles.");
    }

}
