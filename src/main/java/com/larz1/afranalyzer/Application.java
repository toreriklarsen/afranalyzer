package com.larz1.afranalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by tor.erik.larsen on 03/07/2017.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory
            .getLogger(Application.class);

    // Simple example shows how a command line spring application can execute an
    // injected bean service. Also demonstrates how you can use @Value to inject
    // command line args ('--name=whatever') or application properties
    @Autowired
    private AutoTuneService autoTuneService;

    @PostConstruct
    public void logSomething() {
        logger.debug("Sample Debug Message");
        logger.trace("Sample Trace Message");
    }


    @Override
    public void run(String... args) throws IOException {
        logger.trace("Current dir using System:" + System.getProperty("user.dir"));
        this.autoTuneService.process();
        if (args.length > 0 && args[0].equals("exitcode")) {
            throw new ExitException();
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args).close();
    }
}