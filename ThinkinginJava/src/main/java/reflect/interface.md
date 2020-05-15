# 接口interface如何实现解耦？
“接口和内部类为我们提供了一种将接口与实现分离的更加结构化的方法。  
抽象类是普通类与节后之间的一种中庸之道”--《Java编程思想》
1. case1: Office工具包
    - 需求：工具不同的工作需要打开不同的office应用  
    - 已知
        ```java
        class Word{
            void start(){System.out.println("Word is started!");}
        }
        
        class Excel{
            void start(){System.out.println("Excel is started!");}
        }
        ```
    - 不使用interface
        - 方案1
        ```java
        class WordWork{
            void work(Word word){
                word.start();
            }
        }
        
        class ExcelWork{
            void work(Excel excel){
                excel.start();
            }
        }
        ```
        - 方案2
        ```java
        class Work{
            void work(String name){
                if(name.equals("Word")){
                    Word w = new Word();
                    w.start();
                }else if(name.equals("Excel")){
                    Excel e = new Excel();
                    e.start();
                }
            }
        }
        
        ```
        - 不使用interface的缺陷  
            若再增加一个PPT类
            - 方案1：需要另外再写一个PPTWork类，代码冗余
            - 方案2：需要修改Work类代码，Work类与Word/Excel/PPT类耦合度高。
    - 使用interface
        ```java
        interface Office{
           void start();
        }
        
        class Word implements Office{
            void start(){System.out.println("Word is started!");}
        }
        
        class Excel implements Office{
            void start(){System.out.println("Excel is started!");}
        }
        
        class Work{
            void work(Office app){
                app.start();
            }
        }
        ```
        - 运用了java的多态（后期绑定）特性。在编译时传入work(Office app)方法的对象类型并不知道，而是在运行时判别类型，调用各自的方法。
        - 减少了代码冗余，降低了耦合度，符合“开闭原则”。