package org.niceassert;

interface InvocationCounter {
    boolean thereAreInvocationsRemaining();
    void count();
}
