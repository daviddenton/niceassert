package org.niceassert;

class InfiniteInvocationCounter implements InvocationCounter {
    public boolean thereAreInvocationsRemaining() {
        return true;
    }

    public void count() {
    }
}
