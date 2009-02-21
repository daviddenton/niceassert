package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class Override {

    public static <T> T modifyForOverride(final T target) {
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new OverridableInvocationHandler(), target.getClass(), Overrideable.class);
    }

    public static <T> OverrideBuilder<T> override(T target) {
        if(!Overrideable.class.isAssignableFrom(target.getClass())) throw new IllegalArgumentException("Target object must be modified for override");
        return new OverrideBuilder<T>(Overrideable.class.cast(target));
    }

    public static class OverrideBuilder<T> {
        private final Overrideable target;

        public OverrideBuilder(Overrideable target) {
            this.target = target;
        }

        public OverrideBuilder<T> to(Action action) {
            target.setAction(action);
            return this;
        }

        public T whenCalling() {
            return (T) target;
        }
    }

    public static Action returnValue(final Object returnValue) {
        return new Action() {
            public Object execute() {
                return returnValue;
            }
        };
    }

    public static Action throwException(final Throwable t) {
        return new Action() {
            public Object execute() throws Throwable {
                throw t;
            }
        };
    }

    private static class OverridableInvocationHandler implements InvocationHandler {
        private Action action;

        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if(method == Overrideable.class.getMethod("setAction", Action.class)) {
                action = (Action) objects[0];
                return Void.TYPE;
            }
            return action != null ? action.execute() : method.invoke(o, objects);
        }
    }

    static interface Overrideable {
    void setAction(Action action);
}
}