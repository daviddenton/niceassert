package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class DelegatingInvocationHandler implements InvocationHandler {
    private InvocationHandler invocationHandler;

    public void setInvocationHandler(InvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }

    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return DelegatingInvocationHandler.class.isAssignableFrom(o.getClass()) ? handleMethodCall(o, method, objects) : method.invoke(o, method, objects);
    }

    public Object handleMethodCall(Object o, Method method, Object[] objects) throws Throwable {
        if (invocationHandler == null) throw new IllegalStateException("No overriding behaviour set up");
        return invocationHandler.invoke(o, method, objects);
    }
}
