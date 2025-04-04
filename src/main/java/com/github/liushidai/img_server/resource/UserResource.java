package com.github.liushidai.img_server.resource;

import com.github.liushidai.img_server.config.result.ResultUtil;
import com.github.liushidai.img_server.model.User;
import com.github.liushidai.img_server.model.UserGroup;
import com.github.liushidai.img_server.resource.user.Login;
import com.github.liushidai.img_server.resource.user.Register;
import com.github.liushidai.img_server.resource.user.Token;
import com.github.liushidai.img_server.util.BcryptUtil;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
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

@Path("/manage")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    ReactiveRedisDataSource tokenRedisDataSource;
    @WithSession
    @Path("/register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> register(@Valid @NotNull Register register) {
        // 首先调用 User.findByUsername 判断用户名是否存在
        return User.findByUsername(register.username()).onItem().ifNotNull().transform(user -> {
            // 用户名已存在
            return Response.status(Response.Status.CONFLICT).entity(ResultUtil.failed("Msg.User.register.username_already_exist")).build();
        }).onItem().ifNull().switchTo(() ->
                // 用户名不存在，继续校验 userGroup
                UserGroup.getUserGroupById(register.userGroupId()).onItem().ifNotNull().transformToUni(userGroup -> {
                    // userGroup 存在，保存用户
                    User newUser = new User(register.username(), BcryptUtil.encryptPassword(register.password()), userGroup);
                    return newUser.save().onItem().transform(v -> Response.ok(ResultUtil.success("User registered successfully")).build());
                }).onItem().ifNull().switchTo(() ->
                        // userGroup 不存在
                        Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND).entity(ResultUtil.failed("Msg.UserGroup.not_exists")).build())));
    }



    @WithSession
    @Path("/login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> login(@Valid @NotNull Login login) {
        return User.findByUsername(login.username()).onItem().ifNotNull().transform(e -> {
            boolean checked = BcryptUtil.checkPassword(login.password(), e.password);
            if (checked) {
                // 验证成功，获取或生成 token（返回 Uni<Token>）
                return Response.ok(ResultUtil.data(token(e))).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ResultUtil.failed("Msg.User.login.error")).build();
            }
        }).onItem().ifNull().continueWith(Response.status(Response.Status.UNAUTHORIZED).entity(ResultUtil.failed("Msg.User.login.error")).build());
    }

    /**
     * 获取或生成 token
     *
     * @param user 当前登录用户
     * @return 一个 Uni<Token> 对象，异步返回 token 信息
     */
    private Uni<Token> token(User user) {
        // Redis 中的 key 用用户 id 的字符串表示
        String key = "user_token:" + user.id.toString();
        // 尝试从 Redis 中获取 token
        return tokenRedisDataSource.value(String.class).get(key).onItem().transformToUni(existing -> {
            if (existing == null) {
                // 若不存在 token，则生成一个新的 token
                String newToken = generateUuid();
                // 写入 token
                return tokenRedisDataSource.value(String.class).set(key, newToken).onItem().transform(res -> new Token(user.id, newToken));
            } else {
                // 如果 token 已经存在，则直接返回
                return Uni.createFrom().item(new Token(user.id, existing));
            }
        });
    }

    /**
     * 生成 UUID 字符串
     * @return 一个新的 UUID 字符串
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
