package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class OverridableInvocationHandler implements InvocationHandler {

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return canHandleInvocation(o) ? handleMethodCall(o, method, objects) : method.invoke(o, method, objects);
    }

    private boolean canHandleInvocation(Object o) {
        return Overrideable.class.isAssignableFrom(o.getClass());
    }

    public Object handleMethodCall(Object o, Method method, Object[] objects) throws Throwable {
 //        if (invocationHandler == null) throw new IllegalStateException("No overriding behaviour set up");
        return method.invoke(o, method, objects);
    }
}
