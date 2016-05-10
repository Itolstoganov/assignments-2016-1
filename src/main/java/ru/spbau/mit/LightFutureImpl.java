package ru.spbau.mit;


import java.util.function.Function;
import java.util.function.Supplier;

public class LightFutureImpl<R> implements LightFuture<R> {
    private ThreadPoolImpl threadPool;
    private volatile R result = null;
    private volatile Supplier<R> supplier = null;
    private volatile Throwable exception = null;

    private final Object dummy = new Object();

    protected LightFutureImpl(ThreadPoolImpl threadPool, Supplier<R> supplier) {
        this.threadPool = threadPool;
        this.supplier = supplier;
    }


    public boolean isReady() {
        return result != null || exception != null;
    }

    @Override
    public R get() throws LightExecutionException, InterruptedException {
        synchronized (this) {
            if (!isReady()) {
                this.wait();
            }
        }

        if (exception != null) {
            throw new LightExecutionException(exception);
        }

        return result;
    }


    @Override
    public <U> LightFuture<U> thenApply(Function<? super R, ? extends U> f) {
        LightFutureImpl<U> child = new LightFutureImpl<>(threadPool, () -> {
            R parentResult = null;
            try {
                    parentResult = LightFutureImpl.this.get();
                } catch (Exception e) {
                    exception = e;
                }
            return f.apply(parentResult);
        });
        threadPool.addToQueue(child);
        return child;
    }


    protected void execute() {
        synchronized (this) {
            if (!isReady()) {
                try {
                    result = supplier.get();
                } catch (Exception e) {
                    exception = e;
                }
            }
            notifyAll();
        }
    }
}
