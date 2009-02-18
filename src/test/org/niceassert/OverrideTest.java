package org.niceassert;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.niceassert.Override.returnValue;
import static org.niceassert.Override.throwException;

import java.util.concurrent.atomic.AtomicBoolean;

public class OverrideTest {
    private static final String ORIGINAL_VALUE = "original value";
    private static final String OVERRIDDEN_STRING = "overridden value";
    private final AtomicBoolean wasCalled = new AtomicBoolean(false);
    private final ARecordingObject target = new ARecordingObject();

    @Test(expected = AnException.class)
    public void throwExceptions() throws AnException {
        throwException(new AnException()).whenCalling(target).aMethod();
        new ARecordingObject().aMethod();
        assertThat(wasCalled.get(), is(false));
    }

    @Test
    public void returnValueOccursForOverriddenMethod() throws AnException {
        returnValue(OVERRIDDEN_STRING).whenCalling(target).aMethod();
        assertThat(target.aMethod(), is(equalTo(OVERRIDDEN_STRING)));
        assertThat(wasCalled.get(), is(false));
    }

    @Test
    public void originalMethodCalledForNonOverriddenMethod() throws AnException {
        returnValue(OVERRIDDEN_STRING).whenCalling(target).anotherMethod();
        assertThat(target.aMethod(), is(equalTo(ORIGINAL_VALUE)));
        assertThat(wasCalled.get(), is(true));
    }


    public class ARecordingObject {
        String aMethod() {
            wasCalled.set(true);
            return ORIGINAL_VALUE;
        }

        String anotherMethod() {
            return ORIGINAL_VALUE;
        }
    }

    private class AnException extends Throwable {
        public boolean equals(Object obj) {
            return obj.getClass() == this.getClass();
        }
    }
}