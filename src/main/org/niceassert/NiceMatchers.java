package org.niceassert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsInstanceOf;

public class NiceMatchers {

    private NiceMatchers() {
    }

    public static Matcher isInstanceOf(Class clazz) {
        return new IsInstanceOf(clazz);
    }

    public static Matcher isAssignableFrom(final Class clazz) {
        return new BaseMatcher<Class>() {
            public boolean matches(Object o) {
                return Class.class == o.getClass() && clazz.isAssignableFrom((Class)o);
            }

            public void describeTo(Description description) {
                description.appendText(clazz.getName());
            }
        };
    }
}
