package com.dsh105.holoapi.command.sub.script;

public class Script {

    private final String name;
    private final String code;

    public Script(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }
}
