package org.niceassert;

/**
 * A simple closure.
 */
public interface Action {
    Object execute(Object[] objects) throws Throwable;
}
