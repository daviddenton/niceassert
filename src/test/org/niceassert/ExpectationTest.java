package org.niceassert;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import static org.niceassert.Expectation.*;

public class ExpectationTest {
    
    private static final String RESULT = "String";
    private static final AnException AN_EXCEPTION = new AnException();

    @Test (expected = AssertionError.class)
    public void whenExceptionThrownAndNoExpectationSet() throws AnException {
        expect(new AThrowingObject()).to(returnValue(RESULT)).whenCalling().aMethod();
    }

    @Test (expected = IllegalArgumentException.class)
    public void whenValueReturnedAndNoExpectationSet() throws AnException {
        expect(new AReturningObject()).whenCalling().aMethod();
    }

    @Test (expected = AssertionError.class)
    public void unexpectedException() throws AnException {
        expect(new AThrowingObject()).to(returnValue(RESULT)).whenCalling().aMethod();
    }

    @Test (expected = AssertionError.class)
    public void expectedExceptionNotThrown() throws AnException {
        expect(new AReturningObject()).to(throwException(AN_EXCEPTION)).whenCalling().aMethod();
    }

    @Test (expected = IllegalArgumentException.class)
    public void attemptToSetSameExpectationTwice() throws AnException {
        expect(new AReturningObject()).to(returnValue(RESULT)).to(returnValue(RESULT)).whenCalling().aMethod();
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void attemptToSetCompetingExpectationTwice() throws AnException {
        expect(new AReturningObject()).to(returnValue(RESULT)).to(throwException(AN_EXCEPTION)).whenCalling().aMethod();
    }

    @Test
    public void exceptionChecked() throws AnException {
        expect(new AThrowingObject()).to(throwException(AN_EXCEPTION)).whenCalling().aMethod();
    }

    @Test
    public void exceptionCheckedWithCustomMatcher() throws AnException {
        expect(new AThrowingObject()).to(throwExceptionThat(is(equalTo(AN_EXCEPTION)))).whenCalling().aMethod();
        expect(new AThrowingObject()).to(throwExceptionThat(is(any(Exception.class)))).whenCalling().aMethod();
    }

    @Test
    public void returnedValueChecked() {
        expect(new AReturningObject()).to(returnValue(RESULT)).whenCalling().aMethod();
    }

    @Test
    public void returnedValueCheckedUsingCustomAssert() {
        expect(new AReturningObject()).to(returnValueThat(is(RESULT))).whenCalling().aMethod();
    }

    private class AThrowingObject {
        void aMethod() throws AnException {
            throw AN_EXCEPTION;
        }
    }

    private static class AnException extends Throwable {
        public boolean equals(Object obj) {
            return obj.getClass() == this.getClass();
        }
    }

    private class AReturningObject {
        String aMethod() {
            return RESULT;
        }
    }
}
