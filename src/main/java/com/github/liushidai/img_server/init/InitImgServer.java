package com.github.liushidai.img_server.init;

import com.github.liushidai.img_server.model.UserGroup;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

@Startup
@Singleton
public class InitImgServer {

    @PostConstruct
    public void initialize() {
        Log.info("------------- [start] Init Img Server [start] -------------");
        initDefaultUserGroup();
        Log.info("------------- [end] Init Img Server [end] -------------");

    }

    /**
     * 初始化用户组
     */
    private void initDefaultUserGroup() {
        Log.info("------------- init DefaultUserGroup -------------");
        Uni<Long> count = UserGroup.count("select count(id) from UserGroup where userGroupType = 'ADMIN'");
        count.onItem().transformToUni(e->{
            if (e == 0) {
                UserGroup userGroup = new UserGroup();
                userGroup.uploadLimitSizeType = UserGroup.UploadCapacityType.TB;
                userGroup.uploadLimitSize = -1;
                userGroup.uploadLimitCount = -1;
                userGroup.userGroupType = UserGroup.UserGroupType.ADMIN;
                userGroup.groupName = "Admin";
                return UserGroup.persist(userGroup);
            }else if (e>1){
                throw new RuntimeException("admin group count is greater than 1 !");
            }
            return null;
        }).subscribe();
        UserGroup userGroup = new UserGroup();
    }
}
