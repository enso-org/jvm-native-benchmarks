use crate::ast_generated::*;
use flatbuffers::{FlatBufferBuilder, WIPOffset, UnionWIPOffset};
use jni::sys::*;
use jni::JNIEnv;
use jni::objects::*;
use std::ptr::slice_from_raw_parts;
use std::convert::TryInto;

pub fn ast_type(ast:&crate::Ast) -> Ast {
    match ast {
        crate::Ast::Name{..}     => Ast::Name,
        crate::Ast::Offset{..}   => Ast::Offset,
        crate::Ast::Position{..} => Ast::Position,
        crate::Ast::App{..}      => Ast::App,
    }
}

pub fn serialize(ast:&crate::Ast, buf:&mut FlatBufferBuilder) -> WIPOffset<UnionWIPOffset> {
    match ast {
        crate::Ast::Name{str} => {
            let str = Some(buf.create_string(str.as_str()));
            Name::create(buf, &NameArgs{str}).as_union_value()
        }
        crate::Ast::Offset{value} =>{
            Offset::create(buf, &OffsetArgs{value:*value}).as_union_value()
        }
        crate::Ast::Position{at,to} => {
            Position::create(buf, &PositionArgs{at:*at, to:*to}).as_union_value()
        }
        crate::Ast::App{fun, arg} => {
            let fun_type = ast_type(fun);
            let arg_type = ast_type(fun);
            let fun      = Some(serialize(fun, buf));
            let arg      = Some(serialize(arg, buf));
            App::create(buf, &AppArgs{fun_type,fun,arg_type,arg}).as_union_value()
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_test_Bench_flatSerialize(env: JNIEnv, _: JClass, ast: jlong) -> jbyteArray {
    let ast = unsafe { Box::from_raw(ast as isize as usize as *mut crate::Ast) };
    let mut buf = FlatBufferBuilder::new();
    let offset = serialize(&ast, &mut buf);
    buf.finish(offset, None);
    let buf = buf.finished_data();
    let buf = unsafe{ &*slice_from_raw_parts(buf.as_ptr() as *const i8, buf.len()) };
    let arr = env.new_byte_array(buf.len().try_into().unwrap()).unwrap();
    env.set_byte_array_region(arr, 0, buf).unwrap();
    Box::into_raw(ast);
    arr
}
