package org.niceassert;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class SimpleInvocationCounterTest {
    private final SimpleInvocationCounter invocationCounter = new SimpleInvocationCounter(1);

    @Test
    public void invocationsCounted() {
        assertThat(invocationCounter.thereAreInvocationsRemaining(), is(true));
        invocationCounter.count();
        assertThat(invocationCounter.thereAreInvocationsRemaining(), is(false));
    }
}