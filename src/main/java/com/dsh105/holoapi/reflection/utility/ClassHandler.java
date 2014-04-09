package com.dsh105.holoapi.reflection.utility;

public abstract class ClassHandler {

    protected ClassLoader classLoader;

    public ClassHandler() {
        this(ClassHandler.class.getClassLoader());
    }

    public ClassHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public static ClassHandler fromClassLoader() {
        return fromClassLoader(ClassHandler.class.getClassLoader());
    }

    public static ClassHandler fromClassLoader(ClassLoader classLoader) {
        return new ClassHandler() {
            @Override
            public Class<?> loadClass(String className) throws ClassNotFoundException {
                return classLoader.loadClass(className);
            }
        };
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return this.classLoader.loadClass(className);
    }

    public static ClassHandler fromPackage(final String packageName) {
        return new ClassHandler() {
            @Override
            public Class<?> loadClass(String className) throws ClassNotFoundException {
                return this.classLoader.loadClass(packageName + "." + className);
            }
        };
    }
}
