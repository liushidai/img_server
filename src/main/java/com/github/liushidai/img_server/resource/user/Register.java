package com.github.liushidai.img_server.resource.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record Register(@NotEmpty @Size(min = 4, max = 10) String username,
                       @NotEmpty @Size(min = 8, max = 60) @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$") String password,
                       @NotNull Long userGroupId) {
}
