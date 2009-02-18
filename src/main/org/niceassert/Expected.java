package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Expected {

    public static ExpectationBuilder expect(Matcher matcher) {
        return new ExpectationBuilder(matcher);
    }

    public static Matcher exception(final Throwable t) {
        return new BaseMatcher<Throwable>() {

            public boolean matches(Object o) {
                return o.getClass().equals(t.getClass());
            }

            public void describeTo(Description description) {
                description.appendText(t.getClass().getName());
            }
        };
    }

    public static <T> Matcher<T> returnedValue(final T t) {
        return new BaseMatcher<T>() {
            public boolean matches(Object o) {
                return o.equals(t);
            }

            public void describeTo(Description description) {
                description.appendText(" but got: " + t);
            }
        };
    }

    public static Matcher returnedValue(final Matcher matcher) {
        return matcher;
    }

    public static Matcher exception(final Matcher matcher) {
        return matcher;
    }

    public static class ExpectationBuilder {
        private final Matcher matcher;

        ExpectationBuilder(Matcher matcher) {
            this.matcher = matcher;
        }

        public <T> T whenCalling(final T target) {
            return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(target.getClass(), new InvocationHandler() {
                public Object invoke(Object object, Method method, Object[] args) throws Throwable {
                    try {
                        final Object o = method.invoke(target, args);
                        org.junit.Assert.assertThat(o, matcher);
                        return o;
                    } catch (InvocationTargetException e) {
                        org.junit.Assert.assertThat(e.getTargetException(), matcher);
                    }
                    return null;
                }
            });
        }
    }
}
