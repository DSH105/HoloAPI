package com.dsh105.holoapi.script;

import com.dsh105.holoapi.api.Hologram;
import org.bukkit.entity.Player;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Script<T> {

    protected String name;
    protected String code;

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

    public T eval(ScriptEngine engine, Hologram hologram, Player player) throws ScriptException {

        this.compile(engine);

        try {

            Object result = ((Invocable) engine).invokeFunction(this.name, hologram, player);

            return (T) result;

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Failed to compile " + this.name + " into the ScriptEngine!", e);
        }
    }

    protected void compile(ScriptEngine engine) throws ScriptException {
        if (engine.get(this.name) == null) {
            engine.eval("var " + this.name + " = function(hologram, player) {\n" + this.code + "\n}");
        }
    }

    public void cleanup(ScriptEngine engine) {
        engine.put(this.name, null);
    }

    public void saveToFile(File file) throws FileNotFoundException {
        if (file.isDirectory() || !file.exists())
            throw new IllegalArgumentException("File is a directory or doesn't exist!");

        if (file.getName().endsWith(ScriptLoader.SCRIPT_EXTENSION))
            throw new IllegalArgumentException("File doesn't have the " + ScriptLoader.SCRIPT_EXTENSION + " extension!");

        PrintWriter writer = new PrintWriter(file);
        writer.print(this.code);
        writer.flush();
        writer.close();
    }

    public static Script readFromFile(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        String code = new String(encoded, StandardCharsets.UTF_8);
        return new Script(file.getName(), code);
    }
}