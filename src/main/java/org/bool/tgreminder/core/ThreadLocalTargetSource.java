package org.bool.tgreminder.core;

import org.springframework.aop.TargetSource;

import java.util.List;

public class ThreadLocalTargetSource<T> implements TargetSource {

    private final Class<T> type;
    
    private final List<? extends T> targetCache;
    
    private final ThreadLocal<Object> localKey;
    
    public ThreadLocalTargetSource(Class<T> type, List<? extends T> targetCache) {
        this(type, targetCache, new ThreadLocal<>());
    }
    
    public ThreadLocalTargetSource(Class<T> type, List<? extends T> targetCache, ThreadLocal<Object> localKey) {
        this.type = type;
        this.targetCache = targetCache;
        this.localKey = localKey;
    }
    
    public void setKey(Object key) {
        localKey.set(key);
    }
    
    public void resetKey() {
        localKey.remove();
    }
    
    @Override
    public T getTarget() {
        Object key = localKey.get();
        return key != null
                ? targetCache.get(key.hashCode() % (targetCache.size() - 1) + 1)
                : targetCache.get(0);
    }

    @Override
    public Class<T> getTargetClass() {
        return type;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public void releaseTarget(Object target) {
    }
}
