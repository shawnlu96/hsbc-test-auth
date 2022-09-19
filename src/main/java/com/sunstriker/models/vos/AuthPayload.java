package com.sunstriker.models.vos;

import com.sunstriker.utils.json.JsonProperty;

public class AuthPayload {
    @JsonProperty
    private String access_token;
    @JsonProperty
    private long expire_at;

    public AuthPayload(String access_token, long expire_at) {
        this.access_token = access_token;
        this.expire_at = expire_at;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpire_at() {
        return expire_at;
    }

    public void setExpire_at(long expire_at) {
        this.expire_at = expire_at;
    }
}
