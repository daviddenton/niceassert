package org.niceassert;

import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class OverriderInvocationMatcher {
    private final List<Matcher> parameterMatchers = new ArrayList<Matcher>();
    private final ActionInvocationHandler actionInvocationHandler;
    private InvocationCounter invocationCounter;
    private Method aMethod;

    public OverriderInvocationMatcher(ActionInvocationHandler actionInvocationHandler) {
        this.actionInvocationHandler = actionInvocationHandler;
    }

    void addNextParameterMatcher(Matcher parameterMatcher) {
        this.parameterMatchers.add(parameterMatcher);
    }

    boolean isMethodCallMatched(Method method, Object[] objects) {
        if (!method.equals(aMethod)) {
            return false;
        }
        for (int i = 0; i < parameterMatchers.size(); i++) {
            if (!parameterMatchers.get(i).matches(objects[i])) return false;
        }

        return invocationCounter.thereAreInvocationsRemaining();
    }

    void recordInvocation(InvocationCounter counter, Method method, Object[] objects) {
        this.invocationCounter = counter;
        this.aMethod = method;
        if (parameterMatchers.isEmpty()) {
            for (Object object : objects) {
                parameterMatchers.add(equalTo(object));
            }
        }
    }

    Object processOverriddenCall(Method method, Object[] objects) throws Throwable {
        invocationCounter.count();
        return actionInvocationHandler.invoke(null, method, objects);
    }
}
