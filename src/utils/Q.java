package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Optimized Que list
 * I might have misread the source code for Stack, but it looked like calling "pop" had an array copy operation
 * I could probably find a proper collection but I'm lazy and this worked
 */
public class Q<T> {
    private List<T> backbone = new ArrayList<T>();
    private int length = 0;

    public void add(T toAdd) {
        if (length >= backbone.size()) {
            backbone.add(toAdd);
        } else {
            backbone.set(length, toAdd);
        }
        length++;
    }

    public int size() {
        return length;
    }

    public T pop() {
        return backbone.get(--length);
    }
}
