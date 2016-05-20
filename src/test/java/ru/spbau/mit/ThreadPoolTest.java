package ru.spbau.mit;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;

public class ThreadPoolTest {

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
    public void testSingleSimple() throws LightExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(1);
        LightFuture<Integer> future = threadPool.submit(() -> 54);
        Assert.assertEquals((int) future.get(), 54);
        threadPool.shutdown();
    }

    @Test
    public void testMultipleSimple() throws LightExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(15);
        List<LightFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            final int finalI = i;
            futures.add(threadPool.submit(() -> finalI));
        }

        for (int i = 0; i < 15; i++) {
            Assert.assertEquals((int) futures.get(i).get(), i);
        }
        threadPool.shutdown();
    }

    @Test
    public void testThenApply() throws LightExecutionException, InterruptedException {
        ThreadPool threadPool = new ThreadPoolImpl(10);
        List<LightFuture<Integer>> futures = new ArrayList<>();
        List<LightFuture<String>> applies = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            futures.add(threadPool.submit(() ->  finalI));
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
    public void testThenApplyBlocker() throws InterruptedException {
        final ThreadPool threadPool = new ThreadPoolImpl(2);
        IntThenApplier ita = new IntThenApplier(20, Integer.toString(42));
        final LightFuture<Integer> blockingTask = threadPool.submit(() -> {
            while (true) {
                int checkstyleDummy = 0;
            }
        });
        blockingTask.thenApply(ita);
        final LightFuture<Integer> blockedTask = threadPool.submit(() -> 10);
        Thread.sleep(200);
        assertTrue(blockedTask.isReady());
        threadPool.shutdown();
    }

    @Test
    public void testThreadNumber() throws LightExecutionException, InterruptedException {
        ThreadPoolImpl threadPool = new ThreadPoolImpl(3);
        CyclicBarrier barrier = new CyclicBarrier(3);
        List<LightFuture<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tasks.add(threadPool.submit(() -> {
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }));
        }
        for (LightFuture task : tasks) {
            task.get();
            assertTrue(task.isReady());
        }
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
