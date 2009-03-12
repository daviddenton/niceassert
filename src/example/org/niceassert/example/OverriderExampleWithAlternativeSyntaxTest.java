package org.niceassert.example;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.niceassert.Overrider;
import static org.niceassert.Overrider.*;

public class OverriderExampleWithAlternativeSyntaxTest {
    private static final String ORIGINAL_VALUE = "original value";
    private static final String OVERRIDDEN_VALUE = "overridden value";
    private final Overrider<MyObject> override = wrapForOverride(new MyObject());

    @Test(expected = AnException.class)
    public void overrideToThrowException() throws AnException {
        override.will(throwException(new AnException())).whenCalling().methodWithArgs(OVERRIDDEN_VALUE);
        override.proxy().methodWithArgs(OVERRIDDEN_VALUE);
    }

    @Test
    public void overrideToReturnValue() throws AnException {
        override.will(returnValue(OVERRIDDEN_VALUE)).whenCalling().aMethod();
        assertThat(override.proxy().aMethod(), is(equalTo(OVERRIDDEN_VALUE)));
    }

    @Test
    public void overrideToReturnValueOnMatchedCallOnly() throws AnException {
        override.will(returnValue(OVERRIDDEN_VALUE)).whenCalling().methodWithArgs(OVERRIDDEN_VALUE);

        assertThat(override.proxy().methodWithArgs(OVERRIDDEN_VALUE), is(equalTo(OVERRIDDEN_VALUE)));
        assertThat(override.proxy().methodWithArgs(ORIGINAL_VALUE), is(equalTo(ORIGINAL_VALUE)));
    }

    @Test
    public void overrideToMatchMultipleCalls() throws AnException {
        override.will(returnValue(null)).whenCalling().methodWithArgs(ORIGINAL_VALUE);
        override.will(returnValue(OVERRIDDEN_VALUE)).whenCalling().methodWithArgs(OVERRIDDEN_VALUE);

        assertThat(override.proxy().methodWithArgs(ORIGINAL_VALUE), is(nullValue()));
        assertThat(override.proxy().methodWithArgs(OVERRIDDEN_VALUE), is(equalTo(OVERRIDDEN_VALUE)));
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