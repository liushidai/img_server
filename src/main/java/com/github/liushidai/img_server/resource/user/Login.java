package com.github.liushidai.img_server.resource.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record Login(@NotEmpty @Size(min = 4, max = 10) String username,
                    @NotEmpty @Size(min = 8, max = 60) String password) {
}
