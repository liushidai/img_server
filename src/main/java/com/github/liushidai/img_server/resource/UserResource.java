package com.github.liushidai.img_server.resource;

import com.github.liushidai.img_server.model.User;
import com.github.liushidai.img_server.resource.user.Login;
import com.github.liushidai.img_server.resource.user.Token;
import com.github.liushidai.img_server.util.BcryptUtil;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Path("/manage")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @CacheName("token")
    Cache cache;

    @WithSession
    @Path("/login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> login(@Valid @NotNull Login login) {
        return User.findByUsername("admin").onItem().ifNotNull().transform(e -> {
            boolean checked = BcryptUtil.checkPassword(login.password(), e.password);
            if (checked) {
                return Response.ok(token(e)).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        }).onItem().ifNull().continueWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    /**
     * 获取或生成token
     *
     * @param user user
     * @return Token
     */
    private Token token(User user) {
        String token = this.getToken(user.id);
        if (token == null) {
            token = setToken(user.id);
        }
        return new Token(user.id, token);
    }

    /**
     * 生成uuid
     * @return uuid
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 从缓存中获取token
     *
     * @param id id
     * @return String token
     */
    private String getToken(Long id) {
        CompletableFuture<String> future = cache.as(CaffeineCache.class).getIfPresent(id);
        if (future == null) {
            return null;
        } else {
            return future.join();
        }
    }

    /**
     * 将token 存入缓存
     *
     * @param id id
     * @return token
     */
    private String setToken(Long id) {
        String uuid = generateUuid();
        cache.as(CaffeineCache.class).put(id, CompletableFuture.completedFuture(uuid));
        return uuid;
    }
}
