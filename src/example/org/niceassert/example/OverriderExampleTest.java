package org.niceassert.example;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.niceassert.Overrider;

public class OverriderExampleTest {
    private static final String ORIGINAL_VALUE = "original value";
    private static final String OVERRIDDEN_VALUE = "overridden value";

    @Test (expected = AnException.class)
    public void overrideToThrowException() throws AnException {
        MyObject proxy = new Overrider<MyObject>(new MyObject()) {{
            will(throwException(new AnException())).whenCalling().aMethod();
        }}.proxy();

        proxy.aMethod();
    }

    @Test
    public void overrideToReturnValue() throws AnException {
        MyObject proxy = new Overrider<MyObject>(new MyObject()) {{
            will(returnValue(OVERRIDDEN_VALUE)).whenCalling().aMethod();
        }}.proxy();

        assertThat(proxy.aMethod(), is(equalTo(OVERRIDDEN_VALUE)));
    }

    @Test
    public void overrideToReturnValueOnMatchedCallOnly() throws AnException {
        MyObject proxy = new Overrider<MyObject>(new MyObject()) {{
            will(returnValue(OVERRIDDEN_VALUE)).whenCalling().methodWithArgs(OVERRIDDEN_VALUE);
        }}.proxy();

        assertThat(proxy.methodWithArgs(OVERRIDDEN_VALUE), is(equalTo(OVERRIDDEN_VALUE)));
        assertThat(proxy.methodWithArgs(ORIGINAL_VALUE), is(equalTo(ORIGINAL_VALUE)));
    }

    @Test
    public void overrideToMatchMultipleCalls() throws AnException {
        MyObject proxy = new Overrider<MyObject>(new MyObject()) {{
            will(returnValue(null)).whenCalling(2).methodWithArgs(ORIGINAL_VALUE);
            will(returnValue(OVERRIDDEN_VALUE)).whenCalling().methodWithArgs(OVERRIDDEN_VALUE);
        }}.proxy();

        assertThat(proxy.methodWithArgs(ORIGINAL_VALUE), is(nullValue()));
        assertThat(proxy.methodWithArgs(ORIGINAL_VALUE), is(nullValue()));
        assertThat(proxy.methodWithArgs(OVERRIDDEN_VALUE), is(equalTo(OVERRIDDEN_VALUE)));
    }

    @Test
    public void overrideToReturnValueOnMatchedCallOnlyUsingMatchers() throws AnException {
        MyObject proxy = new Overrider<MyObject>(new MyObject()) {{
            will(returnValue(OVERRIDDEN_VALUE)).whenCalling().methodWithArgs(with(equalTo(OVERRIDDEN_VALUE)));
        }}.proxy();

        assertThat(proxy.methodWithArgs(OVERRIDDEN_VALUE), is(equalTo(OVERRIDDEN_VALUE)));
        assertThat(proxy.methodWithArgs(ORIGINAL_VALUE), is(equalTo(ORIGINAL_VALUE)));
    }

    public static class MyObject {
        public String aMethod() throws AnException {
            return ORIGINAL_VALUE;
        }

        public String methodWithArgs(String arg) throws AnException {
            return ORIGINAL_VALUE;
        }
    }

    private class AnException extends Throwable {
        public boolean equals(Object obj) {
            return obj.getClass() == this.getClass();
        }
    }
}