package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;

public class MatchingOverride<T> {
    private final Overrideable proxy;
    private Class clazz;
    private final OverrideInvocationMatcher overrideInvocationMatcher = new OverrideInvocationMatcher();

    public <T> MatchingOverride(final T target) {
        this.clazz = target.getClass();
        proxy = (Overrideable) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                return overrideInvocationMatcher.isMethodCallMatched(method, objects) ? overrideInvocationMatcher.processOverriddenCall(method, objects) : method.invoke(target, objects);
            }
        }, target.getClass(), Overrideable.class);
    }

    public T proxy() {
        return (T) proxy;
    }

    public <T> T with(Matcher<T> matcher) {
        overrideInvocationMatcher.addNextParameterMatcher(matcher);
        return null;
    }

    public MatchingOverride<T> returnValue(final Object returnValue) {
        overrideInvocationMatcher.setAction(new Action() {
            public Object execute(Object[] objects) {
                return returnValue;
            }
        });
        return this;
    }

    public MatchingOverride<T> throwException(final Throwable t) {
        overrideInvocationMatcher.setAction(new Action() {
            public Object execute(Object[] objects) throws Throwable {
                throw t;
            }
        });
        return this;
    }

    public T whenCalling() {
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            private boolean set;
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if(!set) overrideInvocationMatcher.recordInvocation(method, objects);
                set = true;
                return null;
            }
        }, clazz);
    }

    private static interface Overrideable {
        void setMethod(Method method);
    }
}
