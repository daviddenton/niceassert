
The Niceassert library defines a few useful Java tools designed to aid test readability by reducing the code overhead of various common testing patterns. It works well alongside existing widely-used testing frameworks (especially JUnit4/JMock2 with which it shares support library requirements).

*Features:*

 * [Expectations] - a concise way to express expected behaviour of a method call    
 * [Overriding] - partially mock the behaviour of an object
 * Matchers - a collection of various useful Hamcrest Matchers


Problem
JUnit 4 introduced the concept of being able to add an assert that a given exception has been thrown during the course of a test, as a replacement for the repetition of the standard try-test-fail-catch-pass loop seen below:

// JUnit 3
public void testExceptionalThing() { 
    try {
        new ObjectUnderTest().doSomethingThatCausesAnException();
        fail();
    } catch (AnExpectedException e) {
        // no assertion here guv!
    }
}

// JUnit 4
@Test (expected = AnExpectedException.class)
public void exceptionalThing() { 
    new ObjectUnderTest().doSomethingThatCausesAnException();
}
However, there is a trade-off for the reduction in verbosity - you lose the ability to perform any additional assertions on the thrown exception.

Solution
Niceassert uses a more natural language structure which allows you to combine the density of the JUnit4 case with the ability to assert on the result of any method call (using the awesome Hamcrest library). For consistency, you can also use it perform assertions on the result value of a call or to invoke a custom Matcher. The setup assertion is performed "inline" by the call at the end of the structure and a standard AssertionError raised if it is not matched:

expect(new AReturningObject()).to(throwException(AN_EXCEPTION)).whenCalling().aMethod("INVALID ARGUMENT");
// or:
expect(new AThrowingObject()).to(returnValue(RESULT)).whenCalling().aMethod("ARGUMENT");
// or:
expect(new AWorldUpdatingObject()).to(resultIn(new CustomMatcherToCheckStateOfTheWorld())).whenCalling().aMethod("ARGUMENT");
For example code, please see the examples.

Problem
In order to assert or recreate a particular behaviour, it is often required (although not necessarily desirable) to override methods in a particular concrete class, which results in test code that looks similar to:

Object objectToOverride = new Object() {
    public String toString() {
        return "KNOWN RESULT";
    }
};

assertThat(objectToOverride.toString(),is(equalTo("KNOWN RESULT")));
In the case that more than one method is overridden the readability of the test code is badly affected. In the case that you are working with source code that pre-dates/doesn't use the "Override" annotation, removing the method in the overridden class may even leave the overridden method orphaned.

Solution
Niceassert provides not just a mechanism to implement the same thing in a compact format, but also to be able to use Hamcrest parameter matching in which the proxy will fall through to the delegate implementation if the invocation doesn't match.

There are 2 methods available to override behaviour and obtain access to the proxy. The first uses a JMock2-style setup block:

AnObject proxy = new Override<AnObject>(target) {{
    will(returnValue("a different value")).whenCalling().aMethod("ARGUMENT");
    will(throw(new RuntimeException())).whenCalling().aMethod("ARGUMENT");
}}.proxy();
The other method is more compact, but requires an initial "wrapping" mechanism and only supports exact argument matching (ie. no Hamcrest matchers). Some may find it a little less readable in trade off for the compactness:

Overrider<AnObject> overrider = Overrider.wrapForOverride(originalTarget);
overrider.will(returnValue(OVERRIDDEN_VALUE)).whenCalling().aMethod("ARGUMENT"));
overrider.will(throw(new RuntimeException())).whenCalling().aMethod("INVALID ARGUMENT");
AnObject proxy = overrider.proxy();
Regardless of how the proxy is created, you can then just call it in place of the original target:

proxy.methodToOverride("DIFFERENT ARGUMENT"); // original target is called
proxy.methodToOverride("ARGUMENT");           // proxy is called
For example code, please see the examples


