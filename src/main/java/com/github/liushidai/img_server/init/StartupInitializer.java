package com.github.liushidai.img_server.init;

import com.github.liushidai.img_server.model.UserGroup;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Parameters;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;

import java.time.Duration;

@ApplicationScoped
public class StartupInitializer {

    private static final String DEFAULT_ADMIN_GROUP_NAME = "Default Admin";
    private static final String DEFAULT_COMMON_GROUP_NAME = "Default Common";
    private static final int UNLIMITED = -1;
    private static final int MAX_RETRIES = 3; // 最大重试次数
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2); // 重试间隔

    @Inject
    Mutiny.SessionFactory sessionFactory;
    // 注入 Vert.x，用于确保操作在事件循环上执行
    @Inject
    io.vertx.mutiny.core.Vertx vertx;

    void onStart(@Observes StartupEvent ev) {
        Log.info("---------- Starting application initialization ----------");

        // 同时初始化 ADMIN 与 COMMON 用户组
        Uni<Void> adminInit = initializeDefaultAdmin();
        Uni<Void> commonInit = initializeDefaultCommon();

        Uni.combine().all().unis(adminInit, commonInit).discardItems().subscribe().with(result -> Log.info("---------- Ending application initialization ----------"), throwable -> Log.error("---------- Initialization failed after retries: " + throwable.getMessage() + " ----------", throwable));

    }

    private Uni<Void> initializeDefaultAdmin() {
        return sessionFactory.withTransaction(session -> UserGroup.count("userGroupType = :type", Parameters.with("type", UserGroup.UserGroupType.ADMIN)).chain(count -> {
            if (count == 0) {
                Log.info("No ADMIN group found, creating default...");
                return createDefaultAdmin(session);
            }
            Log.infof("Found %d ADMIN group(s), skip creation.", count);
            return Uni.createFrom().voidItem();
        })).onFailure().retry().withBackOff(RETRY_DELAY).atMost(MAX_RETRIES).onFailure().invoke(e -> Log.errorf("Failed to initialize admin group: %s", e.getMessage()));
    }

    private Uni<Void> createDefaultAdmin(Mutiny.Session session) {
        UserGroup adminGroup = new UserGroup();
        adminGroup.groupName = DEFAULT_ADMIN_GROUP_NAME;
        adminGroup.userGroupType = UserGroup.UserGroupType.ADMIN;
        adminGroup.uploadLimitCount = UNLIMITED;
        adminGroup.uploadLimitSize = UNLIMITED;
        adminGroup.uploadLimitSizeType = UserGroup.UploadCapacityType.TB;

        Log.debugf("Persisting admin group: %s", adminGroup);

        return session.persist(adminGroup).chain(session::flush).onFailure().invoke(e -> Log.errorf("Failed to persist admin group: %s", e.getMessage()));
    }

    // 初始化默认 COMMON 用户组
    private Uni<Void> initializeDefaultCommon() {
        return sessionFactory.withTransaction(session -> UserGroup.count("userGroupType = :type", Parameters.with("type", UserGroup.UserGroupType.COMMON)).chain(count -> {
            if (count == 0) {
                Log.info("No COMMON group found, creating default common group...");
                return createDefaultCommon(session);
            }
            Log.infof("Found %d COMMON group(s), skipping creation.", count);
            return Uni.createFrom().voidItem();
        })).onFailure().retry().withBackOff(RETRY_DELAY).atMost(MAX_RETRIES).onFailure().invoke(e -> Log.errorf("Failed to initialize common group: %s", e.getMessage()));
    }

    private Uni<Void> createDefaultCommon(Mutiny.Session session) {
        UserGroup commonGroup = new UserGroup();
        commonGroup.groupName = DEFAULT_COMMON_GROUP_NAME;
        commonGroup.userGroupType = UserGroup.UserGroupType.COMMON;
        commonGroup.uploadLimitCount = 100;
        commonGroup.uploadLimitSize = 100;
        commonGroup.uploadLimitSizeType = UserGroup.UploadCapacityType.MB;

        Log.debugf("Persisting common group: %s", commonGroup);

        return session.persist(commonGroup).chain(session::flush).onFailure().invoke(e -> Log.errorf("Failed to persist common group: %s", e.getMessage()));
    }
}

