package com.jk.alienplayer.model;


/**
 * Created by junjie.qu on 6/22/2016.
 */

public abstract class BaseBean {
    private int code = 0;
    private String message = "";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
