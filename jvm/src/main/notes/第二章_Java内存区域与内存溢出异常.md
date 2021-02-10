# 第二章 Java内存区域与内存溢出异常

1. 运行时数据区域
    - 程序计数器（Program Counter Register）
        - 一块较小的内存空间，可看做是当前线程所执行的字节码的行号指示器
        - 为了线程切换后能恢复到正确的执行位置，每个线程的程序计数器是独立的，即“线程私有”的
    - Java虚拟机栈（Java Virtual Machine Stack）
        - JVMS描述的是Java方法执行的线程内存模型：每个方法被执行时，JVM都会同步创建一个栈帧(Stack Frame)，
        用于存储局部变量变、操作数栈、动态连接、方法出口等信息。
        - JVMS也是线程私有的，生命周期与线程相同。
        - 局部变量表
            - 存放了编译器可知的各种Java虚拟机基本数据类型、对象引用和returnAddress类型
    - 本地方法栈（Native Method Stack）
        - 类似于JVMS,JVMS是为虚拟机执行Java方法服务，NMS为虚拟机执行Native方法服务
        - HotSopt虚拟机直接将JVMS与NMS合二为一,不加区分
    - Java堆（Java Heap）
        - 是虚拟机所管理的内存中最大的一块，也是线程共享区域
        - 唯一的目的就是存放对象实例
    - 方法区（Method Area）
        - 各个线程共享的内存区域
        - 存储已被虚拟机加载的类型信息、常量、静态变量、即时编译器编译后的代码缓存等数据
        - 实现
            - Perm的废除：在jdk1.8中，Perm被替换成MetaSpace，MetaSpace存放在本地内存中。原因是永久代进场内存不够用，或者发生内存泄漏。
            - MetaSpace（元空间）：元空间的本质和永久代类似，都是对JVM规范中方法区的实现。不过元空间与永久代之间最大的区别在于：元空间并不在虚拟机中，而是使用本地内存。   
        - 运行时常量池（Runtime Constant Pool）  
![JVM运行时数据区](./images/jvm内存.png)
    
1. HotSpot虚拟机对象探秘
    - 对象的创建
        1. 当虚拟机遇到一条new指令时候，首先去检查这个指令的参数是否能在常量池中能否定位到一个类的符号引用，并且检查这个符号引用代表的类是否已被加载、解析和初始化过。如果没有，那必须先执行相应的类加载过程。
        1. 在类加载检查通过后，接下来虚拟机将为新生的对象分配内存。
            - 指针碰撞(Bump The Pointer):Java堆空间是连续的，Serial,ParNew
            - 空闲列表(Free List):Java堆空间是离散的，CMS
            - 选择哪种分配方式由Java堆是否规整决定，而Java堆是否规整又由所采用的垃圾收集器是否带有空间压缩能力决定的  
             除了如何划分可用空间外，在并发情况下划分不一定是线程安全的，
             有可能出现正在给A对象分配内存，指针还没有来得及修改，对象B又同时使用了原来的指针分配内存的情况，解决这个问题两种方案：
              - 分配内存空间的动作进行同步处理：实际上虚拟机采用CAS配上失败重试的方式保证了更新操作的原子性。
              - 内存分配的动作按照线程划分在不同的空间中进行：为每个线程在Java堆中预先分配一小块内存，称为本地线程分配缓冲（Thread Local Allocation Buffer, TLAB）。
        1. 内存分配完后，虚拟机需要将分配到的内存空间(不包括对象头)都初始化为零值。若使用了TLAB，这项工作也可以提前至TLAB分配时进行。
        1. 接下来虚拟机要对对象进行必要的设置，例如这个对象是哪个类的实例、如何才能找到类的元数据信息、对象的哈希码、对象的GC分代年龄等信息，这些信息都存放在对象的对象头中。
        1. 做完以上以后，从虚拟机视角来看，一个新的对象已经产生了，但是Java程序视角来看，执行new操作后会接着执行<init>方法，把对象按照程序员的意愿进行初始化，这样一个真正的对象就产生了。
           
           ```java
           public class JavaBase {
               static Cat cat1 = new Cat(1);
               public static void main(String[] args) { }
           }
           
           class Cat extends Animal{
               Fish fish1 = new Fish(1);
               public Cat(int i){
                   System.out.println("cat"+i);
               }
               static {
                   System.out.println("static block1");
               }
               {
                   System.out.println("non-static block3");
               }
               static {
                   System.out.println("static block2");
               }
               static Fish fish2 = new Fish(2);
			}
           
           class Fish{
               public Fish(int i){
                   System.out.println("fish"+i);
               }
           }
           
           class Animal{
               public Animal(){
                   System.out.println("this is an animal!");
               }
               static Fish fish1 = new Fish(3);
           }
            /**
             *fish3                父类<clinit>
             *static block1        子类<clinit>
             *static block2        子类<clinit>
             *fish2                子类<clinit>
             *this is an animal!   父类<init>
             *fish1                子类<init>
             *non-static block3    子类<init>
             *cat1                 子类<init>
             */
           ```
    - 对象的内存布局
        1. 对象头(Header)
            - Mark Word，对象自身的运行时数据，如HashCode,GC分代年龄，锁状态标志等
            - 类型指针，即对象指向它的类型元数据的指针，JVM通过这个指针来确定该对象是哪个类的实例
        1. 实例数据(Instance Data)
            - 对象真正存储的有效信息，即代码中定义的各种类型的字段内容
            - 无论自己定义还是父类继承的都必须记录下来
        1. 对齐填充(Padding)
            - 并非必然存在，起到占位符的作用
    - 对象的访问定位
      
    1. 句柄访问：reference(存储句柄地址)->对象句柄->对象实例数据等信息，解耦对象与reference
    2. **直接指针访问**：reference（对象地址）->对象实例数据等信息，速度更快，HotSpot主要采用这种方法
    
1. 实战：OutOfMemoryError异常
    - Java堆溢出
        - 大量对象导致堆内存溢出
            1. -Xms20m，设置最小堆内存20mb
            1. -Xmx20m，设置最大堆内存20mb
            1. -Xms参数与-Xmx相等时可避免堆自动扩展
        - [内存溢出与内存泄漏](https://www.cnblogs.com/rgever/p/8899758.html)
    - 虚拟机栈和本地方法栈溢出
        - HotSpot虚拟机不支持栈的动态扩展
        - 线程请求栈的深度大于虚拟机允许的最大深度，抛出StackOverFlowError
            1. -Xss参数减小栈内存容量
            1. 定义大量本地变量，增大栈帧中本地变量表的长度
        - 若虚拟机栈内存允许动态拓展，但是扩展栈无法申请到足够内存时，抛出OutOfMemoryError
            1. 创建大量线程，每个线程都要分配独立的虚拟机栈和本地方法栈，导致内存OOM而不是SOF
    - 方法区和运行时常量池溢出
        - JDK6及之前用永久代实现方法区，JDK7逐步“去永久代”，JDK8用元空间实现方法区
        - 创建大量的类填充方法区，导致溢出
          