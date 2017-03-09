package se.alten.sockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.socket.config.annotation.*;


/**
 * Created by pl3731 on 2017-03-03.
 */

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MessageHandler messageHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler, "/chat").setAllowedOrigins("*");
    }


}

