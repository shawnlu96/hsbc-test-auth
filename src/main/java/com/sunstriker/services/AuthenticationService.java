package com.sunstriker.services;

import com.sunstriker.exceptions.ForbiddenException;
import com.sunstriker.exceptions.UnauthorizedException;
import com.sunstriker.models.domains.User;
import com.sunstriker.models.vos.AuthPayload;
import com.sunstriker.storage.Storage;
import com.sunstriker.utils.HashUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class AuthenticationService {

    public AuthPayload authenticate(String username, String password) throws ForbiddenException {
        User user = Storage.getInstance().userMap.getOrDefault(username, null);
        if(user == null || !user.verifyPassword(password)) throw new ForbiddenException("Invalid username or password.");

        // token generation
        StringBuilder tokenBuilder = new StringBuilder();
        // generate expire time
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        long expireAt = calendar.getTimeInMillis();
        // generate payload
        String plainPayload = String.format("{%s:%d}", user.getUsername(), expireAt);
        String encodedPayload = Base64.getEncoder().encodeToString(plainPayload.getBytes(StandardCharsets.UTF_8));
        // generate sign
        String sign = HashUtils.hmacSha256(plainPayload, user.getTokenSecret());
        // concatenate token
        tokenBuilder.append(encodedPayload).append('.').append(sign);

        return new AuthPayload(tokenBuilder.toString(), expireAt) ;
    }

    public User verifyToken(String token) throws UnauthorizedException {
        return verifyToken(token, new Date());
    }

    public User verifyToken(String token, Date timeToCompare) throws UnauthorizedException {
        try {
            String[] arr = token.split("\\.");
            String plainPayload = new String(Base64.getDecoder().decode(arr[0]));
            String[] infos = plainPayload.substring(1, plainPayload.length()-1).split(":");
            Date expireAt = new Date(Long.parseLong(infos[1]));
            // expiry check
            if(expireAt.after(timeToCompare)) {
                String username = infos[0];
                User user = Storage.getInstance().userMap.getOrDefault(username, null);
                if(user!=null){
                    String computedSign = HashUtils.hmacSha256(plainPayload, user.getTokenSecret());
                    if(computedSign.equals(arr[1])) return user;
                }
            }
            throw new UnauthorizedException();
        } catch (Exception e) {
            throw new UnauthorizedException();
        }

    }


    public void invalidateToken(User user) {
        // refresh user-specific token secret, so that all token generated before will no longer be valid
        user.refreshTokenSecret();
    }
}
