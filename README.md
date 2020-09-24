### Steps To Run

#### Linux

1. `cd rust; cargo build --release; cd ..; cp rust/target/release/rust.so ./rust.so`
2. ```
   cd cpp
   g++ -std=c++17 -O3 -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -shared -o cpp.so main.c
   cd ..
   cp cpp/cpp.so ./cpp.so
   ```
3. run java application with `-Djava.library.path=.`
  
 
#### Windows

1. `cd rust; cargo build --release; cd ..; cp rust/target/release/rust.dll ./rust.dll`
2. ```
   cd cpp
   g++ -std=c++17 -O3 -fPIC -I"%JAVA_HOME%/include" -I"%JAVA_HOME%/include/windows" -shared -o cpp.dll main.c
   cd ..
   cp cpp/cpp.dll ./cpp.dll
   ```
3. run java applicatio with `-Djava.library.path=.`
  
  
#### Mac

1. `cd rust; cargo build --release; cd ..; cp rust/target/release/rust.dylib ./rust.dylib`
2. ```
   cd cpp
   g++ -std=c++17 -O3 -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/darwin" -dynamiclib -o cpp.dylib main.c
   cd ..
   cp cpp/cpp.dylib ./cpp.dylib
   ```
3. run java applicatio with `-Djava.library.path=.`
  
  
### Results (operations / second)


| method      | Leave=Name("x") | Leave=Offset(1) | Leave=Position(2,3) |
|:--          | :--:            |:--:             |:--:                 |
| Java        | 107.683         | 102.363         | 99.934              |
| C++         | 1.649           | 4.019           | 3.618               |
| Rust        | 0.561           | 2.422           | 2.195               |
| Unsafe Rust | 2.268           | 3.402           | 3.368               |


All tests construct an `App(fun, arg)` tree with depth=20 that is a copy of a preallocated tree.
The objects are constructed trough JNI by `AllocObject + SetField`.

- C++: The preallocated tree is of form `App(UniquePtr<Ast>, UniquePtr<Ast>)`.
- Rust: The preallocated tree is of form `App(Box<Ast>, Box<Ast>)`.
- Unsafe Rust: Uses JNI API directly instead of using the jni rust library.


Result "test.Bench.testCName":
  1.649 ±(99.9%) 0.158 ops/s [Average]
  (min, avg, max) = (1.594, 1.649, 1.696), stdev = 0.041
  CI (99.9%): [1.491, 1.807] (assumes normal distribution)