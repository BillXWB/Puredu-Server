package edu.pure.server.opedukg.payload;

import lombok.Getter;

@Getter
public class OpedukgResponse<T> {
    private int code;
    private String msg;
    private T data;
}
