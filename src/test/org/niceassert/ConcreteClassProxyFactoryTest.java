package org.niceassert;

import net.sf.cglib.proxy.InvocationHandler;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;


public class ConcreteClassProxyFactoryTest {

    private final ConcreteClassProxyFactory concreteClassProxyFactory = ConcreteClassProxyFactory.INSTANCE;

    @Test(expected = IllegalArgumentException.class)
    public void failsToImposterizeFinalClass() {
        assertFailsToImposterize(FinalClass.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsToImposterizeClassWithFinalStringMethod() {
        assertFailsToImposterize(ClassWithFinalStringMethod.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsToImposterizePrimitiveClass() {
        assertFailsToImposterize(Integer.class);
    }

    @Test
    public void proxyExtendsTargetObject() {
        assertThat(ExtendableClass.class.isAssignableFrom(concreteClassProxyFactory.proxyFor(ExtendableClass.class, null).getClass()), is(true));
    }
    
    @Test
    public void proxyInvokesPassedHandler() {
        final AtomicBoolean called = new AtomicBoolean(false);
        concreteClassProxyFactory.proxyFor(ExtendableClass.class, new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                called.set(true);
                return null;
            }
        }).toString();
        assertThat(called.get(), is(true));

        assertThat(ExtendableClass.class.isAssignableFrom(concreteClassProxyFactory.proxyFor(ExtendableClass.class, null).getClass()), is(true));
    }

    private void assertFailsToImposterize(Class targetClass) {
        concreteClassProxyFactory.proxyFor(targetClass, new FailingInvocationHandler());
    }

    private static class FailingInvocationHandler implements InvocationHandler {
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            throw new AssertionError();
        }
    }

    private class ClassWithFinalStringMethod {
        public final String toString() {
            return null;
        }
    }

    private final class FinalClass {
    }

    public class ExtendableClass {
    }

}