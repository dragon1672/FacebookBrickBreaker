package utils;

/**
 * String Utilities
 */
public class MyStringUtils {
    /**
     * Rotates a string 90 degreese counter clockwise
     * @param src string to rotate
     * @return rotated string
     */
    public static String rotateCounterClockwise(String src) {
        /// Don't judge me. This didn't end up having a huge performance cost
        return rotateClockwise(rotateClockwise(rotateClockwise(src)));
    }

    /**
     * Rotates a string 90 degrees clockwise
     * @param src string to rotate
     * @return rotated string
     */
    public static String rotateClockwise(String src) {
        StringBuilder sb = new StringBuilder();
        String[] rows = src.split("\n");
        int height = 0;
        boolean hasWritten;
        do {
            hasWritten = false;
            StringBuilder line = new StringBuilder(rows.length);
            for (String row : rows) {
                if (height < row.length()) {
                    line.append(row.charAt(height));
                    hasWritten = true;
                } else {
                    line.append(' ');
                }
            }
            if(hasWritten) {
                sb.append(line.reverse());
                sb.append('\n');
            }
            height++;
        } while(hasWritten);
        return sb.toString();
    }
}
