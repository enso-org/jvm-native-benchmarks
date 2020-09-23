package test;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

class App {
    public Object fun, arg;

    public App(Object fun, Object arg) {
        this.fun = fun;
        this.arg = arg;
    }

    public static Object tree(long typ, long depth) {
        if (depth == 0) {
            if (typ == 0) return new Name("");
            if (typ == 1) return new Offset(0);
            if (typ == 2) return new Position(0, 0);
        }
        return new App(App.tree(typ, depth-1), App.tree(typ, depth-1));
    }

    public static Object copy(Object tree) {
        if (tree instanceof App)
            return new App(App.copy(((App) tree).fun), App.copy(((App) tree).arg));
        if (tree instanceof Name)
            return new Name(((Name) tree).str);
        if (tree instanceof Offset)
            return new Offset(((Offset) tree).value);
        return new Position(((Position) tree).at, ((Position) tree).to);
    }
}

class Name {
    public String str;

    public Name(String str) {
        this.str = str;
    }

    public Name copy() {
        return new Name(str);
    }
}


class Offset {
    public long value;

    public Offset(long value) {
        this.value = value;
    }
}

class Position {
    public long at, to;

    public Position(long at, long to) {
        this.at = at;
        this.to = to;
    }
}



public class Bench {
    static {  System.load("D:/enso/jvm-native-benchmarks/cpp/main.dll"); }

    private static native App tree(long typ, long depth);
    private static native long rustTree(long typ, long depth);
    private static native App rustJni(long ast);
    private static native App rustRaw(long ast);

    @State(Scope.Thread)
    public static class MyState {
//        public Object[] javaAst = new Object[]{App.tree(0,20),App.tree(1,20),App.tree(2,20)};
//        public long[] rustAst = new long[]{rustTree(0,20),rustTree(1,20),rustTree(2,20)};
    }


//    @Benchmark
//    public void test_java_copy_name() {
//        App.copy(App.tree(0, 20));
//    }
//    @Benchmark
//    public void test_java_copy_offset() {
//        App.copy(App.tree(1, 20));
//    }
//    @Benchmark
//    public void test_java_copy_position() {
//        App.copy(App.tree(2, 20));
//    }

//    @Benchmark
//    public void test_rust_name() {
//        tree(0,20);
//    }
//    @Benchmark
//    public void test_rust_offset() {
//        tree(1,20);
//    }
    @Benchmark
    public void test_rust_position(MyState state) {
       App a = tree(1, 20);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Bench.class.getSimpleName())
                .verbosity(VerboseMode.EXTRA)
                .forks(1)
                .build();

        new Runner(opt).run();

    }
}
