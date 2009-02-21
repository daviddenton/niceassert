package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Override {

    public static <T> T modifyForOverride(final T target) {
        T proxy = (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new OverridableInvocationHandler(), target.getClass(), Overrideable.class);
        Overrideable.class.cast(proxy).setTarget(target);
        return proxy;
    }

    public static <T> OverrideBuilder<T> override(T target) {
        if (!Overrideable.class.isAssignableFrom(target.getClass()))
            throw new IllegalArgumentException("Target object must be modified for override");
        return new OverrideBuilder<T>(Overrideable.class.cast(target));
    }

    static class OverrideBuilder<T> {
        private final Overrideable overrideableTarget;
        private Action action;

        public OverrideBuilder(Overrideable overrideableTarget) {
            this.overrideableTarget = overrideableTarget;
        }

        public OverrideBuilder<T> to(Action action) {
            this.action = action;
            return this;
        }

        public T whenCalling() {
            try {
                return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        overrideableTarget.setMethodAction(method, action);
                        return null;
                    }
                }, overrideableTarget.getTarget().getClass());
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

            return processOverride(o, method, objects);
        }

        private Object processOverride(Object o, Method method, Object[] objects) throws Throwable, InvocationTargetException {
            if (method.equals(aMethod)) {
                return anAction.execute(objects);
            } else return method.invoke(target, objects);
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