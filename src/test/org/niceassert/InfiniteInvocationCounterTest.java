package org.niceassert;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

class InfiniteInvocationCounterTest {
    private final InfiniteInvocationCounter invocationCounter = new InfiniteInvocationCounter();

    @Test
    public void lotsOfInvocationsNotCounted() {
        for (int i=0; i<100; i++) {
            invocationCounter.count();
            assertThat(invocationCounter.thereAreInvocationsRemaining(), is(true));
        }
    }
}