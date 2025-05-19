package cn.mycommons.processkit;

import org.testng.annotations.Test;

public class JavaTest {

    @Test
    public void testRun() {
        ProcessKit.run("ls -alh", null, 0);
    }

    @Test
    public void testCall() {
        var ret = ProcessKit.call("ls -alh", null, 10, false);
        System.out.println("ret = " + ret);
    }
}
