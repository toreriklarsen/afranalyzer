package com.larz1.afranalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tor.erik.larsen on 03/07/2017.
 */
@SpringBootApplication
//@EnableAutoConfiguration
public class Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory
            .getLogger(Application.class);

    @Override
    public void run(String... args) {
        logger.debug("Current dir using System:" + System.getProperty("user.dir"));
        if (args.length > 0 && args[0].equals("exitcode")) {
            throw new ExitException();
        }
    }

    public static void main(String[] args) {
        //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        try {
            UIManager.setLookAndFeel(UIManager
                    .getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.info("HOHO, not here");
        }

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Application.class)
                .headless(false).web(false).run(args);

        EventQueue.invokeLater(() -> {
            AfrAnalyzerUi ex = ctx.getBean(AfrAnalyzerUi.class);
            ex.setVisible(true);
        });
    }
}