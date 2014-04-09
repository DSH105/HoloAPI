package com.dsh105.holoapi.reflection.utility;

import com.captainbern.yggdrasil.core.Yggdrasil;
import com.captainbern.yggdrasil.reflection.ClassTemplate;
import com.captainbern.yggdrasil.reflection.MethodAccessor;
import com.captainbern.yggdrasil.server.ServerBrand;

public class RemappedClassHandler extends ClassHandler {

    protected ClassLoader classLoader;
    protected Object remapper;
    protected MethodAccessor<String> map;

    public RemappedClassHandler() {
        this(RemappedClassHandler.class.getClassLoader());
    }

    public RemappedClassHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    protected RemappedClassHandler initialize() throws UnsupportedOperationException, IllegalStateException {
        if(Yggdrasil.getCommonServer().getServerBrand() != ServerBrand.MCPC_PLUS)
            throw new UnsupportedOperationException("The current brand doesn't support Remmapers!");

        this.remapper = ClassTemplate.create(this.classLoader.getClass()).getField("remapper");

        if(this.remapper == null)
            throw new IllegalStateException("Remapper is NULL!");

        Class<?> remapperClass = this.remapper.getClass();

        this.map = ClassTemplate.create(remapperClass).getMethod("map", String.class);
        return this;
    }

    public String getRemappedName(String className) {
        return map.invoke(remapper, className.replace('.', '/')).replace('/', '.');
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        try {
            return this.classLoader.loadClass(getRemappedName(className));
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Failed to find class: " + className + " (Remapped name: " + getRemappedName(className) + ")");
        }
    }
}
