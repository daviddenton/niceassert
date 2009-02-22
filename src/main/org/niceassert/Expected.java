package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Expected<T> {

    public static <T> Expected<T> expect(final T target) {
        return new Expected<T>(target);
    }

    private final T target;
    private Matcher returnMatcher = new FailureMatcher("Exception to be thrown");
    private Matcher throwsMatcher = new FailureMatcher("No exception to be thrown");
    private boolean matcherSet;

    private Expected(T target) {
        this.target = target;
    }

    public Expected<T> toThrowExceptionThat(Matcher matcher) {
        validateExpectedBehaviour();
        this.throwsMatcher = matcher;
        return this;
    }

    public Expected<T> toReturnValueThat(Matcher matcher) {
        validateExpectedBehaviour();
        this.returnMatcher = matcher;
        return this;
    }

    private void validateExpectedBehaviour() {
        if(matcherSet) throw new IllegalArgumentException("Expected behaviour already set");
        this.matcherSet = true;
    }

    public Expected<T> toThrowException(final Throwable t) {
        toThrowExceptionThat(new BaseMatcher<Throwable>() {

            public boolean matches(Object o) {
                return o.getClass().equals(t.getClass());
            }

            public void describeTo(Description description) {
                description.appendText(t.getClass().getName());
            }
        });
        return this;
    }

    public Expected<T> toReturnValue(final Object t) {
        toReturnValueThat(new BaseMatcher<T>() {
            public boolean matches(Object o) {
                return o.equals(t);
            }

            public void describeTo(Description description) {
                description.appendText(" but got: " + t);
            }
        });
        return this;
    }

    public T whenCalling() {

        if(!matcherSet) throw new IllegalArgumentException("No expectation set");
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            public Object invoke(Object object, Method method, Object[] args) throws Throwable {
                try {
                    final Object o = method.invoke(target, args);
                    assertThat(o, returnMatcher);
                    return o;
                } catch (InvocationTargetException e) {
                    assertThat(e.getTargetException(), throwsMatcher);
                }
                return null;
            }
        }, target.getClass());
    }

    private static class FailureMatcher extends BaseMatcher {
        private final String message;

        public FailureMatcher(String message) {
            this.message = message;
        }

        public boolean matches(Object o) {
            return false;        }

        public void describeTo(Description description) {
            description.appendText(message);
        }
    }
}
