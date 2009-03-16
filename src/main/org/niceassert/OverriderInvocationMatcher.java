package org.niceassert;

import static org.hamcrest.CoreMatchers.equalTo;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class OverriderInvocationMatcher {
    private final List<Matcher> parameterMatchers = new ArrayList<Matcher>();
    private InvocationCounter invocationCounter;
    private Method aMethod;
    private Action action;

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

    void setAction(Action action) {
        this.action = action;
    }

    Object processOverriddenCall(Method method, Object[] objects) throws Throwable {
        invocationCounter.count();
        try {
            Object result = action.execute(objects);
            validateReturnValueCompatability(aMethod.getReturnType(), result);
            return result;
        } catch (Throwable throwable) {
            for (Class exceptionClass : method.getExceptionTypes()) {
                if (exceptionClass.isAssignableFrom(throwable.getClass())) throw throwable;
            }
            throw new ClassCastException("Can't override method " + aMethod.getName() + " to throw incompatible exception " + throwable.getClass());
        }
    }

    private void validateReturnValueCompatability(Class<?> expected, Object returnValue) {
        if (returnValue == null) return;
        if (!expected.isAssignableFrom(returnValue.getClass()))
            throw new ClassCastException("Can't override method to return incompatible class (expected=" + expected + ", got=" + returnValue + ")");
    }
}
