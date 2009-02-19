package org.niceassert;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.niceassert.Expected.*;

public class ExpectedTest {
    private static final String STRING = "String";

    @Test
    public void exceptionChecked() throws AnException {
        expect(new AThrowingObject()).to(throwException(new AnException())).whenCalling().aMethod();
    }

    @Test
    public void exceptionCheckedWithCustomMatcher() throws AnException {
        expect(new AThrowingObject()).to(throwExceptionThat(is(equalTo(new AnException())))).whenCalling().aMethod();
    }

    @Test
    public void returnedValueChecked() {
        expect(new AReturningObject()).to(returnValue(STRING)).whenCalling().aMethod();
    }

    @Test
    public void returnedValueCheckedUsingCustomAssert() {
        expect(new AReturningObject()).to(returnValueThat(is(STRING))).whenCalling().aMethod();
    }


    public static class AThrowingObject {
        void aMethod() throws AnException {
            throw new AnException();
        }
    }

    private static class AnException extends Throwable {
        public boolean equals(Object obj) {
            return obj.getClass() == this.getClass();
        }
    }

    public static class AReturningObject {
        String aMethod() {
            return STRING;
        }
    }
}
