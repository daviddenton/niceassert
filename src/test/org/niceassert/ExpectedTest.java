package org.niceassert;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.niceassert.Expected.expect;

public class ExpectedTest {
    
    private static final String STRING = "String";
    private static final AnException AN_EXCEPTION = new AnException();

    @Test (expected = IllegalArgumentException.class)
    public void whenExceptionThrownAndNoExpectationSet() throws AnException {
        expect(new AThrowingObject()).whenCalling().aMethod();
    }

    @Test (expected = IllegalArgumentException.class)
    public void whenValueReturnedAndNoExpectationSet() throws AnException {
        expect(new AReturningObject()).whenCalling().aMethod();
    }

    @Test (expected = AssertionError.class)
    public void unexpectedException() throws AnException {
        expect(new AThrowingObject()).toReturnValue(STRING).whenCalling().aMethod();
    }


    @Test (expected = AssertionError.class)
    public void expectedExceptionNotThrown() throws AnException {
        expect(new AReturningObject()).toThrowException(AN_EXCEPTION).whenCalling().aMethod();
    }

    @Test (expected = IllegalArgumentException.class)
    public void attemptToSetSameExpectationTwice() throws AnException {
        expect(new AReturningObject()).toReturnValue(STRING).toReturnValue(STRING).whenCalling().aMethod();
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void attemptToSetCompetingExpectationTwice() throws AnException {
        expect(new AReturningObject()).toReturnValue(STRING).toThrowException(AN_EXCEPTION).whenCalling().aMethod();
    }

    @Test
    public void exceptionChecked() throws AnException {
        expect(new AThrowingObject()).toThrowException(AN_EXCEPTION).whenCalling().aMethod();
    }

    @Test
    public void exceptionCheckedWithCustomMatcher() throws AnException {
        expect(new AThrowingObject()).toThrowExceptionThat(is(equalTo(AN_EXCEPTION))).whenCalling().aMethod();
    }

    @Test
    public void returnedValueChecked() {
        expect(new AReturningObject()).toReturnValue(STRING).whenCalling().aMethod();
    }

    @Test
    public void returnedValueCheckedUsingCustomAssert() {
        expect(new AReturningObject()).toReturnValueThat(is(STRING)).whenCalling().aMethod();
    }

    private static class AThrowingObject {

        void aMethod() throws AnException {
            throw AN_EXCEPTION;
        }
    }

    private static class AnException extends Throwable {
        public boolean equals(Object obj) {
            return obj.getClass() == this.getClass();
        }
    }

    private static class AReturningObject {
        String aMethod() {
            return STRING;
        }
    }
}
