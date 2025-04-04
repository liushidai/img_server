package com.github.liushidai.img_server.model;

import com.github.liushidai.img_server.base.BaseEntity;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

import static com.github.liushidai.img_server.Constant.CACHE_NOT_FOUND;

@Entity
public class User extends BaseEntity implements Serializable {
    /**
     * 用户名
     */
    @Column(unique = true, nullable = false, length = 10)
    public String username;

    /**
     * 密码
     */
    @Column(nullable = false, length = 60)
    public String password;
    /**
     * 用户组
     */
    @ManyToOne
    public UserGroup userGroup;

    public User() {
    }

    public User(String username, String password, UserGroup userGroup) {
        this.username = username;
        this.password = password;
        this.userGroup = userGroup;
    }

    /**
     * 通过用户名查询
     *
     * @param username username
     * @return Uni<User>
     */
    @CacheResult(cacheName = "User")
    public static Uni<User> findByUsername(@CacheKey String username) {
        return find("username", username)
                .firstResult();
    }

    @CacheInvalidate(cacheName = "User")
    public Uni<Void> save() {
        return persist(this).onItem().invoke(() -> invalidateCache(this.username));
    }
    /**
     * 清除指定用户名的缓存项
     * 调用该方法时，Quarkus Cache 扩展会自动清理名为 "User" 的缓存中，对应的 @CacheKey
     *
     * @param username 用户名，即缓存KEY
     */
    @CacheInvalidate(cacheName = "User")
    @SuppressWarnings("unused")
    public static void invalidateCache(@CacheKey String username) {
        // 方法体可以为空，注解 实现缓存清除
    }
}
