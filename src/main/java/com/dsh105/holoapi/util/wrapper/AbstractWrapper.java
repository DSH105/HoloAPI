package com.dsh105.holoapi.util.wrapper;

public class AbstractWrapper {

    private Object handle;

    public AbstractWrapper() {}

    protected void setHandle(Object handle) {
        if(this.handle == null) {
            this.handle = handle;
            return;
        }
        throw new RuntimeException("Handle already set!");
    }

    public Object getHandle() {
        return this.handle;
    }
}
