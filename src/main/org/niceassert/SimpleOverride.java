package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class SimpleOverride {

    public static <T> T modifyForOverride(final T target) {
        T proxy = (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new OverridableInvocationHandler(), target.getClass(), Overrideable.class);
        Overrideable.class.cast(proxy).setTarget(target);
        return proxy;
    }

    public static <T> OverrideBuilder<T> override(T target) {
        if (!Overrideable.class.isAssignableFrom(target.getClass()))
            throw new IllegalArgumentException("Target object must be modified for override");
        return new OverrideBuilder<T>(Overrideable.class.cast(target));
    }

    static class OverrideBuilder<T> {
        private final Overrideable overrideableTarget;
        private final OverrideInvocationMatcher matcher = new OverrideInvocationMatcher();

        public OverrideBuilder(Overrideable overrideableTarget) {
            this.overrideableTarget = overrideableTarget;;
            overrideableTarget.setMatcher(matcher);
        }

        public OverrideBuilder<T> to(Action action) {
            matcher.setAction(action);
            return this;
        }

        public T whenCalling() {
            return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                    matcher.recordInvocation(method, objects);
                    return null;
                }
            }, overrideableTarget.getTarget().getClass());
        }
    }

    public static Action returnValue(final Object returnValue) {
        return new Action() {
            public Object execute(Object[] objects) {
                return returnValue;
            }
        };
    }

    public static Action throwException(final Throwable t) {
        return new Action() {
            public Object execute(Object[] objects) throws Throwable {
                throw t;
            }
        };
    }

    private static class OverridableInvocationHandler implements InvocationHandler {
        private Object target;
        private OverrideInvocationMatcher matcher = new OverrideInvocationMatcher();

        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (Overrideable.class.getMethod("setTarget", Object.class).equals(method)) {
                target = objects[0];
                return Void.TYPE;
            }
            if (Overrideable.class.getMethod("getTarget").equals(method)) {
                return target;
            }
            if (Overrideable.class.getMethod("setMatcher", OverrideInvocationMatcher.class).equals(method)) {
                matcher = (OverrideInvocationMatcher) objects[0];
                return method.getReturnType() == Void.TYPE ? Void.TYPE : null;
            }

            return matcher.isMethodCallMatched(method, objects) ? matcher.processOverriddenCall(method, objects) : method.invoke(target, objects);
        }
    }

    private static interface Overrideable<T> {
        void setTarget(Object target);
        T getTarget();
        void setMatcher(OverrideInvocationMatcher matcher);
    }
}