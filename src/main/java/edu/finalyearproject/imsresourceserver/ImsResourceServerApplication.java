package edu.finalyearproject.imsresourceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableResourceServer
public class ImsResourceServerApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ImsResourceServerApplication.class, args);
    }
}
