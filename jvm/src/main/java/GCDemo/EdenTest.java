package GCDemo;

public class EdenTest {
    /**
     * -Xmx20m
     * -Xms20m
     * -Xmn10m // 新生代内存10Mb
     * -XX:SurvivorRatio=8 Eden和一个Survivor分配的空间8:1
     * -verbose:gc //在控制台输出GC情况
     * -XX:+PrintGCDetails  //在控制台输出详细的GC情况
     */
    public static final int _1MB = 1024*1024;
    public static void main(String[] args) {
        byte[] al1, al2, al3, al4;
        al1 = new byte[2*_1MB];
        al2 = new byte[2*_1MB];
        al3 = new byte[2*_1MB];
        al4 = new byte[4*_1MB]; // 出现一次minor GC
    }

}
