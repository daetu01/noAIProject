package com.no.ai.global.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Message {
    private StatusEnum status;
    private String message;
    private Object data;

    public Message() {
        this.status = StatusEnum.BAD_REQUEST;
        this.data = null;
        this.message = null;
    }
}
