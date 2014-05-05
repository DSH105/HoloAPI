package com.dsh105.holoapi.reflection.fuzzy.accessor;

import java.lang.reflect.Method;

public abstract class MethodAccessor<TType> {

    public abstract Method getMethod();

    public abstract TType invoke(Object instance, Object... params);

    public abstract boolean isConstructor();

    public abstract Class<?> getReturnType();

    public abstract Class[] getParameters();
}
