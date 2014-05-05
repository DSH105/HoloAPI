package com.dsh105.holoapi.reflection.fuzzy.accessor;

import java.lang.reflect.Field;

public abstract class FieldAccessor<TType> {

    public abstract Field getField();

    public abstract TType get(Object instance);

    public abstract void set(Object instance, TType value);

    public abstract FieldAccessor<TType> transfer(Object from, Object to);

}
