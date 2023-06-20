package com.likaitong.chatgptweb.entity.ResponseEntity;

import lombok.Data;

/**
 * @author LKT
 * @create 2023-06-06 11:16
 */


public class SuccessResponse extends ResponseInfo {
    public SuccessResponse(){
        super(200,"OK");
    }
}
