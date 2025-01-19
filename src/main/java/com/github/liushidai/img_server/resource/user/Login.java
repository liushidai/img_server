package com.github.liushidai.img_server.resource.user;

import jakarta.validation.constraints.NotEmpty;

public record Login(@NotEmpty String username, @NotEmpty String password) {
}
