package com.belbim.kopter.followme;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by eakbiyik on 22.5.2015.
 */
public class FireAndForgetExecutor {

    private static Executor executor = Executors.newFixedThreadPool(5);
    private static Executor singleExecutor = Executors.newSingleThreadExecutor();

    public static void exec(Runnable command) {
        executor.execute(command);
    }

    public static void singleExec(Runnable command) {
        singleExecutor.execute(command);
    }

}
