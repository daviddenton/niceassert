package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An readable assertion API used to check the expected behaviour of a particular method call.
 * <p/>
 * The expected behaviour of the target call is built up using the DSL of this class. Assertions can
 * be made on either a return value, or an exception thrown.
 * <p/>
 * For usage examples, please see the wiki documentation at: http://code.google.com/p/niceassert/
 *
 * @param <T> the class that's behaviour is being checked.
 */
public class Expectation<T> {

    public static <T> Expectation<T> expect(final T target) {
        return new Expectation<T>(target);
    }

    private final T target;
    private Matcher successMatcher = new FailureMatcher("Exception to be thrown");
    private Matcher failureMatcher = new FailureMatcher("No exception to be thrown");
    private boolean matcherSet;

    private Expectation(T target) {
        this.target = target;
    }

    private interface ExpectedAction {
        void setOn(Expectation expectation);
    }

    public Expectation<T> to(ExpectedAction action) {
        validateExpectedBehaviour();
        action.setOn(this);
        return this;
    }

    public static <T> ExpectedAction returnValue(final T returnValue) {
        return returnValueThat(new BaseMatcher<T>() {
            public boolean matches(Object o) {
                return o.equals(returnValue);
            }

            public void describeTo(Description description) {
                description.appendText(" but got: " + returnValue);
            }
        });
    }

    public static ExpectedAction returnValueThat(final Matcher matcher) {
        return new ExpectedAction() {
            public void setOn(Expectation expectation) {
                expectation.successMatcher = matcher;
            }
        };
    }

    public static ExpectedAction throwExceptionThat(final Matcher matcher) {
        return new ExpectedAction() {
            public void setOn(Expectation expectation) {
                expectation.failureMatcher = matcher;
            }
        };
    }

    public static ExpectedAction throwException(final Throwable t) {
        return throwExceptionThat(new BaseMatcher<Throwable>() {
            public boolean matches(Object o) {
                return o.getClass().equals(t.getClass());
            }

            public void describeTo(Description description) {
                description.appendText(t.getClass().getName());
            }
        });
    }

    public static ExpectedAction resultIn(final Matcher matcher) {
        return new ExpectedAction() {
            public void setOn(Expectation expectation) {
                expectation.successMatcher = matcher;
            }
        };
    }

    private void validateExpectedBehaviour() {
        if (matcherSet) throw new IllegalArgumentException("Expected behaviour already set");
        this.matcherSet = true;
    }

    public T whenCalling() {

        if (!matcherSet) throw new IllegalArgumentException("No expectation set");
        return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
            public Object invoke(Object object, Method method, Object[] args) throws Throwable {
                try {
                    final Object o = method.invoke(target, args);
                    assertThat(o, successMatcher);
                    return o;
                } catch (InvocationTargetException e) {
                    assertThat(e.getTargetException(), failureMatcher);
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
            return false;
        }

        public void describeTo(Description description) {
            description.appendText(message);
        }
    }
}
