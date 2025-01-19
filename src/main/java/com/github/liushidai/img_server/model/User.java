package com.github.liushidai.img_server.model;

import com.github.liushidai.img_server.base.BaseEntity;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

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
    /**
     * 通过用户名查询
     *
     * @param username username
     * @return Uni<User>
     */

    public static Uni<User> findByUsername(String username) {
        return find("username", username).firstResult();
    }


}
