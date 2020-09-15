package ru.javawebinar.topjava.util;

public class BooleanContainer {
    private Boolean value;

    public BooleanContainer() {}
    public BooleanContainer(Boolean initial) { value = initial; }
    public Boolean get() { return value; }
    public void set(Boolean value) { this.value = value; }

    @Override
    public String toString() {
        return  value +"";
    }
}
