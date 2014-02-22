package com.dsh105.holoapi.api.stored;

public class TextData implements DataStorage {

    private String[] text;

    public TextData(String... text) {
        this.text = text;
    }

    public String[] getText() {
        return text;
    }

    public void setText(String... text) {
        this.text = text;
    }
}