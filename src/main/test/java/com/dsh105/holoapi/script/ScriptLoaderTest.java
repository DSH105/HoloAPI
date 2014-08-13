package com.dsh105.holoapi.script;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptLoaderTest {

    @Test
    public void testScript() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");

        Script script = new Script("test", "return \"Success!\"");
        System.out.println(script.eval(engine, null, null));
    }
}