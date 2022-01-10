package com.motaharinia.ms.captchaotp.config.caching;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class RedisObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    public RedisObjectMapper() {
        super();
        this.configure(MapperFeature.USE_ANNOTATIONS, false);
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.activateDefaultTyping(this.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        this.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public ObjectMapper copy() {
        return this.toBuilder().build();
    }
}
