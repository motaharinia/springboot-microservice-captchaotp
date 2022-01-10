package com.motaharinia.ms.captchaotp.config.security.oauth2.resource.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.motaharinia.msutility.custom.customjson.CustomObjectMapper;
import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import io.jsonwebtoken.lang.Assert;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;


public class CustomJacksonDeserializer<T> implements Deserializer<T> {
    private final Class<T> returnType;
    private final ObjectMapper objectMapper;

    public CustomJacksonDeserializer(Map<String, Class> claimTypeMap,ObjectMapper objectMapper) {
        this(objectMapper);
        Assert.notNull(claimTypeMap, "Claim type map cannot be null.");
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Object.class, new MappedTypeDeserializer(Collections.unmodifiableMap(claimTypeMap)));
        this.objectMapper.registerModule(module);
        this.objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public CustomJacksonDeserializer(ObjectMapper objectMapper) {
        this(objectMapper, (Class<T>) Object.class);
    }

    private CustomJacksonDeserializer(ObjectMapper objectMapper, Class<T> returnType) {
        Assert.notNull(objectMapper, "ObjectMapper cannot be null.");
        Assert.notNull(returnType, "Return type cannot be null.");
        this.objectMapper = objectMapper;
        this.returnType = returnType;
    }

    public T deserialize(byte[] bytes) throws DeserializationException {
        try {
            return this.readValue(bytes);
        } catch (IOException var4) {
            String msg = "Unable to deserialize bytes into a " + this.returnType.getName() + " instance: " + var4.getMessage();
            throw new DeserializationException(msg, var4);
        }
    }

    protected T readValue(byte[] bytes) throws IOException {
        return this.objectMapper.readValue(bytes, this.returnType);
    }

    private static class MappedTypeDeserializer extends UntypedObjectDeserializer {
        private final Map<String, Class> claimTypeMap;

        private MappedTypeDeserializer(Map<String, Class> claimTypeMap) {
            super((JavaType)null, (JavaType)null);
            this.claimTypeMap = claimTypeMap;
        }

        public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String name = parser.currentName();
            if (this.claimTypeMap != null && name != null && this.claimTypeMap.containsKey(name)) {
                Class type = (Class)this.claimTypeMap.get(name);
                return parser.readValueAsTree().traverse(parser.getCodec()).readValueAs(type);
            } else {
                return super.deserialize(parser, context);
            }
        }
    }
}
