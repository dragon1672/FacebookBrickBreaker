package BrickBreaker;

import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Board Colors
 */
enum Color {
    WHITES_INVALID,
    // I got these values from reading from the image,
    // made the input a set, in case in the future I find other values I want to represent each color
    RED(-696218),
    GREEN(-13260140),
    BROWN(-7569813),
    PURPLE(-5741590),
    INDIGO(-11236113),
    YELLOW(-677581);

    public final Set<Integer> RBGValues;

    Color(Integer ... RBGValues) {
        this.RBGValues = Arrays.stream(RBGValues).collect(Collectors.toSet());
    }

    private static Random rand = new Random();

    public static Color fromSymbol(char colorSymbol) {
        switch (colorSymbol) {
            case 'R': return RED;
            case 'G': return GREEN;
            case 'Y': return YELLOW;
            case 'B': return BROWN;
            case 'P': return PURPLE;
            case 'I': return INDIGO;
        }
        return WHITES_INVALID;
    }

    public static char toSymbol(Color color) {
        return color.toString().charAt(0);
    }

    public static Color random() {
        // skip WHITES_INVALID
        return values()[1+rand.nextInt(values().length-1)];
    }

    public static Color fromRGB(int rgb) {
        return Arrays.stream(values())
                .filter(color -> color.RBGValues.contains(rgb))
                .findFirst()
                .orElse(WHITES_INVALID);
    }
}
