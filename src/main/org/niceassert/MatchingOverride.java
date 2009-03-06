package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MatchingOverride<T> {
    private final Overrideable proxy;
    private Class clazz;
    private final List<OverrideInvocationMatcher> invocationMatchers = new ArrayList<OverrideInvocationMatcher>();

    public <T> MatchingOverride(final T target) {
        newMatcher();
        this.clazz = target.getClass();
        proxy = (Overrideable) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                for (OverrideInvocationMatcher invocationMatcher : invocationMatchers) {
                    if(invocationMatcher.isMethodCallMatched(method, objects)) return invocationMatcher.processOverriddenCall(method, objects);
                }
                return method.invoke(target, objects);
            }
        }, target.getClass(), Overrideable.class);
    }

    public T proxy() {
        return (T) proxy;
    }

    public <T> T with(Matcher<T> matcher) {
        currentMatcher().addNextParameterMatcher(matcher);
        return null;
    }

    public MatchingOverride<T> returnValue(final Object returnValue) {
        newMatcher();
        currentMatcher().setAction(new Action() {
            public Object execute(Object[] objects) {
                return returnValue;
            }
        });
        return this;
    }

    public MatchingOverride<T> throwException(final Throwable t) {
        currentMatcher().setAction(new Action() {
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
                if(!set) currentMatcher().recordInvocation(method, objects);
                set = true;
                return null;
            }
        }, clazz);
    }

    private void newMatcher() {
        invocationMatchers.add(new OverrideInvocationMatcher());
    }

    private OverrideInvocationMatcher currentMatcher() {
        return invocationMatchers.get(invocationMatchers.size()-1);
    }

    private static interface Overrideable {
        void setMethod(Method method);
    }
}
