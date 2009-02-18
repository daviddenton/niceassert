package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class Override {

    public static <T> T override(final T target) {
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(target.getClass(), new InvocationHandler() {
            public Object invoke(Object object, Method method, Object[] args) throws Throwable {
                return method.invoke(target);
            }
        });
    }

    public static OverrideReturnValueBuilder returnValue(Object returnValue) {
        return new OverrideReturnValueBuilder(returnValue);
    }

    public static ThrowExceptionBuilder throwException(final Throwable t) {
        return new ThrowExceptionBuilder(t);
    }

    public static class OverrideReturnValueBuilder {
        private final Object returnValue;

        public OverrideReturnValueBuilder(Object returnValue) {
            this.returnValue = returnValue;
        }

        public <T> T whenCalling(final T target) {
            return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(target.getClass(), new InvocationHandler() {
                public Object invoke(Object object, Method method, Object[] args) throws Throwable {
                    return returnValue;
                }
            });
        }
    }

    public static class ThrowExceptionBuilder {
        private final Throwable throwable;

        public ThrowExceptionBuilder(Throwable throwable) {
            this.throwable = throwable;
        }

        public <T> T whenCalling(final T target) {
            return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(target.getClass(), new InvocationHandler() {
                public Object invoke(Object object, Method method, Object[] args) throws Throwable {
                    throw throwable;
                }
            });
        }
    }
}