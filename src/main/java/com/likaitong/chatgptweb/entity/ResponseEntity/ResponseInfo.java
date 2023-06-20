package com.likaitong.chatgptweb.entity.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LKT
 * @create 2023-06-06 11:13
 */


public class ResponseInfo {
    protected int code;
    protected String responseMessage;


    public ResponseInfo(int code, String responseMessage) {
        this.code = code;
        this.responseMessage = responseMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
