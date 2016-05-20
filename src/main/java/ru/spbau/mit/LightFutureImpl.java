package ru.spbau.mit;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class LightFutureImpl<R> implements LightFuture<R> {
    private final Supplier<R> supplier;
    private ThreadPoolImpl threadPool;
    private volatile R result = null;
    private volatile Throwable exception = null;
    private final List<LightFutureImpl> children = new ArrayList<>();

    protected LightFutureImpl(ThreadPoolImpl threadPool, Supplier<R> supplier) {
        this.supplier = supplier;
        this.threadPool = threadPool;
    }


    public boolean isReady() {
        return result != null || exception != null;
    }

    @Override
    public R get() throws LightExecutionException, InterruptedException {
        synchronized (this) {
            while (!isReady()) {
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
        if (isReady()) {
            threadPool.addToQueue(child);
        } else {
            children.add(child);
        }
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
                children.stream().forEach(c -> this.threadPool.addToQueue(c));
            }
            notifyAll();
        }
    }

}
