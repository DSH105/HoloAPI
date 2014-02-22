package com.dsh105.holoapi.util.wrapper;

import com.dsh105.holoapi.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;

public class DataWatcher extends AbstractWrapper {

    public DataWatcher() {
        try {
            super.setHandle(ReflectionUtil.getNMSClass("DataWatcher").getConstructor(ReflectionUtil.getNMSClass("Entity")).newInstance(null));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void watch(int index, Object value) {
        ReflectionUtil.invokeMethod(ReflectionUtil.getMethod(getHandle().getClass(), "a", int.class, Object.class), getHandle(), index, value);
    }
}
