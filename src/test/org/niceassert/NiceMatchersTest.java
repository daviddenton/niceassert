package org.niceassert;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.niceassert.NiceMatchers.*;

import java.util.Date;

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
    
    @Test
    public void isBeforeMatch() {
        assertThat(isBefore(new Date(1)).matches(new Date(0)), is(true));
    }

    @Test
    public void isBeforeNoMatch() {
        assertThat(isBefore(new Date(1)).matches(new Date(1)), is(false));
        assertThat(isBefore(new Date(1)).matches(new Date(2)), is(false));
    }

    @Test
    public void isAfterMatch() {
        assertThat(isAfter(new Date(0)).matches(new Date(1)), is(true));
    }

    @Test
    public void isAfterNoMatch() {
        assertThat(isAfter(new Date(1)).matches(new Date(1)), is(false));
        assertThat(isAfter(new Date(2)).matches(new Date(1)), is(false));
    }

    @Test (expected = IllegalArgumentException.class)
    public void isBetweenIllegalRange() {
        isBetween(new Date(2), new Date(1));
    }

    @Test
    public void isBetweenMatch() {
        assertThat(isBetween(new Date(1), new Date(2)).matches(new Date(1)), is(true));
        assertThat(isBetween(new Date(1), new Date(2)).matches(new Date(2)), is(true));
    }

    @Test
    public void isBetweenNoMatch() {
        assertThat(isBetween(new Date(1), new Date(2)).matches(new Date(0)), is(false));
        assertThat(isBetween(new Date(1), new Date(2)).matches(new Date(3)), is(false));
    }
}