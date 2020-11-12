package org.bool.tgreminder.core;

import org.springframework.aop.TargetSource;

import java.util.List;
import java.util.function.Supplier;

public class LocalKeyTargetSource<T> implements TargetSource {

    private final Class<T> type;
    
    private final List<? extends T> targetCache;
    
    private final Supplier<?> localKey;
    
    public LocalKeyTargetSource(Class<T> type, List<? extends T> targetCache, Supplier<?> localKey) {
        this.type = type;
        this.targetCache = targetCache;
        this.localKey = localKey;
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
