package org.niceassert;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class MatchingOverrideTest {
    private static final String ORIGINAL_VALUE = "original value";
    private static final String OVERRIDDEN_VALUE = "overridden value";
    private final AtomicBoolean originalTargetWasCalled = new AtomicBoolean(false);
    private final ARecordingObject originalTarget = new ARecordingObject();

    @Test(expected = AnException.class)
    public void overrideToThrowException() throws AnException {
        ARecordingObject proxy = new MatchingOverride<ARecordingObject>(originalTarget) {{
            throwException(new AnException()).whenCalling().aMethod();
        }}.proxy();

        proxy.aMethod();
        assertThat(originalTargetWasCalled.get(), is(false));
    }

    @Test
    public void overrideToReturnValue() throws AnException {
        ARecordingObject proxy = new MatchingOverride<ARecordingObject>(originalTarget) {{
            returnValue(OVERRIDDEN_VALUE).whenCalling().aMethod();
        }}.proxy();

        assertThat(proxy.aMethod(), is(equalTo(OVERRIDDEN_VALUE)));
        assertThat(originalTargetWasCalled.get(), is(false));
    }

    @Test
    public void overrideToReturnValueOnMatchedCallOnly() throws AnException {
        ARecordingObject proxy = new MatchingOverride<ARecordingObject>(originalTarget) {{
            returnValue(OVERRIDDEN_VALUE).whenCalling().methodWithArgs(OVERRIDDEN_VALUE);
        }}.proxy();

        assertThat(proxy.methodWithArgs(ORIGINAL_VALUE), is(equalTo(ORIGINAL_VALUE)));
        assertThat(originalTargetWasCalled.get(), is(false));
        assertThat(proxy.methodWithArgs(OVERRIDDEN_VALUE), is(equalTo(OVERRIDDEN_VALUE)));
    }

    @Test
    public void overrideToMatchMultipleCalls() throws AnException {
        ARecordingObject proxy = new MatchingOverride<ARecordingObject>(originalTarget) {{
            returnValue(null).whenCalling().methodWithArgs(ORIGINAL_VALUE);
            returnValue(OVERRIDDEN_VALUE).whenCalling().methodWithArgs(OVERRIDDEN_VALUE);
        }}.proxy();

        assertThat(proxy.methodWithArgs(ORIGINAL_VALUE), is(nullValue()));
        assertThat(originalTargetWasCalled.get(), is(false));
        assertThat(proxy.methodWithArgs(OVERRIDDEN_VALUE), is(equalTo(OVERRIDDEN_VALUE)));
    }

    @Test
    public void overrideToReturnValueOnMatchedCallOnlyUsingMatchers() throws AnException {
        ARecordingObject proxy = new MatchingOverride<ARecordingObject>(originalTarget) {{
            returnValue(OVERRIDDEN_VALUE).whenCalling().methodWithArgs(with(equalTo(OVERRIDDEN_VALUE)));
        }}.proxy();

        assertThat(proxy.methodWithArgs(ORIGINAL_VALUE), is(equalTo(ORIGINAL_VALUE)));
        assertThat(originalTargetWasCalled.get(), is(false));
        assertThat(proxy.methodWithArgs(OVERRIDDEN_VALUE), is(equalTo(OVERRIDDEN_VALUE)));
    }

    @Test(expected = ClassCastException.class)
    public void overrideToReturnIncompatibleValue() throws AnException {
        ARecordingObject proxy = new MatchingOverride<ARecordingObject>(originalTarget) {{
            returnValue(new Object()).whenCalling().aMethod();
        }}.proxy();

        proxy.aMethod();
    }

    @Test(expected = ClassCastException.class)
    public void overrideToReturnIncompatibleValueForVoidMethod() throws AnException {
        ARecordingObject proxy = new MatchingOverride<ARecordingObject>(originalTarget) {{
            returnValue(new Object()).whenCalling().aVoidMethod();
        }}.proxy();

        proxy.aVoidMethod();
    }

    @Test(expected = ClassCastException.class)
    public void overrideToThrowIncompatibleException() throws AnException {
        ARecordingObject proxy = new MatchingOverride<ARecordingObject>(originalTarget) {{
            throwException(new AnotherException()).whenCalling().aMethod();
        }}.proxy();

        proxy.aMethod();
    }

    @Test
    public void originalMethodCalledForNonOverriddenMethod() throws AnException {
        ARecordingObject proxy = new MatchingOverride<ARecordingObject>(originalTarget) {{
            returnValue(OVERRIDDEN_VALUE).whenCalling().anotherMethod();
        }}.proxy();

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