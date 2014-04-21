package com.dsh105.holoapi.reflection;

import org.junit.Test;
import sun.management.Agent;

public class ClassTemplateTest {

    private FieldVisitor visitor;

    private int field1 = 5;
    private double field2 = 1.0;
    private int field3 = 6;
    private String field5 = "test";
    private Object field6 = new Object();
    private Agent doubleO7 = new Agent();

    @Test
    public void onTest() {
        this.visitor = new FieldVisitor(this);
        this.visitor = this.visitor.withType(String.class);

        for (Object accessor : this.visitor.withType(String.class).getFields()) {
            System.out.println(((FieldAccessor) accessor).getField().getName());
        }
    }
}
