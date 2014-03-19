package com.dsh105.holoapi.util.wrapper;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;

public class WrappedDataWatcher extends AbstractWrapper {

    public WrappedDataWatcher() {
        try {
            if(HoloAPI.isUsingNetty) {
                super.setHandle(ReflectionUtil.getNMSClass("DataWatcher").getConstructor(ReflectionUtil.getNMSClass("Entity")).newInstance(new Object[]{null}));
            } else {
                super.setHandle(ReflectionUtil.getNMSClass("DataWatcher").newInstance());
            }
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
