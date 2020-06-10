import java.util.ArrayList;

/**
 * VM args:
 * -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 *
 * -Xms20m: 堆的最小值
 * -Xmx20m: 堆的最大值
 * -XX:+HeapDumpOnOutOfMemoryError: 让虚拟机在出现OOM时dump出当前的内存堆转储快照
 */

public class OOMHeap {
    static class HeapOOM{}

    /**
     * java.lang.OutOfMemoryError: Java heap space
     * Dumping heap to java_pid16280.hprof ...
     * Heap dump file created [28246645 bytes in 0.082 secs]
     */
    public static void main(String[] args) {
        ArrayList<HeapOOM> list = new ArrayList<HeapOOM>();

        while (true){
            list.add(new HeapOOM());
        }
    }
}
