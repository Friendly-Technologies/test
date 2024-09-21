package com.friendly.services;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.friendly.services", "com.friendly.commons"})
public class ServicesApplication {

    private static volatile ConfigurableApplicationContext context;
    private static ClassLoader mainThreadClassLoader;


    public static void main(String[] args) {
        mainThreadClassLoader = Thread.currentThread().getContextClassLoader();
        context = SpringApplication.run(ServicesApplication.class, args);
    }

    public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(ServicesApplication.class, args.getSourceArgs());
        });

        thread.setContextClassLoader(mainThreadClassLoader);
        thread.setDaemon(false);
        thread.start();
    }

}
