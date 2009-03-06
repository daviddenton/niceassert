package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class SimpleOverride<T> extends AbstractOverride<T> {

    public static <T> T modifyForOverride(final T target) {
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new OverridableInvocationHandler(target), target.getClass(), Overrideable.class);
    }

    public static <T> SimpleOverride<T> override(T target) {
        if (!Overrideable.class.isAssignableFrom(target.getClass()))
            throw new IllegalArgumentException("Target object must be modified for override");
        return new SimpleOverride<T>(Overrideable.class.cast(target));
    }

    public SimpleOverride(Overrideable overrideableTarget) {
        super(overrideableTarget.getTarget().getClass());
        overrideableTarget.setMatcher(currentMatcher());
    }

    public SimpleOverride<T> to(Action action) {
        currentMatcher().setAction(action);
        return this;
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

    private static class OverridableInvocationHandler<T> implements InvocationHandler {
        private T target;
        private OverrideInvocationMatcher matcher = new OverrideInvocationMatcher();

        public OverridableInvocationHandler(T target) {
            this.target = target;
        }

        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
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
        T getTarget();
        void setMatcher(OverrideInvocationMatcher matcher);
    }
}