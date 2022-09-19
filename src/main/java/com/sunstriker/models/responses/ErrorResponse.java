package com.sunstriker.models.responses;

import com.sunstriker.utils.json.JsonProperty;

public class ErrorResponse {
    @JsonProperty
    private final int code = 1;
    @JsonProperty
    private final String type = "error";
    @JsonProperty
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
