package com.github.liushidai.img_server.base;

import com.github.liushidai.img_server.config.id.SnowflakeId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity extends PanacheEntityBase {
    @Id
    @SnowflakeId
    @Column(name = "id", length = 15)
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = NumberDeserializers.LongDeserializer.class)
    public Long id;
    /**
     * 创建时间
     */
    @CreationTimestamp
    public LocalDateTime createTime;
    /**
     * 修改时间
     */
    @UpdateTimestamp
    public LocalDateTime updateTime;
}
