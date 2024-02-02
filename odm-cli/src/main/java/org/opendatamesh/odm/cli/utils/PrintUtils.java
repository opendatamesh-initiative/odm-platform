package org.opendatamesh.odm.cli.utils;

import java.io.OutputStream;
import java.io.PrintStream;

public class PrintUtils {

    public static void silentExecution(ExceptionThrowingRunnable runnable) throws Exception {
        // Redirect standard output and error to null
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                // No-op to suppress output
            }
        }));
        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {
                // No-op to suppress output
            }
        }));

        try {
            runnable.run();
        } finally {
            // Reset standard output and error to original streams
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    @FunctionalInterface
    public interface ExceptionThrowingRunnable {
        void run() throws Exception;
    }

}
