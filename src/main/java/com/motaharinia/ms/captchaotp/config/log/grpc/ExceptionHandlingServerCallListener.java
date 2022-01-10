package com.motaharinia.ms.captchaotp.config.log.grpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motaharinia.ms.captchaotp.config.log.ExceptionLogger;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public class ExceptionHandlingServerCallListener<ReqT, RespT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

    /**
     * شییی لاگ خطاها
     */
    private final ExceptionLogger exceptionLogger;
    private final ServerCall<ReqT, RespT> serverCall;
    private final Metadata metadata;

    private final ObjectMapper objectMapper;

    ExceptionHandlingServerCallListener(ServerCall.Listener<ReqT> listener, ServerCall<ReqT, RespT> serverCall, Metadata metadata, ExceptionLogger exceptionLogger,ObjectMapper objectMapper) {
        super(listener);
        this.serverCall = serverCall;
        this.metadata = metadata;
        this.exceptionLogger = exceptionLogger;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onHalfClose() {
        try {
            super.onHalfClose();
        } catch (RuntimeException ex) {
            handleException(ex, serverCall, metadata);
            //throw ex;
        }
    }

    @Override
    public void onReady() {
        try {
            super.onReady();
        } catch (RuntimeException ex) {
            handleException(ex, serverCall, metadata);
            //throw ex;
        }
    }

    private void handleException(RuntimeException exception, ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
        try {
            //تنظیم زبان لوکیل پروژه روی پارسی
            Locale.setDefault(new Locale("fa", "IR"));
            ClientResponseDto<String> clientResponseDto = exceptionLogger.handle(exception, null, null);
            switch (clientResponseDto.getException().getType()) {
                case BUSINESS_EXCEPTION:
                    serverCall.close(Status.UNKNOWN.augmentDescription(objectMapper.writeValueAsString(clientResponseDto)), metadata);
                    break;
                case GENERAL_EXCEPTION:
                    serverCall.close(Status.INTERNAL.augmentDescription(objectMapper.writeValueAsString(clientResponseDto)), metadata);
                    break;
                case EXTERNAL_CALL_EXCEPTION:
                    serverCall.close(Status.UNAVAILABLE.augmentDescription(objectMapper.writeValueAsString(clientResponseDto)), metadata);
                    break;
                case VALIDATION_EXCEPTION:
                    serverCall.close(Status.INVALID_ARGUMENT.augmentDescription(objectMapper.writeValueAsString(clientResponseDto)), metadata);
                    break;
            }
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("UM jsonProcessingException: {}", jsonProcessingException.getMessage() + " , " + jsonProcessingException);
        }
    }

}
