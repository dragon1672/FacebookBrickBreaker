package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Immutable 2d Int Vector
 */
@SuppressWarnings("WeakerAccess")
public class IntVector2D {
    private static final Map<Integer,Map<Integer, IntVector2D>> cache = new HashMap<>();
    private final int x;
    private final int y;

    private IntVector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int X() {return x;}
    public int Y() {return y;}
    public int getX() {return x;}
    public int getY() {return y;}

    public static IntVector2D create(int x, int y) {
        return cache.computeIfAbsent(x,integer -> new HashMap<>()).computeIfAbsent(y,integer -> new IntVector2D(x,y));
    }

    static IntVector2D add(IntVector2D left, IntVector2D right){
        return create(left.x + right.x,left.y + right.y);
    }
    public IntVector2D add(IntVector2D that){
        return add(this,that);
    }

    static IntVector2D sub(IntVector2D left, IntVector2D right){
        return create(left.x - right.x,left.y - right.y);
    }
    IntVector2D sub(IntVector2D that){
        return sub(this,that);
    }

    static IntVector2D mul(IntVector2D intVector2D, float value){
        return create((int)(intVector2D.x * value),(int)(intVector2D.y * value));
    }
    IntVector2D mul(float value){
        return mul(this,value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntVector2D that = (IntVector2D) o;

        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return String.format("{%d,%d}",x,y);
    }
}
