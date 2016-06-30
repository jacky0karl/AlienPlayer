package com.jk.alienplayer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by junjie.qu on 6/22/2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseBean {
    @JsonProperty("code")
    private int code = 0;
    @JsonProperty("message")
    private String message = "";

    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }
}
