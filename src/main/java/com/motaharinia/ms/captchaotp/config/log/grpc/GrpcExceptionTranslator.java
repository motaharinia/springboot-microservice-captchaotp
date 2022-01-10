package com.motaharinia.ms.captchaotp.config.log.grpc;

import com.motaharinia.ms.captchaotp.config.log.ExceptionLogger;
import com.motaharinia.msutility.custom.customjson.CustomObjectMapper;
import io.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class GrpcExceptionTranslator implements ServerInterceptor {

    @Autowired
    private ExceptionLogger exceptionLogger;

    @Autowired
    private MessageSource messageSource;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);
        return new ExceptionHandlingServerCallListener<>(listener, serverCall, metadata,exceptionLogger,new CustomObjectMapper(messageSource));
    }

}
