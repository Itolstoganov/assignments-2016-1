package ru.spbau.mit;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {

    private boolean isWorking = true;
    private BlockingQueue<LightFutureImpl> taskQueue;
    private FutureWorker[] futureWorkers;

    private final class BlockingQueue<E> {

        private Queue<E> queue = new LinkedList<>();
        private int limit;

        private BlockingQueue(int limit) {
            this.limit = limit;
        }


        public synchronized void enqueue(E element) throws InterruptedException {
            while (this.queue.size() == this.limit) {
                wait();
            }
            if (this.queue.size() == 0) {
                notifyAll();
            }
            this.queue.add(element);
        }


        public synchronized E dequeue() throws InterruptedException {
            while (this.queue.size() == 0) {
                wait();
            }
            if (this.queue.size() == this.limit) {
                notifyAll();
            }
            return this.queue.remove();
        }
    }

    private class FutureWorker extends Thread {
        private ThreadPoolImpl threadPool;
        private boolean isWorking = true;

        FutureWorker(ThreadPoolImpl threadPool) {
            this.threadPool = threadPool;
        }


        public void run() {
            while (isWorking()) {
                try {
                    LightFutureImpl lightFuture  = taskQueue.dequeue();
                    lightFuture.execute();
                } catch (InterruptedException e) {
                    this.futureStop();
                }
            }
        }

        public synchronized void futureStop() {
            isWorking = false;
            this.interrupt();
        }

        public boolean isWorking() {
            return this.isWorking && threadPool.getIsWorking();
        }
    }

    public ThreadPoolImpl(int n) {
        futureWorkers = new FutureWorker[n];
        taskQueue = new BlockingQueue<>(n);
        isWorking = true;
        for (int i = 0; i < n; i++) {
            futureWorkers[i] = new FutureWorker(this);
            futureWorkers[i].start();
        }
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        LightFutureImpl<R> lightFuture = new LightFutureImpl<>(this, supplier);
        this.addToQueue(lightFuture);
        return lightFuture;
    }

    @Override
    public void shutdown() {
        synchronized (this) {
            this.isWorking = false;
            for (FutureWorker worker : futureWorkers) {
                worker.futureStop();
            }
        }
    }

    protected boolean getIsWorking() {
        return isWorking;
    }

    protected synchronized void addToQueue(LightFutureImpl task) {
        try {
            taskQueue.enqueue(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //For testing purposes
    public int threadsNumber() {
        return futureWorkers.length;
    }
}

