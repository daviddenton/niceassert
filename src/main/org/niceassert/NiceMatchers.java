package org.niceassert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;

import java.util.Date;

public class NiceMatchers {

    private NiceMatchers() {
    }

    public static Matcher instanceOf(Class clazz) {
        return new IsInstanceOf(clazz);
    }

    public static Matcher<Class> assignableFrom(final Class clazz) {
        return new BaseMatcher<Class>() {
            public boolean matches(Object o) {
                return Class.class == o.getClass() && clazz.isAssignableFrom((Class)o);
            }

            public void describeTo(Description description) {
                description.appendText(clazz.getName());
            }
        };
    }

    public static Matcher<Date> before(final Date date) {
        return new BaseMatcher<Date>() {

            public boolean matches(Object o) {
                return ((Date)o).before(date);
            }

            public void describeTo(Description description) {
                description.appendText(date.toString());
            }
        };
    }

    public static Matcher<Date> after(final Date date) {
        return new BaseMatcher<Date>() {

            public boolean matches(Object o) {
                return ((Date)o).after(date);
            }

            public void describeTo(Description description) {
                description.appendText(date.toString());
            }
        };
    }

    public static Matcher<Date> between(final Date date1, final Date date2) {
        if(date1.after(date2)) throw new IllegalArgumentException("Illegal range");
        return new BaseMatcher<Date>() {

            public boolean matches(Object o) {
                return !((Date)o).before(date1) && !((Date)o).after(date2);
            }

            public void describeTo(Description description) {
                description.appendText("between " + date1.toString() + " and " + date2.toString());
            }
        };
    }
}
