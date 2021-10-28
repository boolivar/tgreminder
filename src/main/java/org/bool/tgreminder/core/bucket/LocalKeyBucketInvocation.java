package org.bool.tgreminder.core.bucket;

import org.aopalliance.intercept.MethodInvocation;

public class LocalKeyBucketInvocation implements BucketInvocation {

    private final ThreadLocal<Object> localKey;
    
    public LocalKeyBucketInvocation() {
        this(new ThreadLocal<>());
    }
    
    public LocalKeyBucketInvocation(ThreadLocal<Object> localKey) {
        this.localKey = localKey;
    }
    
    public Object getKey() {
        return localKey.get();
    }
    
    @Override
    public Object invoke(Object key, MethodInvocation invocation) throws Throwable {
        localKey.set(key);
        try {
            return invocation.proceed();
        } finally {
            localKey.remove();
        }
    }
}
