package org.niceassert;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.niceassert.Expected.*;

public class ExpectedTest {
    private static final String STRING = "String";

    @Test
    public void exceptionChecked() throws AnException {
        expect(exception(new AnException())).whenCalling(new AThrowingObject()).aMethod();
    }

    @Test
    public void exceptionCheckedWithCustomMatcher() throws AnException {
        expect(exception(is(equalTo(new AnException())))).whenCalling(new AThrowingObject()).aMethod();
    }

    @Test
    public void returnedValueChecked() {
        expect(returnedValue(STRING)).whenCalling(new AReturningObject()).aMethod();
    }

    @Test
    public void returnedValueCheckedUsingCustomAssert() {
        expect(returnedValue(is(STRING))).whenCalling(new AReturningObject()).aMethod();
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
