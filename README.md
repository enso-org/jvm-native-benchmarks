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
  