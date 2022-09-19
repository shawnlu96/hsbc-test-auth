package com.sunstriker.models.domains;

import com.sunstriker.storage.Storage;
import com.sunstriker.utils.HashUtils;

public class User {
    private String username;
    private String passwordHash;

    // cannot be altered after created
    private final String pwdSalt;
    private String tokenSecret;

    public User(String username, String password) {
        this.username = username;
        // creating random salt/secret
        this.pwdSalt = HashUtils.getRandomString(6);
        this.tokenSecret = HashUtils.getRandomString(6);
        setPasswordHash(password);
    }

    public boolean save(){
        // use atomic method ConcurrentHashMap.putIfAbsent for thread safety
        User res = Storage.getInstance().userMap.putIfAbsent(username, this);
        return res == null;
    }

    // todo: delete all user-role records related to this user
    public void onRemoved(){

    }

    public boolean verifyPassword(String password){
        return hashPassword(password).equals(passwordHash);
    }

    // use HmacSHA256 with salt for password encryption
    private String hashPassword(String password){
        return HashUtils.hmacSha256(String.format("%s%s%s", username, password, pwdSalt), pwdSalt);
    }

    public void setPasswordHash(String password) {
        this.passwordHash = hashPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getPwdSalt() {
        return pwdSalt;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
