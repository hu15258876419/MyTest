package containers;

import java.util.List;

/**
 * @version 1.0
 * @Description: 代码无法理解
 * @author: hxw
 * @date: 2019/2/8 3:25
 */
public class Tester<C> {
    public static int fieldWidth = 8;
    public static TestParam[] defaultParams= TestParam.array(10, 5000, 100, 5000, 1000, 5000, 10000, 500);
    // Override this to modify pre-test initialization:
    protected C initialize(int size) {
        return container;
    }

    protected C container; //待测试的容器
    private String headline = "";
    private List<Test<C>> tests; //测试容器列表

    private static String stringField() {
        return "%" + fieldWidth + "s";
    }
    private static String numberField() {
        return "%" + fieldWidth + "d";
    }
    private static int sizeWidth = 5;
    private static String sizeField = "%" + sizeWidth + "s";
    private TestParam[] paramList = defaultParams;

    public Tester(C container, List<Test<C>> tests) {
        this.container = container;
        this.tests = tests;
        if(container != null)
            headline = container.getClass().getSimpleName();
    }

    public Tester(C container, List<Test<C>> tests, TestParam[] paramList) {
        this(container, tests);
        this.paramList = paramList;
    }

    public void setHeadline(String newHeadline) {
        headline = newHeadline;
    }

    // Generic methods for convenience :
    public static <C> void run(C cntnr, List<Test<C>> tests){
        new Tester<C>(cntnr, tests).timedTest();
    }

    public static <C> void run(C cntnr, List<Test<C>> tests, TestParam[] paramList) {
        new Tester<C>(cntnr, tests, paramList).timedTest();
    }

    //这个方法是用来打印标题
    private void displayHeader() {
        // Calculate width and pad with ‘-’: 用“-”计算宽度和衬垫：
        int width = fieldWidth * tests.size() + sizeWidth;
        int dashLength = width - headline.length() - 1;
        StringBuilder head = new StringBuilder(width);
        for(int i = 0; i < dashLength/2; i++){
            head.append('-');
        }
        head.append(' ');
        head.append(headline);
        head.append(' ');
        for(int i = 0; i < dashLength/2; i++){
            head.append('-');
        }
        System.out.println(head);
        // Print column headers:
        System.out.format(sizeField, "size");
        for(Test test : tests)
            System.out.format(stringField(), test.name);
        System.out.println();
    }

    // Run the tests for this container:运行此容器的测试
    public void timedTest() {
        displayHeader();
        for(TestParam param : paramList) { //遍历循环分别是size=10,100,1000,10000的执行情况
            System.out.format(sizeField, param.size);
            for(Test<C> test : tests) { //这里的tests是ListPerformance中的tests通过Tester的构造器传递过来的，代表一个待测试方法的列表
                C kontainer = initialize(param.size); //这里在ListPerformance 的Array as List测试中使用匿名内部类重写了initialize方法，这里param.size是动态变化的10,100,1000,10000
                long start = System.nanoTime();
                // Call the overriden method: 调用重写的test方法
                int reps = test.test(kontainer, param);
                long duration = System.nanoTime() - start;
                long timePerRep = duration / reps; // Nanoseconds 纳秒
                System.out.format(numberField(), timePerRep); //花费时长
            }
            System.out.println();
        }
    }

}
