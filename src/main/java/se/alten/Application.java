package se.alten;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import se.alten.model.User;
import se.alten.repository.ChatMessageRepo;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by pl3731 on 2017-01-26.
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan({"se.alten"})
public class Application extends SpringBootServletInitializer {

    private static Properties properties;
    private Logger log = Logger.getLogger(Application.class.toString());
    @Resource
    private ChatMessageRepo messageRepo;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.application().setRegisterShutdownHook(true);
        return application.sources(Application.class);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8000", "http://localhost:4200", "chrome-extension://aejoelaoggembcahagimdiliamlcdmfm")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

    public static void main(String[] args) throws Exception {
        setLocalConfigurations();
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void addUser() {
        String property = properties.getProperty("ddl-auto");
        if (property.equalsIgnoreCase("Create-drop")) {
            log.info("Database is empty. Creating default users");
            User user1 = new User("testuser 1", "password1");
            log.info("@PostConstruct " + user1.toString());
            messageRepo.createUser(user1);
            User user2 = new User("testuser 2", "password2");
            log.info("@PostConstruct " + user2.toString());
            messageRepo.createUser(user2);
        }
    }

    private static void setLocalConfigurations() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("/local_properties.yml"));
        properties = yaml.getObject();
        System.setProperty("username", properties.getProperty("username"));
        System.setProperty("password", properties.getProperty("password"));
        System.setProperty("url", properties.getProperty("url"));
        System.setProperty("ddl-auto", properties.getProperty("ddl-auto"));


    }
}
