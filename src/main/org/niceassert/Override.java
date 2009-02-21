package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Override {

    public static <T> T modifyForOverride(final T target) {
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new OverridableInvocationHandler(), target.getClass(), Overrideable.class);
    }

    public static <T> OverrideBuilder<T> override(T target) {
        if (!Overrideable.class.isAssignableFrom(target.getClass()))
            throw new IllegalArgumentException("Target object must be modified for override");
        return new OverrideBuilder<T>(Overrideable.class.cast(target));
    }

    public static class OverrideBuilder<T> {
        private final Overrideable target;
        private Action action;

        public OverrideBuilder(Overrideable target) {
            this.target = target;
        }

        public OverrideBuilder<T> to(Action action) {
            this.action = action;
            return this;
        }

        public T whenCalling() {
            try {
                return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        Overrideable.class.cast(target.getClass()).setOverride(method, action);
                        return null;
                    }
                }, target.getClass());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
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
        private Method aMethod;
        private Action anAction;

        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (method == Overrideable.class.getMethod("setOverride", Action.class)) {
                return setupOverride(method, objects);
            }

            return processOverride(o, method, objects);
        }

        private Object processOverride(Object o, Method method, Object[] objects) throws IllegalAccessException, InvocationTargetException {
            if (method == aMethod) {
                try {
                    return anAction.execute(objects);
                } catch (Throwable throwable) {
                    throw new InvocationTargetException(throwable);
                }
            }
            else return method.invoke(objects);
        }

        private Object setupOverride(Method method, Object[] objects) {
            aMethod = (Method) objects[0];
            anAction = (Action) objects[1];
            return method.getReturnType() == Void.TYPE ? Void.TYPE : null;
        }
    }

    static interface Overrideable {
        void setOverride(Method method, Action action);
    }
}