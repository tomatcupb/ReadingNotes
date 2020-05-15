# 为什么用反射？
"**运行时**类型信息使得你可以在程序运行时发现和使用类型信息。  
它使你从只能在**编译期**执行**面向类型**的操作的禁锢中解脱了出来，并且可以使用某些非常强大的程序。"--《Java编程思想》

一般使用关键字new创建对象是在已知了类型时进行的操作，即在编译期已经知道了创建的对象类型，即所说的“编译期执行面向类型的操作”。  
而反射使得程序可以在运行时动态加载类型，即编译期不确定创建对象的类型（Class.forName()生成的结果在编译时是不可知的，只有在运行的时候才能加载这个类）。  
这是比interface更高层次的抽象和面向对象。以java.lang.reflect类库中的Field, Method, Constructor类为例，抽取所有类的共性，以类为对象。
这么做的最终目的和成果是进一步降低程序耦合。

1. 什么是反射  
    JAVA反射机制是在**运行状态**中，对于任意一个类，都能够知道这个类的所有属性和方法；
    对于任意一个对象，都能够调用它的任意方法和属性；这种**动态**获取信息以及动态调用对象方法的功能称为java语言的反射机制。

1. 更高程度的面向对象（以类为对象），降低耦合  
    需求：是比较两个User对象是否相等,返回不相等的属性列表。
    ```java
    class User{
        String name;
        int age;
        boolean gender;
    }
    ```
    不使用反射去做的代码如下：
    ```
    List<String> diff(User u1, User u2, List<String> list){
        if(u1.getName().equals(u2.getName())){
            list.add("name");
        }
        if(u1.getAge()!=u2.getAge()){
            list.add("age");
        }
        if(u1.getGender()!=u2.getGender()){
            list.add("gender");
        }
        return list;
    }
    ```
    以上的写法主要有两个缺陷：
        1. 如果User类的属性很多，diff()会写的很长很麻烦。且当User类增加属性时，diff()代码需要修改。
        2. 另一个很重要的问题就是，这个diff方法只能服务于User类。其他类的对象想要比较的话得重新根据自己类属性去写diff()。
    
    而使用反射，不需直到具体的属性信息，很好的解决了上边的问题。    
    ```
    List<String> diff(Object o1, Object o2, List<String> list){
            Class claszz1 = o1.getClass();
            Class claszz2 = o2.getClass();
            if(claszz1==claszz2){
                Field[] fields = claszz1.getDeclaredFields();
                for(Field f: fields){
                    f.setAccessible(true);
                    try {
                        Object value1 = f.get(o1);
                        Object value2 = f.get(o2);
                        if(!value1.equals(value2)){
                            list.add(f.getName());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return list;
        }
    ```

1. 动态加载  
    1. Case1: Office工具包  
    已知目前存在Office.java和Word.java。但是执行Office的main()会因为没有Excel类报错。
    但是这个Excel类我们一定会用到吗？如果这个Excel类需要很久才能写出来，在此我们也不能使用其他功能吗？
    后面如果一个类出问题了，这个系统是不是就瘫痪了？
    ```java
    class Office{
        public static void main(String[] args){
            if(args[0].equals("Word")){
                Word w = new Word();
                w.start();
            }
            if(args[0].equals("Excel")){
                Excel e = new Excel();
                e.start();
            }
        }
    }
    
    
    class Word{
        public void start(){
            System.out.println("Word Start");
        }
    }
    ```

    利用反射改写
    ```java
    interface OfficeAble{
        public void start();
    }
    
    class Word implements OfficeAble{
        public void start(){
            System.out.println("Word Start");
        }
    }
    
    class Office{
        public static void main(String[] args){
            try{
                for(String name: args){
                    Class c = Class.forName(name);
                    OfficeAble w = (OfficeAble)c.newInstance();
                    w.start();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    ```
    这样改造之后，我后面要添加一个Excel类，乃至添加其他的类，只需要实现OfficeAble接口就可以了，不需要改动Office这个类和其他的功能类，扩展性很强，这就是动态加载的优势。

    1. Case2: 数据库连接的动态加载  
        当我们的程序在运行时，需要动态的加载一些类这些类可能之前用不到所以不用加载到jvm，而是在运行时根据需要才加载，这样的好处对于服务器来说不言而喻。
        不用反射:
        假设有个生产环境，数据库连接是用的mysql,所以代码应该是这样
        ```
        MysqlConnection conn =new  MysqlConnection()  
        ```
        然而，某一天突然要改成oracle。所以，这时要做的是改成
        ```
        conn =new  OracleConnection() 
        ```
        或者新建一个配置文件，里面填mysql或oracle，然后代码中取得配置文件的字符串，
        if是 mysql就 conn =new  MysqlConnection() 是oracle就  conn =new  OracleConnection.
        然后,最重要的是，重新把java代码用javac编译一遍，再把编译后的class文件把程序启动。
        
        用反射：
        最开始，如果我们就考虑到会有时切换数据库，我们写成配置文件，然后用
        Class.forName(str)什么的来new 数据库驱动，更改数据库时就仅需简单的更改配置文件了，这样就不需要重新编译代码了。



- 参考
    - Java编程思想
    - [知乎：如何理解反射？](https://www.zhihu.com/question/24304289)
    - [Java动态加载类](https://blog.csdn.net/zai_xia/article/details/80026325)