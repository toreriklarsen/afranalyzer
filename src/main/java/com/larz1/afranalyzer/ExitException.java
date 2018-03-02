package com.larz1.afranalyzer;

import org.springframework.boot.ExitCodeGenerator;

/**
 * Created by tor.erik.larsen on 03/07/2017.
 */
public class ExitException  extends RuntimeException implements ExitCodeGenerator {
    @Override
    public int getExitCode() {
        return 10;
    }
}
