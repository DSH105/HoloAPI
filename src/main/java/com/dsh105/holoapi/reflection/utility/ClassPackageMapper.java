package com.dsh105.holoapi.reflection.utility;

import com.google.common.base.Preconditions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassPackageMapper {

    protected String packageName;
    protected ClassHandler classHandler;
    protected Map<String, Class<?>> classes = new ConcurrentHashMap<String, Class<?>>();

    public ClassPackageMapper(String packageName, ClassHandler classHandler) {
        this.packageName = packageName;
        this.classHandler = classHandler;
    }

    public Class<?> getClass(String className) {
        try {
            Class<?> clazz = this.classes.get(Preconditions.checkNotNull(className, "ClassName can't be NULL!"));

            if(clazz == null) {


                clazz = this.classHandler.loadClass(this.packageName + "." + className);

                if(clazz == null)
                    throw new ClassNotFoundException("Failed to find class: " +this.packageName + "." + className);

                this.classes.put(className, clazz);
            }

            return clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to find class: " + this.packageName + "." + className);
        }
    }
}
