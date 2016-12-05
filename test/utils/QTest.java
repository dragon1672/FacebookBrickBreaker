package utils;

import org.junit.Assert;
import org.junit.Test;

public class QTest {
    @Test
    public void test() throws Exception {
        Q<Integer> q = new Q<>();
        q.add(1);
        q.add(2);
        q.add(3);
        Assert.assertEquals(3,q.size());
        Assert.assertEquals(new Integer(3),q.pop());
        Assert.assertEquals(2,q.size());
        q.add(4);
        Assert.assertEquals(3,q.size());
        Assert.assertEquals(new Integer(4),q.pop());
        Assert.assertEquals(new Integer(2),q.pop());
        Assert.assertEquals(new Integer(1),q.pop());
    }

}