package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AbstractOverride<T> {
    private final Class clazz;
    protected final List<OverrideInvocationMatcher> invocationMatchers = new ArrayList<OverrideInvocationMatcher>();

    public AbstractOverride(Class clazz) {
        this.clazz = clazz;
        newMatcher();
    }
    
    protected void newMatcher() {
        invocationMatchers.add(new OverrideInvocationMatcher());
    }

    protected OverrideInvocationMatcher currentMatcher() {
        return invocationMatchers.get(invocationMatchers.size()-1);
    }

    public T whenCalling() {
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            private boolean set;
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if(!set) currentMatcher().recordInvocation(method, objects);
                set = true;
                return null;
            }
        }, clazz);
    }
}
