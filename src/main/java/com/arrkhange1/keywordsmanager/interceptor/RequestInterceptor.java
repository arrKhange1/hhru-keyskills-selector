package com.arrkhange1.keywordsmanager.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Primary
@Service
public class RequestInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequestDetails(request, body);
        return execution.execute(request, body);
    }

    private void logRequestDetails(HttpRequest request, byte[] body) throws IOException {
        logger.info("URI: {}", request.getURI());
        logger.info("Method: {}", request.getMethod());
    }
}
