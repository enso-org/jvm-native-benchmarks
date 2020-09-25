## Results 

### Serialization

#### Milliseconds / run (Cold Start)

| method      | Leaf=Name("x")  | Leaf=Offset(1)  | Leaf=Position(2,3) |
|:--          | :--:            |:--:             |:--:                |
| handwritten | 208             | 142             | 186                |
| flatbuffers | 463             | 365             | 423                |

#### Milliseconds / run (Average)

| method      | Leaf=Name("x")  | Leaf=Offset(1)  | Leaf=Position(2,3) |
|:--          | :--:            |:--:             |:--:                |
| handwritten | 98              | 80              | 92                 |
| flatbuffers | 338             | 285             | 322                |


### AllocObject + SetField

#### Milliseconds / run (Cold Start)

| method      | Leaf=Name("x") | Leaf=Offset(1) | Leaf=Position(2,3) |
|:--          | :--:           |:--:            | :--:               |
| Java        | 66             | 34             | 45                 |
| C++         | 731            | 283            | 302                |
| Rust        | 1 306          | 434            | 546                |
| Unsafe Rust | 673            | 415            | 492                |

#### Milliseconds / run (Average)

| method      | Leaf=Name("x") | Leaf=Offset(1) | Leaf=Position(2,3) |
|:--          | :--:           |:--:            | :--:               |
| Java        | 10             | 9              | 9                  |
| C++         | 611            | 258            | 288                |
| Rust        | 993            | 413            | 472                |
| Unsafe Rust | 443            | 311            | 307                |

#### Runs / second (Average)

| method      | Leaf=Name("x") | Leaf=Offset(1) | Leaf=Position(2,3) |
|:--          | :--:           |:--:            | :--:               |
| Java        | 107.683        | 102.363        | 99.934             |
| C++         | 1.649          | 4.019          | 3.618              |
| Rust        | 0.561          | 2.422          | 2.195              |
| Unsafe Rust | 2.268          | 3.402          | 3.368              |

All tests construct an `App(fun, arg)` tree with depth=20 that is a copy of a preallocated tree.
The objects were constructed with JNI API `AllocObject + SetField`.

- C++: The preallocated tree is of form `App(UniquePtr<Ast>, UniquePtr<Ast>)`.
- Rust: The preallocated tree is of form `App(Box<Ast>, Box<Ast>)`.
- Unsafe Rust: Uses JNI API directly instead of using the jni rust library.


## Steps To Run

#### Linux

1. ```
   cd rust; cargo build --release; cd ..; cp rust/target/release/rust.so ./rust.so
   ```
2. ```
   cd cpp
   g++ -std=c++17 -O3 -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -shared -o cpp.so main.c
   cd ..
   cp cpp/cpp.so ./cpp.so
   ```
3. run java application with `-Djava.library.path=.`
  
 
#### Windows

1. ```
   cd rust; cargo build --release; cd ..; cp rust/target/release/rust.dll ./rust.dll
   ```
2. ```
   cd cpp
   g++ -std=c++17 -O3 -fPIC -I"%JAVA_HOME%/include" -I"%JAVA_HOME%/include/windows" -shared -o cpp.dll main.c
   cd ..
   cp cpp/cpp.dll ./cpp.dll
   ```
3. run java applicatio with `-Djava.library.path=.`
  
  
#### Mac

1. ```
   cd rust; cargo build --release; cd ..; cp rust/target/release/rust.dylib ./rust.dylib
   ```
2. ```
   cd cpp
   g++ -std=c++17 -O3 -fPIC -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/darwin" -dynamiclib -o cpp.dylib main.c
   cd ..
   cp cpp/cpp.dylib ./cpp.dylib
   ```
3. run java applicatio with `-Djava.library.path=.`
  

