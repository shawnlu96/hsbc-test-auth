package com.sunstriker;

import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.exceptions.UnauthorizedException;
import com.sunstriker.models.domains.User;
import com.sunstriker.services.AuthenticationService;
import com.sunstriker.services.UserRoleService;
import com.sunstriker.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthenticationServiceTest {

    AuthenticationService authenticationService = new AuthenticationService();
    UserRoleService userRoleService = new UserRoleService();

    final String username = "shawn", password = "123456";

    @BeforeEach
    public void cleanUp() {
        Storage.getInstance().userMap.clear();
        Storage.getInstance().roleMap.clear();
        Storage.getInstance().userRoles.clear();
    }

    @Test
    public void testTokenGenerateAndVerify() throws ForbiddenException {
        // create user first
        userRoleService.createUser(username, password);

        assertThrows(ForbiddenException.class, ()-> authenticationService.authenticate("fake_user", password), "Not throwing when username not existed.");
        assertThrows(ForbiddenException.class, ()-> authenticationService.authenticate(username, "wrong_pass"), "Not throwing when password is wrong.");

        String token = authenticationService.authenticate(username, password).getAccess_token();

        // wrong sign
        assertThrows(UnauthorizedException.class, ()-> authenticationService.verifyToken(token.substring(0, token.length()-4)), "Not throwing when sign mismatch.");
        // token expire test
        Calendar calendar = Calendar.getInstance();
        // add 4 hrs
        calendar.add(Calendar.HOUR_OF_DAY, 4);
        assertThrows(UnauthorizedException.class, ()-> authenticationService.verifyToken(token,calendar.getTime()), "Not throwing when sign mismatch.");

        assertDoesNotThrow(()->authenticationService.verifyToken(token));
    }

    @Test
    public void testInvalidateToken() throws ForbiddenException {
        // create user first
        userRoleService.createUser(username, password);
        String token = authenticationService.authenticate(username, password).getAccess_token();
        User user = assertDoesNotThrow(()->authenticationService.verifyToken(token));
        authenticationService.invalidateToken(user);
        assertThrows(UnauthorizedException.class, ()-> authenticationService.verifyToken(token), "Token not invalidated.");
    }
}
