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
            if (returnValueIsCompatible(method.getReturnType(), result)) {
                return result;
            }
            throw new ClassCastException("Can't override method to return incompatible class (expected=" + method.getReturnType() + ", got=" + result + ")");
        } catch (Throwable throwable) {
            if(thrownExceptionIsCompatible(method, throwable)) throw throwable;
            throw new ClassCastException("Can't override method " + method.getName() + " to throw incompatible exception " + throwable.getClass());
        }
    }

    private static boolean thrownExceptionIsCompatible(Method method, Throwable throwable) throws Throwable {
        for (Class exceptionClass : method.getExceptionTypes()) {
            if (exceptionClass.isAssignableFrom(throwable.getClass())) return true;
        }
        return RuntimeException.class.isAssignableFrom(throwable.getClass());
    }

    private static boolean returnValueIsCompatible(Class<?> expected, Object returnValue) {
        return returnValue == null || expected.isAssignableFrom(returnValue.getClass());
    }
}
