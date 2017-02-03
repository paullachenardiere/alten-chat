package se.alten;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;
import se.alten.model.User;
import se.alten.repository.ChatMessageRepo;
import se.alten.service.ChatMessageService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.logging.Logger;

/**
 * Created by pl3731 on 2017-01-26.
 */

//@SpringBootApplication
//@ApplicationScope
@Configuration
@EnableAutoConfiguration
@ComponentScan({"com.therealdanvega", "se.alten"})
public class Application extends SpringBootServletInitializer {

    private Logger log = Logger.getLogger(Application.class.toString());

    @Autowired
    private ChatMessageService service;

    @Resource
    private ChatMessageRepo messageRepo;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void addUser() {
        User user = new User("testuser", "password");
        log.info("@PostConstruct" + user.toString());
        messageRepo.createUser(user);
    }

}
