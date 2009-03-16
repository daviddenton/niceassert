package org.niceassert;

class SimpleInvocationCounter implements InvocationCounter {
    private int count;

    SimpleInvocationCounter(int count) {
        this.count = count;
    }

    public boolean thereAreInvocationsRemaining() {
        return count > 0;
    }

    public void count() {
        count--;
    }
}
