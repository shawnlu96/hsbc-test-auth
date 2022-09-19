package com.sunstriker.models.responses;

import com.sunstriker.utils.json.JsonProperty;

public class OkResponse {
    @JsonProperty
    private final int code = 0;
    @JsonProperty
    private final String type = "success";
    @JsonProperty
    private final Object payload;
    public OkResponse(Object payload) {
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}
