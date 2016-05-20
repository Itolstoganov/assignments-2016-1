package ru.spbau.mit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {

    private boolean isWorking = true;
    private BlockingQueue<LightFutureImpl> taskQueue;
    private List<Thread> workers;
    private final class BlockingQueue<E> {

        private Queue<E> queue = new LinkedList<>();

        public synchronized void enqueue(E element) throws InterruptedException {
            if (this.queue.size() == 0) {
                notifyAll();
            }
            this.queue.add(element);
        }


        public synchronized E dequeue() throws InterruptedException {
            while (this.queue.size() == 0) {
                wait();
            }
            return this.queue.remove();
        }
    }

    public ThreadPoolImpl(int n) {
        workers = new ArrayList<>();
        taskQueue = new BlockingQueue<>();
        isWorking = true;
        for (int i = 0; i < n; i++) {
            workers.add(new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    LightFutureImpl lightFuture = null;
                    try {
                        lightFuture = taskQueue.dequeue();
                    } catch (InterruptedException e) {
                    }
                    if (lightFuture != null) {
                        lightFuture.execute();
                    }
                }
            }));
            workers.get(i).start();
        }
    }

    @Override
    public <R> LightFuture<R> submit(Supplier<R> supplier) {
        LightFutureImpl<R> lightFuture = new LightFutureImpl<>(this, supplier);
        synchronized (this) {
            this.addToQueue(lightFuture);
            notifyAll();
        }
        return lightFuture;
    }

    @Override
    public void shutdown() {
        synchronized (this) {
            this.isWorking = false;
            for (Thread worker : workers) {
                worker.interrupt();
            }
        }
    }

    protected boolean getIsWorking() {
        return isWorking;
    }

    protected synchronized void addToQueue(LightFutureImpl task) {
        try {
            taskQueue.enqueue(task);
        } catch (InterruptedException e) {    //There's no need to handle that
        }
    }

}

