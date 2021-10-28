package org.bool.tgreminder.core.bucket;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.NoSuchElementException;

@Component
public class BucketKeyAdvice extends AbstractPointcutAdvisor implements MethodInterceptor {

    private static final long serialVersionUID = 1L;

    private final Pointcut pointcut;
    
    private final BucketInvocation bucketInvocation;
    
    private final ParameterNameDiscoverer discoverer;
    
    @Autowired
    public BucketKeyAdvice(BucketInvocation bucketInvocation) {
        this(new AnnotationMatchingPointcut(null, BucketKey.class), bucketInvocation, new DefaultParameterNameDiscoverer());
    }
    
    public BucketKeyAdvice(Pointcut pointcut, BucketInvocation bucketInvocation, ParameterNameDiscoverer discoverer) {
        this.pointcut = pointcut;
        this.bucketInvocation = bucketInvocation;
        this.discoverer = discoverer;
    }
    
    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this;
    }
    
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object key = findKey(invocation.getMethod(), invocation.getArguments());
        return bucketInvocation.invoke(key, invocation);
    }
    
    private Object findKey(Method method, Object[] arguments) {
        BucketKey bucketKey = method.getAnnotation(BucketKey.class);
        if (bucketKey == null) {
            throw new NoSuchElementException("@BucketKey not found for method " + method);
        }
        String paramName = bucketKey.value();
        String[] names = discoverer.getParameterNames(method);
        for (int i = 0; i < method.getParameterCount(); ++i) {
            if (paramName.equals(names[i])) {
                return arguments[i];
            }
        }
        throw new NoSuchElementException("Parameter not found: " + paramName + " for method " + method);
    }
}
