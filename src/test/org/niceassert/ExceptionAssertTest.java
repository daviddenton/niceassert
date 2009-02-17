package org.niceassert;

import org.junit.Test;
import static org.niceassert.ExceptionAssert.expect;

public class ExceptionAssertTest {
    private static final String STRING = "String";

    @Test
    public void exception() {
        expect(ExceptionAssert.exception(new RuntimeException())).whenCalling(new AThrowingObject()).aMethod();
    }

    @Test
    public void returnedValue() {
        expect(ExceptionAssert.returnedValue(STRING)).whenCalling(new AReturningObject()).aMethod();
    }


    public static class AThrowingObject {
        void aMethod() {
            throw new RuntimeException();
        }
    }
    public static class AReturningObject {
        String aMethod() {
            return STRING;
        }
    }


}
