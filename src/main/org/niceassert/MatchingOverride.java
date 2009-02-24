package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;

public class MatchingOverride<T> {
    private final Overrideable proxy;
    private Action action;

    public <T> MatchingOverride(T target) {
        proxy = (Overrideable) ConcreteClassProxyFactory.INSTANCE.proxyFor(new OverridableInvocationHandler(), target.getClass(), Overrideable.class);
        Overrideable.class.cast(proxy).setTarget(target);
    }

    public T proxy() {
        return (T) proxy;
    }

    public <T> T with(Matcher<T> matcher) {
//            addParameterMatcher(matcher);
        return null;
    }

    public MatchingOverride<T> returnValue(final Object returnValue) {
        action = new Action() {
            public Object execute(Object[] objects) {
                return returnValue;
            }
        };
        return this;
    }

    public MatchingOverride<T> throwException(final Throwable t) {
        action = new Action() {
            public Object execute(Object[] objects) throws Throwable {
                throw t;
            }
        };
        return this;
    }

    public T whenCalling() {
        return proxy();
    }

    private static class OverridableInvocationHandler implements InvocationHandler {
        private Method aMethod;
        private Action anAction;
        private Object target;

        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (Overrideable.class.getMethod("setTarget", Object.class).equals(method)) {
                target = objects[0];
                return Void.TYPE;
            }
            if (Overrideable.class.getMethod("getTarget").equals(method)) {
                return target;
            }
            if (Overrideable.class.getMethod("setMethodAction", Method.class, Action.class).equals(method)) {
                return setupOverride(method, objects);
            }

            return processOverriddenCall(method, objects);
        }

        private Object processOverriddenCall(Method method, Object[] objects) throws Throwable {
            if (method.equals(aMethod)) {
                try {
                    Object result = anAction.execute(objects);
                    validateClassCompabitility(aMethod.getReturnType(), result.getClass());
                    return result;
                } catch (Throwable throwable) {
                    for (Class exceptionClass : method.getExceptionTypes()) {
                        if (exceptionClass.isAssignableFrom(throwable.getClass())) throw throwable;
                    }
                    throw new ClassCastException("Can't override method " + aMethod.getName() + " to throw incompatible exception " + throwable.getClass());

                }
            } else return method.invoke(target, objects);
        }

        private void validateClassCompabitility(Class<?> expected, Class<? extends Object> actual) {
            if (!expected.isAssignableFrom(actual))
                throw new ClassCastException("Can't override method to return incompatible class (expected=" + expected + ", got=" + actual + ")");
        }

        private Object setupOverride(Method method, Object[] objects) {
            aMethod = (Method) objects[0];
            anAction = (Action) objects[1];
            return method.getReturnType() == Void.TYPE ? Void.TYPE : null;
        }
    }

    private static interface Overrideable<T> {
        void setMethodAction(Method method, Action action);

        void setTarget(Object target);

        T getTarget();
    }
}
