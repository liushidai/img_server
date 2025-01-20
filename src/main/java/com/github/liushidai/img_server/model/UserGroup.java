package com.github.liushidai.img_server.model;

import com.github.liushidai.img_server.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.io.Serializable;
import java.util.List;

@Entity
public class UserGroup extends BaseEntity implements Serializable {
    /**
     * 用户组名称
     */
    @Column(nullable = false, length = 20)
    public String groupName;
    /**
     * 用户组类型
     */
    public UserGroupType userGroupType;
    /**
     * 上传数量限制
     */
    public int uploadLimitCount;
    /**
     * 上传容量限制
     */
    public long uploadLimitSize;
    /**
     * 上传容量限制
     */
    public UploadCapacityType uploadLimitSizeType;
    /**
     * 组中的用户
     */
    @OneToMany(mappedBy = "userGroup")
    public List<User> users;


    public UserGroup() {

    }

    // 枚举定义上传容量类型
    public enum UploadCapacityType {
        MB, GB, TB
    }

    // 用户组类型
    public enum UserGroupType {
        ADMIN, COMMON
    }

}
