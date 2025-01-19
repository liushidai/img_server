package com.github.liushidai.img_server.resource.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

public class Token implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    public Long id;
    public String token;

    public Token(Long id, String token) {
        this.id = id;
        this.token = token;
    }
}
