package org.niceassert;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.niceassert.NiceMatchers.isAssignableFrom;
import static org.niceassert.NiceMatchers.isInstanceOf;

public class NiceMatchersTest {

    @Test
    public void isInstanceOfMatch() {
        assertThat(isInstanceOf(Object.class).matches(Object.class), is(true));
    }

    @Test
    public void isInstanceOfNoMatch() {
        assertThat(isInstanceOf(String.class).matches(Object.class), is(false));
    }
    
    @Test
    public void isAssignableFromMatch() {
        assertThat(isAssignableFrom(Object.class).matches(String.class), is(true));
    }

    @Test
    public void isAssignableFromNoMatch() {
        assertThat(isAssignableFrom(String.class).matches(Integer.class), is(false));
    }
}