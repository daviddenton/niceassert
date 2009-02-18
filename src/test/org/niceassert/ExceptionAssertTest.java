package org.niceassert;

import org.junit.Test;
import static org.niceassert.ExceptionAssert.*;

public class ExceptionAssertTest {
    private static final String STRING = "String";

    @Test
    public void exceptionTest() {
        
        expect(exception(new RuntimeException())).whenCalling(new AThrowingObject()).aMethod();
    }

    @Test
    public void returnedValueTest() {
        expect(returnedValue(STRING)).whenCalling(new AReturningObject()).aMethod();
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
