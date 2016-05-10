package ru.spbau.mit;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolTest {


    private class Task implements Supplier<Integer> {

        private final int sleep;
        private final int answer;

        Task(int sleep, int answer) {
            this.sleep = sleep;
            this.answer = answer;
        }

        @Override
        public Integer get() {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return answer;
        }
    }

    private class IntThenApplier implements Function<Integer, String> {

        private final int sleep;
        private final String str;

        IntThenApplier(int sleep, String str) {
            this.sleep = sleep;
            this.str = str;
        }

        @Override
        public String apply(Integer i) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Integer.toString(i) + str;
        }
    }

    private class StrThenApplier implements Function<String, String> {

        private final int sleep;
        private final String str;

        StrThenApplier(int sleep, String str) {
            this.sleep = sleep;
            this.str = str;
        }

        @Override
        public String apply(String s) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return s + str;
        }
    }

    @Test
    public void singleSimpleTest() throws LightExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(1);
        LightFuture<Integer> future = threadPool.submit(new Task(20, 54));
        Assert.assertEquals((int) future.get(), 54);
        threadPool.shutdown();
    }

    @Test
    public void multipleSimpleTest() throws LightExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(15);
        List<LightFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            futures.add(threadPool.submit(new Task(20, i)));
        }

        for (int i = 0; i < 15; i++) {
            Assert.assertEquals((int) futures.get(i).get(), i);
        }
        threadPool.shutdown();
    }

    @Test
    public void thenApplyTest() throws LightExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(10);
        List<LightFuture<Integer>> futures = new ArrayList<>();
        List<LightFuture<String>> applies = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            futures.add(threadPool.submit(new Task(20, i)));
            for (int j = 1; j < 4; j++) {
                for (int k = 1; k < 4; k++) {
                    IntThenApplier ita = new IntThenApplier(20, Integer.toString(42));
                    StrThenApplier sta = new StrThenApplier(20, Integer.toString(23));
                    applies.add(futures.get(i).thenApply(ita).thenApply(sta));
                }
            }
        }

        for (LightFuture<String> app : applies) {
            Assert.assertEquals(app.get(), app.get().charAt(0) + "42" + "23");
        }
        threadPool.shutdown();
    }


    @Test
    public void threadNumberTest() throws LightExecutionException, InterruptedException {
        ThreadPoolImpl threadPool = new ThreadPoolImpl(20);
        List<LightFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            futures.add(threadPool.submit(new Task(20, i)));
        }
        Assert.assertEquals(threadPool.threadsNumber(), 20);
        threadPool.shutdown();
    }

    @Test(expected = LightExecutionException.class)
    public void lightExceptionTest() throws LightExecutionException, InterruptedException {
        ThreadPoolImpl threadPool = new ThreadPoolImpl(2);
        LightFuture<Integer> future  = threadPool.submit(() -> {
            throw new RuntimeException("re");
        });
        future.get();
    }
}
