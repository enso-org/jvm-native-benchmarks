#![feature(slice_from_raw_parts)]

pub mod jni_lib;
pub mod jni_raw;
pub mod jni_sys;
pub mod jni_serialize;
#[allow(dead_code, unused_imports)]
pub mod ast_generated;
mod jni_flat_serialize;

use jni_sys::*;

#[derive(Debug,Clone)]
pub enum Ast {
   App {fun:Box<Ast>, arg:Box<Ast>},
   Name {str:String},
   Offset {value:i64},
   Position {at:i64, to:i64},
}

impl Ast {
   pub fn tree(typ:i64, depth:i64) -> Self {
      if depth > 0 { 
         let fun = Box::new(Ast::tree(typ, depth-1));
         let arg = Box::new(Ast::tree(typ, depth-1));
         return Self::App{fun,arg}
      }
      match typ {
         0 => Self::Name{str:"x".into()},
         1 => Self::Offset{value:1},
         _ => Self::Position{at:2, to:3},
      }
   }
}


#[no_mangle]
pub extern "system" fn Java_test_Bench_rAst(_:JNIEnv, _:jclass, typ:jlong, depth:jlong) -> jlong {
    let tree = Box::new(Ast::tree(typ, depth));

    Box::into_raw(tree) as usize as isize as i64
}
