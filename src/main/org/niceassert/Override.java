package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class Override {
    private interface OverrideInterface {
        void setReturnValue(Object returnValue);

        void setException(Throwable t);
    }

    public static <T> T modifyForOverride(final T target) {
//        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new DelegatingInvocationHandler(), target.getClass(), DelegatingInvocationHandler.class);
        return null;
    }

     public static <T> OverrideBuilder<T> expect(final T target) {
        return new OverrideBuilder<T>(target);
    }

    public static <T> OverrideBuilder<T> override(T target) {
        return new OverrideBuilder<T>(target);
    }

    private static class OverrideBuilder<T> {
        private final T target;
        private Action action;

        public OverrideBuilder(T target) {
            this.target = target;
        }

        public OverrideBuilder<T> to(Action action) {
            this.action = action;
            return this;
        }

        public T whenCalling() {
            return target;
        }
    }

    public static interface Action {
        Object execute();
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
//            return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new OverrideInvocationHandler(target), target.getClass(), OverrideInterface.class);
//            if (!OverrideInterface.class.isAssignableFrom(target.getClass())) {
//                throw new IllegalArgumentException("Cannot force behaviour on a non-overridden concrete object");
//            }
//            OverrideInterface.class.cast(target).setReturnValue(returnValue);
//            return target;
            return null;
        }
    }

    public static class ThrowExceptionBuilder {
        private final Throwable throwable;

        public ThrowExceptionBuilder(Throwable throwable) {
            this.throwable = throwable;
        }

        public <T> T whenCalling(final T target) {
            if (!OverrideInterface.class.isAssignableFrom(target.getClass())) {
                throw new IllegalArgumentException("Cannot force behaviour on a non-overridden concrete object");
            }
            OverrideInterface.class.cast(target).setException(throwable);
            return target;
        }
    }

    private static class OverrideInvocationHandler<T> implements InvocationHandler {
        public Object returnValue;
        public Throwable throwable;
        private final T target;

        public OverrideInvocationHandler(T target) {
            this.target = target;
        }

        public Object invoke(Object object, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == OverrideInterface.class) {
                while(setUpReturnValue(method, args) && setUpThrowException(method, args));
                return Void.TYPE;
            }

            return method.invoke(target);
        }

        private boolean setUpReturnValue(Method method, Object[] args) {
            if ("setReturnValue".equals(method.getName())) {
                returnValue = args[0];
                return true;
            }
            return false;
        }

        private boolean setUpThrowException(Method method, Object[] args) {
            if ("setException".equals(method.getName())) {
                returnValue = args[0];
                return true;
            }
            return false;
        }
    }
}