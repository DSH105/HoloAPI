package com.dsh105.holoapi.reflection.fuzzy;

public abstract class AbstractMatcher<TType> implements Comparable<AbstractMatcher<TType>> {

    public abstract boolean matches(TType value, Object parent);


}
