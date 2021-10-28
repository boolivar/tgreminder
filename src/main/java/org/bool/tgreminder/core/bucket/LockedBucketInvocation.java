package org.bool.tgreminder.core.bucket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

@Component
public class LockedBucketInvocation implements BucketInvocation {
    
    private final ConcurrentMap<Object, CountDownLatch> locks;
    
    private final int attempts;
    
    public LockedBucketInvocation() {
        this(new ConcurrentHashMap<>(), 5);
    }
    
    public LockedBucketInvocation(ConcurrentMap<Object, CountDownLatch> locks, int attempts) {
        this.locks = locks;
        this.attempts = attempts;
    }

    @Override
    public Object invoke(Object key, MethodInvocation invocation) throws Throwable {
        CountDownLatch myLatch = new CountDownLatch(1);
        try {
            for (int i = 0; i < attempts; ++i) {
                CountDownLatch latch = locks.computeIfAbsent(key, k -> myLatch);
                if (latch == myLatch) {
                    try {
                        return invocation.proceed();
                    } finally {
                        locks.remove(key);
                    }
                }
                latch.await();
            }
            throw new IllegalStateException("Error obtain lock for key " + key);
        } finally {
            myLatch.countDown();
        }
    }
}
