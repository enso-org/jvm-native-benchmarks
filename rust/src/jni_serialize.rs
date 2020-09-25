use crate::Ast;

use std::ptr::slice_from_raw_parts;
use jni::JNIEnv;
use jni::objects::*;
use jni::sys::*;
use std::convert::{TryFrom, TryInto};

pub fn serialize(ast:&Ast, buf:&mut Vec<u8>) {
    match ast {
        Ast::Name{str} => {
            buf.push(0i8 as u8); // enum tag
            let bytes = str.as_bytes();
            buf.extend_from_slice(&(i32::try_from(bytes.len()).unwrap()).to_be_bytes());
            buf.extend_from_slice(bytes);
        }
        Ast::Offset{value} =>{
            buf.push(1i8 as u8); // enum tag
            buf.extend_from_slice(&value.to_be_bytes());
        }
        Ast::Position{at,to} => {
            buf.push(2i8 as u8); // enum tag
            buf.extend_from_slice(&at.to_be_bytes());
            buf.extend_from_slice(&to.to_be_bytes());
        }
        Ast::App{fun, arg} => {
            buf.push(3i8 as u8); // enum tag
            serialize(fun, buf);
            serialize(arg, buf);
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_test_Bench_serialize(env: JNIEnv, _: JClass, ast: jlong) -> jbyteArray {
    let ast = unsafe { Box::from_raw(ast as isize as usize as *mut Ast) };
    let mut buf = Vec::new();
    serialize(&ast, &mut buf);
    let buf = unsafe{ &*slice_from_raw_parts(buf.as_ptr() as *const i8, buf.len()) };
    let arr = env.new_byte_array(buf.len().try_into().unwrap()).unwrap();
    env.set_byte_array_region(arr, 0, buf).unwrap();
    Box::into_raw(ast);
    arr
}
