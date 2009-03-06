package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SimpleOverride<T> {
    private final Class clazz;
    protected final List<OverrideInvocationMatcher> invocationMatchers = new ArrayList<OverrideInvocationMatcher>();

    public static <T> T modifyForOverride(final T target) {
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new OverridableInvocationHandler(target), target.getClass(), Overrideable.class);
    }

    public static <T> SimpleOverride<T> override(T target) {
        if (!Overrideable.class.isAssignableFrom(target.getClass()))
            throw new IllegalArgumentException("Target object must be modified for override");
        return new SimpleOverride<T>(Overrideable.class.cast(target));
    }

    public SimpleOverride(Overrideable overrideableTarget) {
        clazz = overrideableTarget.getTarget().getClass();
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

    protected void newMatcher() {
        invocationMatchers.add(new OverrideInvocationMatcher());
    }

    protected OverrideInvocationMatcher currentMatcher() {
        return invocationMatchers.get(invocationMatchers.size()-1);
    }

    public T whenCalling() {
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            private boolean set;
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if(!set) currentMatcher().recordInvocation(method, objects);
                set = true;
                return null;
            }
        }, clazz);
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