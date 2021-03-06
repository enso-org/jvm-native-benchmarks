package test;

import com.google.flatbuffers.Table;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import test.flat.Ast;

class App {
    public Object fun, arg;

    public App(Object fun, Object arg) {
        this.fun = fun;
        this.arg = arg;
    }

    public static Object ast(long typ, long depth) {
        if (depth == 0) {
            if (typ == 0) return new Name("");
            if (typ == 1) return new Offset(0);
            if (typ == 2) return new Position(0, 0);
        }
        return new App(App.ast(typ, depth - 1), App.ast(typ, depth - 1));
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

    public static Object deserialize(ByteBuffer bytes) {
        switch (bytes.get()) {
            case 0  : int len = bytes.getInt();
                      Name name = new Name(new String(bytes.array(), bytes.position(), len, StandardCharsets.UTF_8));
                      bytes.position(bytes.position() + len);
                      return name;
            case 1  : return new Offset(bytes.getLong());
            case 2  : return new Position(bytes.getLong(), bytes.getLong());
            default : return new App(deserialize(bytes), deserialize(bytes));
        }
    }

    public static Object flatDeserialize(Object ast, byte typ) {
        switch (typ) {
            case Ast.Name     : return new Name(((test.flat.Name)ast).str());
            case Ast.Offset   : return new Offset(((test.flat.Offset)ast).value());
            case Ast.Position : test.flat.Position p = (test.flat.Position)ast;
                                return new Position(p.at(), p.to());
            default           :
                test.flat.App a = (test.flat.App)ast;
                byte t = a.funType();
                Table o1,o2;
                switch (t) {
                    case Ast.Name     : o1 = new test.flat.Name(); o2=new test.flat.Name(); break;
                    case Ast.Offset   : o1 = new test.flat.Offset(); o2 = new test.flat.Offset(); break;
                    case Ast.Position : o1 = new test.flat.Position(); o2 = new test.flat.Position(); break;
                    default           : o1 = new test.flat.App(); o2 = new test.flat.App(); break;
                }
                return new App(flatDeserialize(a.fun(o1),t), flatDeserialize(a.arg(o2),t));
        }
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
    static {
        System.loadLibrary("rust");
        System.loadLibrary("cpp");
    }

    private static native long cAst(long typ, long depth);

    private static native long rAst(long typ, long depth);

    private static native Object cBench(long astPtr);

    private static native Object rBench(long astPtr);

    private static native Object rBenchUnsafe(long astPtr);

    private static native byte[] serialize(long astPtr);

    private static native byte[] flatSerialize(long astPtr);

    @State(Scope.Thread)
    public static class MyState {
        public Object[] jAst = new Object[]{App.ast(0, 20), App.ast(1, 20), App.ast(2, 20)};
        public long  [] rAst = new long[]{rAst(0, 20), rAst(1, 20), rAst(2, 20)};
        public long  [] cAst = new long[]{cAst(0, 20), cAst(1, 20), cAst(2, 20)};
    }


    // JAVA

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testJavaName(MyState state) {
        App.copy(state.jAst[0]);
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testJavaOffset(MyState state) {
        App.copy(state.jAst[1]);
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testJavaPosition(MyState state) {
        App.copy(state.jAst[2]);
    }


    // C++

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testCName(MyState state) {
        cBench(state.cAst[0]);
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testCOffset(MyState state) {
        cBench(state.cAst[1]);
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testCPosition(MyState state) {
        cBench(state.cAst[2]);
    }

    // RUST

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustName(MyState state) {
        rBench(state.rAst[0]);
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustOffset(MyState state) {
        rBench(state.rAst[1]);
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustPosition(MyState state) {
        rBench(state.rAst[2]);
    }

    // UNSAFE RUST

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustNameUnsafe(MyState state) {
        rBenchUnsafe(state.rAst[0]);
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustOffsetUnsafe(MyState state) {
        rBenchUnsafe(state.rAst[1]);
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustPositionUnsafe(MyState state) {
        rBench(state.rAst[2]);
    }

    // RUST HANDWRITTEN SERIALIZATION

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustNameSerial(MyState state) {
        App.deserialize(ByteBuffer.wrap(serialize(state.rAst[0])));
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustOffsetSerial(MyState state) {
        App.deserialize(ByteBuffer.wrap(serialize(state.rAst[1])));
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustPositionSerial(MyState state) {
        App.deserialize(ByteBuffer.wrap(serialize(state.rAst[2])));
    }

    // RUST FlATBUFFER SERIALIZATION

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustNameFlatSerial(MyState state) {
        App.flatDeserialize(test.flat.App.getRootAsApp(ByteBuffer.wrap(flatSerialize(state.rAst[0]))), Ast.App);
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustOffsetFlatSerial(MyState state) {
         App.flatDeserialize(test.flat.App.getRootAsApp(ByteBuffer.wrap(flatSerialize(state.rAst[1]))), Ast.App);
    }

    @Benchmark @BenchmarkMode({Mode.AverageTime, Mode.SingleShotTime})
    public void testRustPositionFlatSerial(MyState state) {
        App.flatDeserialize(test.flat.App.getRootAsApp(ByteBuffer.wrap(flatSerialize(state.rAst[2]))), Ast.App);
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
