package GCDemo;

public class AgeTest {
    /**
     * -Xmx20m
     * -Xms20m
     * -Xmn10m // 新生代内存10Mb
     * -XX:SurvivorRatio=8 Eden和一个Survivor分配的空间8:1
     * -verbose:gc //在控制台输出GC情况
     * -XX:+PrintGCDetails  //在控制台输出详细的GC情况
     * -XX:MaxTenuringThreshold=1 //移动到老年代的年龄限制
     */
    public static final int _1MB = 1024*1024;
    public static void main(String[] args) {
        byte[] al1, al2, al3;
        al1 = new byte[1*_1MB/4];
        al2 = new byte[4*_1MB];
        al3 = new byte[4*_1MB]; // 第一次GC
        al3 = null;
        al3 = new byte[4*_1MB]; // 第二次GC
    }

    //    -Xmx20m -Xms20m -Xmn10m -XX:SurvivorRatio=8 -verbose:gc
//    -XX:+PrintGCDetails -XX:MaxTenuringThreshold=1
//    -XX:+UseSerialGC  // 指定垃圾收集器Serial+Serial Old。
}
