package cn.com.jingcloud.utils.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger PoolNumber = new AtomicInteger(1);
    private final String _name;

    public NamedThreadFactory(String name) {
        _name = name;
    }

    @Override
    public synchronized Thread newThread(Runnable r) {
        return new Thread(r, _name + "-" + PoolNumber.getAndIncrement());//
    }

}
