package com.larz1.afranalyzer;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

//@SpringBootApplication
public class SwingApp extends JFrame {

    public SwingApp() {

        initUI();
    }

    private void initUI() {

        JButton quitButton = new JButton("Quit");

        quitButton.addActionListener((ActionEvent event) -> {
            System.exit(0);
        });

        JButton doButton = new JButton("Do");

        doButton.addActionListener((ActionEvent event) -> {
            System.out.println("Do");
        });

        createLayout(quitButton, doButton);

        setTitle("Quit button");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addComponent(arg[0])
                .addComponent(arg[1])
        );

        gl.setVerticalGroup(gl.createSequentialGroup()

                .addComponent(arg[1])
                .addComponent(arg[0])
        );
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(SwingApp.class)
                .headless(false).run(args);

        EventQueue.invokeLater(() -> {
            SwingApp ex = ctx.getBean(SwingApp.class);
            ex.setVisible(true);
        });
    }
}