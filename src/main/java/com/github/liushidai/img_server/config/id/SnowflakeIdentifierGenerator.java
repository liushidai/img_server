package com.github.liushidai.img_server.config.id;

import com.github.liushidai.img_server.util.SnowflakeIdUtil;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class SnowflakeIdentifierGenerator
        implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return SnowflakeIdUtil.generateNextId();
    }
}
