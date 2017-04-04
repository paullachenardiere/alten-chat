package se.alten.sockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;


/**
 * Created by pl3731 on 2017-03-03.
 */

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String CHAT_URL_PATH = "/chat";
    private final int MaxTextMessageBufferSize = 8192;
    private final int MaxBinaryMessageBufferSize = 8192;
    private final int SessionIdleTimeout = convertMinutesToMillis(10);
    private final int AsyncSendTimeout = convertMinutesToMillis(1);

    @Autowired
    MessageHandler messageHandler;

    @Bean
    public HttpSessionIdHandshakeInterceptor httpSessionIdHandshakeInterceptor() {
        return new HttpSessionIdHandshakeInterceptor();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(MaxTextMessageBufferSize);
        container.setMaxBinaryMessageBufferSize(MaxBinaryMessageBufferSize);
        container.setMaxSessionIdleTimeout(SessionIdleTimeout);
        container.setAsyncSendTimeout(AsyncSendTimeout);
        return container;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        WebSocketHandlerRegistration registration = registry.addHandler(messageHandler, CHAT_URL_PATH);
        registration.addInterceptors(httpSessionIdHandshakeInterceptor());
        registration.setAllowedOrigins("*");
    }

    private int convertMinutesToMillis(int min) {
        return min * (60 * 1000);
    }
}

