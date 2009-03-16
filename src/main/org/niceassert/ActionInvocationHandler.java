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
            if (returnValueIsCompatibleWithMethod(result, method)) {
                return result;
            }
            throw new ClassCastException("Can't override method " + method.getName() + " return incompatible class (expected=" + method.getReturnType() + ", got=" + result + ")");
        } catch (Throwable throwable) {
            if(thrownExceptionIsCompatibleWithMethod(throwable, method)) throw throwable;
            throw new ClassCastException("Can't override method " + method.getName() + " to throw incompatible exception " + throwable.getClass());
        }
    }

    private static boolean thrownExceptionIsCompatibleWithMethod(Throwable throwable, Method method) throws Throwable {
        for (Class exceptionClass : method.getExceptionTypes()) {
            if (exceptionClass.isAssignableFrom(throwable.getClass())) return true;
        }
        return RuntimeException.class.isAssignableFrom(throwable.getClass());
    }

    private static boolean returnValueIsCompatibleWithMethod(Object returnValue, Method method) {
        return returnValue == null || method.getReturnType().isAssignableFrom(returnValue.getClass());
    }
}
