package com.github.liushidai.img_server.model;

import com.github.liushidai.img_server.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.io.Serializable;
import java.util.List;

@Entity
public class Album extends BaseEntity implements Serializable {
    /**
     * 相册名称
     */
    @Column(nullable = false, length = 20)
    public String albumName;
    /**
     * 相册所属用户
     */
    @ManyToOne
    public User user;
    /**
     * 相册中的照片
     */
    @OneToMany(mappedBy = "album")
    public List<Image> images;
}
