package org.niceassert;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.niceassert.Override.returnValue;
import static org.niceassert.Override.throwException;

public class OverrideTest {
    private static final String ORIGINAL_VALUE = "original value";
    private static final String OVERRIDDEN_STRING = "overridden value";

    private final AReturningObject target = new AReturningObject();

    @Test(expected = AnException.class)
    public void throwExceptions() throws AnException {
        throwException(new AnException()).whenCalling(target).aMethod();
        target.aMethod();
    }

    @Test
    public void returnValueOccursForOverridenMethod() throws AnException {
        returnValue(OVERRIDDEN_STRING).whenCalling(target).aMethod();
        assertThat(target.aMethod(), is(equalTo(OVERRIDDEN_STRING)));
    }

    @Test
    public void proxiedObjectCalledForNonOverridenMethod() throws AnException {
        returnValue(OVERRIDDEN_STRING).whenCalling(target).anotherMethod();
        assertThat(target.aMethod(), is(equalTo(ORIGINAL_VALUE)));
    }

    public static class AReturningObject {
        String aMethod() {
            return ORIGINAL_VALUE;
        }

        String anotherMethod() {
            return ORIGINAL_VALUE;
        }
    }

    private static class AnException extends Throwable {
        public boolean equals(Object obj) {
            return obj.getClass() == this.getClass();
        }
    }
}