package com.dsh105.holoapi.script;

import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptLoaderTest {

    @Test
    public void testScript() throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");

        Script script = new Script("test", "return \"Success!\"") {
            @Override
            public String getSignature() {
                return "";
            }

            @Override
            public Object eval(ScriptEngine engine, Object... args) throws ScriptException {
                super.compile(engine);

                try {

                    Object result = ((Invocable) engine).invokeFunction(this.name, args);

                    return result;

                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("Failed to compile " + this.name + " into the ScriptEngine!", e);
                }
            }
        };

        System.out.println(script.eval(engine, null, null));
    }
}