package org.niceassert.example;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.niceassert.Expectation.expect;


public class ExpectationExampleTest {
    
    private static final String A_RETURNED_VALUE = "The returned value";
    private static final AnException AN_EXCEPTION = new AnException();

    @Test
    public void exactValueIsReturned() {
        expect(new AReturningObject()).toReturn(A_RETURNED_VALUE).whenCalling().aMethod();
    }

    @Test
    public void matchedValueIsReturned() {
        expect(new AReturningObject()).toReturnValueThat(is(any(String.class))).whenCalling().aMethod();
    }

    @Test
    public void exactExceptionIsThrown() throws AnException {
        expect(new AnExceptionThrowingObject()).toThrow(AN_EXCEPTION).whenCalling().aMethod();
    }

    @Test
    public void matchedExceptionIsThrown() throws AnException {
       expect(new AnExceptionThrowingObject()).toThrowExceptionThat(is(any(Throwable.class))).whenCalling().aMethod();
    }

    public static class AnExceptionThrowingObject {
        public void aMethod() throws AnException {
            throw AN_EXCEPTION;
        }
    }

    public static class AReturningObject {
        public String aMethod() {
            return A_RETURNED_VALUE;
        }
    }

    public static class AnException extends Throwable {
        public boolean equals(Object obj) {
            return obj.getClass() == this.getClass();
        }

    }
}
