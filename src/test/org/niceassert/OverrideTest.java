package org.niceassert;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.niceassert.Override.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class OverrideTest {
    private static final String ORIGINAL_VALUE = "original value";
    private static final String OVERRIDDEN_STRING = "overridden value";
    private final AtomicBoolean wasCalled = new AtomicBoolean(false);
    private final ARecordingObject proxiedObject = new ARecordingObject();
    private final ARecordingObject theTarget = modifyForOverride(proxiedObject);

    // override(target) - returns Object
    // throwException(blah).whenCalling(target).method();
    @Test(expected = AnException.class)
    public void throwExceptions() throws AnException {

        throwException(new AnException()).whenCalling(theTarget).aMethod();
        theTarget.aMethod();
        assertThat(wasCalled.get(), is(false));
    }

    @Test
    public void returnValueOccursForOverriddenMethod() throws AnException {
        returnValue(OVERRIDDEN_STRING).whenCalling(proxiedObject).aMethod();
        assertThat(proxiedObject.aMethod(), is(equalTo(OVERRIDDEN_STRING)));
        assertThat(wasCalled.get(), is(false));
    }

    @Test
    public void originalMethodCalledForNonOverriddenMethod() throws AnException {
        returnValue(OVERRIDDEN_STRING).whenCalling(proxiedObject).anotherMethod();
        assertThat(proxiedObject.aMethod(), is(equalTo(ORIGINAL_VALUE)));
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