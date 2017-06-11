package org.vnf.server;

/**
 * Created by qik on 6/4/2017.
 */
public class Captor<T> {
    private boolean captured;
    private T value;

    public Captor(){}

    public Captor(T defaultValue){
        capture(defaultValue);
    }

    public void capture(T value) {
        this.value = value;
        captured = true;
    }

    public T getValue() {
        if(!captured) {
            throw new IllegalStateException("Value not captured yet");
        }

        return value;
    }
}
