package se.alten;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import se.alten.model.User;
import se.alten.repository.ChatMessageRepo;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.*;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"se.alten"})
public class Application extends SpringBootServletInitializer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static Properties properties;
    @Resource
    private ChatMessageRepo messageRepo;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.application().setRegisterShutdownHook(true);
        return application.sources(Application.class);
    }

    //TODO Verify that this is required for the web sockets endpoint.
    @Bean
    public ServletContextAware endpointExporterInitializer(final ApplicationContext applicationContext) {
        return new ServletContextAware() {
            @Override
            public void setServletContext(ServletContext servletContext) {
                ServerEndpointExporter exporter = new ServerEndpointExporter();
                exporter.setApplicationContext(applicationContext);
                exporter.afterPropertiesSet();
            }
        };
    }

    //TODO This can probably be removed because of the proxy solution in the client...
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
        log.info("property ddl-auto = " + property);
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
