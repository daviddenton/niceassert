package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MatchingOverride<T> {
    private final Overrideable proxy;
    private final List<Matcher> parameterMatchers = new ArrayList<Matcher>();
    private final OverridableInvocationHandler invocationHandler;
    private Class clazz;

    public <T> MatchingOverride(T target) {
        this.clazz = target.getClass();
        invocationHandler = new OverridableInvocationHandler(parameterMatchers, target);
        proxy = (Overrideable) ConcreteClassProxyFactory.INSTANCE.proxyFor(invocationHandler, target.getClass(), Overrideable.class);
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
            private boolean set;
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if(!set) invocationHandler.recordInvocation(method, objects);
                set = true;
                return null;
            }
        }, clazz);
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

        public void setAction(Action action) {
            this.action = action;
        }

        public void recordInvocation(Method method, Object[] objects) {
            this.aMethod = method;
            if(parameterMatchers.isEmpty()) {
                for (Object object : objects) {
                    parameterMatchers.add(equalTo(object));
                }
            }
        }
    }

    private static interface Overrideable {
        void setMethod(Method method);
    }
}
