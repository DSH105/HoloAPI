package com.dsh105.holoapi.util;

import com.dsh105.holoapi.reflection.MethodAccessor;

public class MapperClassFinder {

    protected ClassLoader classLoader;
    protected Object remapper;
    protected MethodAccessor<String> map;

    public MapperClassFinder() {
        this(MapperClassFinder.class.getClassLoader());
    }

    public MapperClassFinder(ClassLoader classLoader) {
        this.classLoader = classLoader;
        initialize();
    }

    protected void initialize() {
       /** if(HoloAPICore.getCommonServer().getServerBrand() != ServerBrand.MCPC_PLUS)
            throw new UnsupportedOperationException("The current brand doesn't support Remmapers!");

        this.remapper = ClassTemplate.create(this.classLoader.getClass()).getField("remapper");

        if(this.remapper == null)
            throw new IllegalStateException("Remapper is NULL!");

        Class<?> remapperClass = this.remapper.getClass();

        this.map = ClassTemplate.create(remapperClass).getMethod("map", String.class);   */
    }

    public String getRemappedName(String className) {
        return map.invoke(remapper, className.replace('.', '/')).replace('/', '.');
    }
}
