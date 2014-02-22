package com.dsh105.holoapi.reflection;

public interface MethodAccessor<T> {

    T invoke(Object instance, Object... args);

}
