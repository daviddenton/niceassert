package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Expected {

    public static <T> ExpectationBuilder<T> expect(final  T target) {
        return new ExpectationBuilder<T>(target);
    }

    public static Matcher throwException(final Throwable t) {
        return new BaseMatcher<Throwable>() {

            public boolean matches(Object o) {
                return o.getClass().equals(t.getClass());
            }

            public void describeTo(Description description) {
                description.appendText(t.getClass().getName());
            }
        };
    }

    public static <T> Matcher<T> returnValue(final T t) {
        return new BaseMatcher<T>() {
            public boolean matches(Object o) {
                return o.equals(t);
            }

            public void describeTo(Description description) {
                description.appendText(" but got: " + t);
            }
        };
    }

    public static Matcher returnValueThat(final Matcher matcher) {
        return matcher;
    }

    public static Matcher throwExceptionThat(final Matcher matcher) {
        return matcher;
    }

    public static class ExpectationBuilder<T> {
        private final T target;
        private Matcher matcher;

        ExpectationBuilder(T target) {
            this.target = target;
        }

        public ExpectationBuilder<T> to(Matcher matcher) {
            this.matcher = matcher;
            return this;
        }

        public T whenCalling() {
            return (T) ConcreteClassProxyFactory.INSTANCE.proxyFor(new InvocationHandler() {
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
            }, target.getClass());
        }
    }
}
