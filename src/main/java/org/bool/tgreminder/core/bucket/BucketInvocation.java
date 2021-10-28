package org.bool.tgreminder.core.bucket;

import org.aopalliance.intercept.MethodInvocation;

public interface BucketInvocation {
    Object invoke(Object key, MethodInvocation invocation) throws Throwable;
}
