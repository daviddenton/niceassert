package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MatchingOverride<T> {
    private final Overrideable proxy;
    private final List<Matcher> parameterMatchers = new ArrayList<Matcher>();
    private final OverridableInvocationHandler invocationHandler;

    public <T> MatchingOverride(T target) {
        invocationHandler = new OverridableInvocationHandler(parameterMatchers, target);
        proxy = (Overrideable) ConcreteClassProxyFactory.INSTANCE.proxyFor(invocationHandler, target.getClass(), Overrideable.class);
        Overrideable.class.cast(proxy).setTarget(target);
    }

    public T proxy() {
        return (T) proxy;
    }

    public <T> T with(Matcher<T> matcher) {
        parameterMatchers.add(matcher);
        return null;
    }

    public MatchingOverride<T> returnValue(final Object returnValue) {
        invocationHandler.setAction(new Action() {
            public Object execute(Object[] objects) {
                return returnValue;
            }
        });
        return this;
    }

    public MatchingOverride<T> throwException(final Throwable t) {
        invocationHandler.setAction(new Action() {
            public Object execute(Object[] objects) throws Throwable {
                throw t;
            }
        });
        return this;
    }

    public T whenCalling() {
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                invocationHandler.setMethod(method);
                return null;
            }
        }, proxy.getClass());
    }

    private static class OverridableInvocationHandler implements InvocationHandler {
        private Method aMethod;
        private final Object target;
        private final List<Matcher> parameterMatchers;
        private Action action;

        public OverridableInvocationHandler(List<Matcher> parameterMatchers, Object target) {
            this.parameterMatchers = parameterMatchers;
            this.target = target;
        }

        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            return isMethodCallMatched(method, objects) ? processOverriddenCall(method, objects) : method.invoke(target, objects);
        }

        private boolean isMethodCallMatched(Method method, Object[] objects) {
            if (!method.equals(aMethod)) {
                return false;
            }
            for (int i = 0; i < parameterMatchers.size(); i++) {
                if (!parameterMatchers.get(i).matches(objects[i])) return false;
            }
            return true;
        }

        private Object processOverriddenCall(Method method, Object[] objects) throws Throwable {
            try {
                Object result = action.execute(objects);
                validateClassCompabitility(aMethod.getReturnType(), result.getClass());
                return result;
            } catch (Throwable throwable) {
                for (Class exceptionClass : method.getExceptionTypes()) {
                    if (exceptionClass.isAssignableFrom(throwable.getClass())) throw throwable;
                }
                throw new ClassCastException("Can't override method " + aMethod.getName() + " to throw incompatible exception " + throwable.getClass());
            }
        }

        private void validateClassCompabitility(Class<?> expected, Class<? extends Object> actual) {
            if (!expected.isAssignableFrom(actual))
                throw new ClassCastException("Can't override method to return incompatible class (expected=" + expected + ", got=" + actual + ")");
        }

        private Object setupOverride(Method method, Object[] objects) {
            aMethod = (Method) objects[0];
            return method.getReturnType() == Void.TYPE ? Void.TYPE : null;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public void setMethod(Method method) {
            aMethod = method;
        }
    }

    private static interface Overrideable<T> {
        void setMethod(Method method);
        void setTarget(Object target);
    }
}
