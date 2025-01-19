package com.github.liushidai.img_server.model;

import com.github.liushidai.img_server.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Image extends BaseEntity implements Serializable {
    /**
     * 上传用户
     */
    @ManyToOne
    public User user;
    /**
     * 路径
     */
    @Column(nullable = false, length = 9)
    public String imageUrl;
    /**
     * 路径前缀时间
     */
    public LocalDate localDate;

    /**
     * 图片类型
     */
    @Column(nullable = false, length = 5)
    public String imageType;
    /**
     * 图片大小
     */
    public long imageSize;
    /**
     * 所属相册
     */
    @ManyToOne
    public Album album;
}
