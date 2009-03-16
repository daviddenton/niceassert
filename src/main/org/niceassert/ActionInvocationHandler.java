package org.niceassert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class ActionInvocationHandler implements InvocationHandler {
    private final Action action;

    ActionInvocationHandler(Action action) {
        this.action = action;
    }

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        try {
            Object result = action.execute(objects);
            validateReturnValueCompatability(method.getReturnType(), result);
            return result;
        } catch (Throwable throwable) {
            if(RuntimeException.class.isAssignableFrom(throwable.getClass())) throw throwable;
            for (Class exceptionClass : method.getExceptionTypes()) {
                if (exceptionClass.isAssignableFrom(throwable.getClass())) throw throwable;
            }
            throw new ClassCastException("Can't override method " + method.getName() + " to throw incompatible exception " + throwable.getClass());
        }
    }

    private static void validateReturnValueCompatability(Class<?> expected, Object returnValue) {
        if (returnValue == null) return;
        if (!expected.isAssignableFrom(returnValue.getClass()))
            throw new ClassCastException("Can't override method to return incompatible class (expected=" + expected + ", got=" + returnValue + ")");
    }
}
