package GCDemo;

public class OldTest {
    /**
     * -Xmx20m
     * -Xms20m
     * -Xmn10m // 新生代内存10Mb
     * -XX:SurvivorRatio=8 Eden和一个Survivor分配的空间8:1
     * -verbose:gc //在控制台输出GC情况
     * -XX:+PrintGCDetails  //在控制台输出详细的GC情况
     * -XX:PretenureSizeThreshold=3145728 (3Mb，大于该设置值的对象直接在老年代分配空间)
     */
    public static final int _1MB = 1024*1024;
    public static void main(String[] args) {
        byte[] al1;
        al1 = new byte[10*_1MB]; // 直接分配在老年代
    }
//    -Xmx20m -Xms20m -Xmn10m -XX:SurvivorRatio=8 -verbose:gc
//    -XX:+PrintGCDetails -XX:PretenureSizeThreshold=3145728
//    -XX:+UseConcMarkSweepGC  // 指定垃圾收集器par new + CMS + serial old
}
