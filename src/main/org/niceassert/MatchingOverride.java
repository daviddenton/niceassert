package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MatchingOverride<T> {
    private final T proxy;
    private final Class clazz;
    protected final List<OverrideInvocationMatcher> invocationMatchers = new ArrayList<OverrideInvocationMatcher>();

    public MatchingOverride(final T target) {
        clazz = target.getClass();
         newMatcher();
        proxy = (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                for (OverrideInvocationMatcher invocationMatcher : invocationMatchers) {
                    if(invocationMatcher.isMethodCallMatched(method, objects)) return invocationMatcher.processOverriddenCall(method, objects);
                }
                return method.invoke(target, objects);
            }
        }, target.getClass());
    }

    public static <T> MatchingOverride<T> modifyForOverride(final T target) {
        return new MatchingOverride<T>(target);
    }

    public static <T> MatchingOverride<T> override(MatchingOverride<T> override) {
        return override;
    }

    public MatchingOverride<T> to() {
        return this;
    }

    public T proxy() {
        return proxy;
    }

    public <T> T with(Matcher<T> matcher) {
        currentMatcher().addNextParameterMatcher(matcher);
        return null;
    }

    private void newMatcher() {
        invocationMatchers.add(new OverrideInvocationMatcher());
    }

    private OverrideInvocationMatcher currentMatcher() {
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
}
