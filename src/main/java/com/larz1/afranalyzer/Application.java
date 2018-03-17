package com.larz1.afranalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by tor.erik.larsen on 03/07/2017.
 */
@SpringBootApplication
@EnableAutoConfiguration
public class Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory
            .getLogger(Application.class);

    @Autowired
    private AutoTuneService autoTuneService;

    @Value("${afrfile:data/almeria-before.csv}")
    private  String afrFile;

    @PostConstruct
    public void logSomething() {
        logger.debug("Sample Debug Message");
        logger.trace("Sample Trace Message");
    }


    @Override
    public void run(String... args) {
        logger.trace("Current dir using System:" + System.getProperty("user.dir"));
        if (args.length > 0 && args[0].equals("exitcode")) {
            throw new ExitException();
        }
    }

    public static void main(String[] args) {
        //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Application.class)
                .headless(false).web(false).run(args);

        EventQueue.invokeLater(() -> {
            AutoTuneTable ex = ctx.getBean(AutoTuneTable.class);
            ex.setVisible(true);
        });
    }
}