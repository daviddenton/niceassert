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
    private final AtomicBoolean originalTargetWasCalled = new AtomicBoolean(false);
    private final ARecordingObject originalTarget = new ARecordingObject();
    private final ARecordingObject proxy = modifyForOverride(originalTarget);

    @Test(expected = AnException.class)
    public void overrideToThrowException() throws AnException {
        override(proxy).to(throwException(new AnException())).whenCalling().aMethod();
        proxy.aMethod();
        assertThat(originalTargetWasCalled.get(), is(false));
    }

    @Test
    public void overrideToReturnValue() throws AnException {
        override(proxy).to(returnValue(OVERRIDDEN_STRING)).whenCalling().aMethod();
        assertThat(proxy.aMethod(), is(equalTo(OVERRIDDEN_STRING)));
        assertThat(originalTargetWasCalled.get(), is(false));
    }

    @Test
    public void overrideToReturnValueOnMatchedCallOnly() throws AnException {
        override(proxy).to(returnValue(OVERRIDDEN_STRING)).whenCalling().methodWithArgs(OVERRIDDEN_STRING);
        assertThat(proxy.aMethod(), is(equalTo(ORIGINAL_VALUE)));
        assertThat(originalTargetWasCalled.get(), is(false));
        assertThat(proxy.aMethod(), is(equalTo(OVERRIDDEN_STRING)));
    }

    @Test(expected = ClassCastException.class)
    public void overrideToReturnIncompatibleValue() throws AnException {
        override(proxy).to(returnValue(new Object())).whenCalling().aMethod();
        proxy.aMethod();
    }

    @Test(expected = ClassCastException.class)
    public void overrideToReturnIncompatibleValueForVoidMethod() throws AnException {
        override(proxy).to(returnValue(new Object())).whenCalling().aVoidMethod();
        proxy.aVoidMethod();
    }

    @Test(expected = ClassCastException.class)
    public void overrideToThrowIncompatibleException() throws AnException {
        override(proxy).to(throwException(new AnotherException())).whenCalling().aMethod();
        proxy.aMethod();
    }

    // TODO: MORE INVALID CASES - non setup causing weird results - check args list and record against action - use matchers as in JMock

    @Test(expected = IllegalArgumentException.class)
    public void attemptToOverrideANonModifiedObject() throws AnException {
        override(originalTarget);
    }

    @Test
    public void originalMethodCalledForNonOverriddenMethod() throws AnException {
        override(proxy).to(returnValue(OVERRIDDEN_STRING)).whenCalling().anotherMethod();
        assertThat(proxy.aMethod(), is(equalTo(ORIGINAL_VALUE)));
        assertThat(originalTargetWasCalled.get(), is(true));
    }


    private class ARecordingObject {
        String aMethod() throws AnException {
            originalTargetWasCalled.set(true);
            return ORIGINAL_VALUE;
        }

        String anotherMethod() {
            return ORIGINAL_VALUE;
        }

        String methodWithArgs(String arg) {
            return ORIGINAL_VALUE;
        }

        void aVoidMethod() {
        }
    }

    private class AnException extends Throwable {
        public boolean equals(Object obj) {
            return obj.getClass() == this.getClass();
        }
    }

    private class AnotherException extends Throwable {
    }
}