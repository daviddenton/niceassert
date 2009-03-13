package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides the ability to override (or partially mock) the behaviour of a target object.
 * <p/>
 * For usage examples, please see the wiki documentation at: http://code.google.com/p/niceassert/
 *
 * @param <T> the class that's behaviour is being overridden.
 */
public class Overrider<T> {
    private final T proxy;
    private final Class clazz;
    private final List<OverriderInvocationMatcher> invocationMatchers = new ArrayList<OverriderInvocationMatcher>();

    public Overrider(final T target) {
        clazz = target.getClass();
        newMatcher();
        proxy = (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                for (OverriderInvocationMatcher invocationMatcher : invocationMatchers) {
                    if (invocationMatcher.isMethodCallMatched(method, objects))
                        return invocationMatcher.processOverriddenCall(method, objects);
                }
                return method.invoke(target, objects);
            }
        }, target.getClass());
    }

    public static <T> Overrider<T> wrapForOverride(final T target) {
        return new Overrider<T>(target);
    }

    public Overrider<T> will(Action action) {
        newMatcher();
        currentMatcher().setAction(action);
        return this;
    }

    public T proxy() {
        return proxy;
    }

    public <T> T with(Matcher<T> matcher) {
        currentMatcher().addNextParameterMatcher(matcher);
        return null;
    }

    public T whenCalling() {
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            private boolean set;

            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if (!set) currentMatcher().recordInvocation(method, objects);
                set = true;
                return null;
            }
        }, clazz);
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

    private void newMatcher() {
        invocationMatchers.add(new OverriderInvocationMatcher());
    }

    private OverriderInvocationMatcher currentMatcher() {
        return invocationMatchers.get(invocationMatchers.size() - 1);
    }
}
