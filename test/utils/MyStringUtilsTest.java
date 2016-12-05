package utils;

import org.junit.Assert;
import org.junit.Test;

public class MyStringUtilsTest {
    @Test
    public void rotateCounterClockwise() throws Exception {
        String input = "" +
                "1\n" +
                "22\n" +
                "3";
        String expected = "" +
                " 2 \n" +
                "123\n";
        String output = MyStringUtils.rotateCounterClockwise(input);
        Assert.assertEquals(expected, output);
    }

    @Test
    public void rotateClockwise() throws Exception {
        String input = "" +
                "1\n" +
                "22\n" +
                "3";
        String expected = "" +
                "321\n" +
                " 2 \n";
        String output = MyStringUtils.rotateClockwise(input);
        Assert.assertEquals(expected, output);
    }

    @Test
    public void rotateClockwise_2() throws Exception {
        String input = "" +
                "123\n" +
                " 2 \n";
        String expected = "" +
                " 1\n" +
                "22\n" +
                " 3\n";
        String output = MyStringUtils.rotateClockwise(input);
        Assert.assertEquals(expected, output);
    }

}