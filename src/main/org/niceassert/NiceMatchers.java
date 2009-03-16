package org.niceassert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;

import java.util.Date;

public class NiceMatchers {

    private NiceMatchers() {
    }

    public static Matcher isInstanceOf(Class clazz) {
        return new IsInstanceOf(clazz);
    }

    public static Matcher<Class> isAssignableFrom(final Class clazz) {
        return new BaseMatcher<Class>() {
            public boolean matches(Object o) {
                return Class.class == o.getClass() && clazz.isAssignableFrom((Class)o);
            }

            public void describeTo(Description description) {
                description.appendText(clazz.getName());
            }
        };
    }

    public static Matcher<Date> isBefore(final Date date) {
        return new BaseMatcher<Date>() {

            public boolean matches(Object o) {
                return ((Date)o).before(date);
            }

            public void describeTo(Description description) {
                description.appendText(date.toString());
            }
        };
    }

    public static Matcher<Date> isAfter(final Date date) {
        return new BaseMatcher<Date>() {

            public boolean matches(Object o) {
                return ((Date)o).after(date);
            }

            public void describeTo(Description description) {
                description.appendText(date.toString());
            }
        };
    }
}
