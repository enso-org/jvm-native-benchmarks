use jni::JNIEnv;
use jni::objects::*;
use jni::sys::*;
use std::time::Instant;
use crate::Ast;


#[derive(Clone)]
pub struct Env<'a> {
    app: App<'a>,
    name: Name<'a>,
    position: Position<'a>,
    offset: Offset<'a>,
}

#[derive(Clone)]
pub struct App<'a> {
    env: &'a JNIEnv<'a>,
    this: JClass<'a>,
    fun: JFieldID<'a>,
    arg: JFieldID<'a>,
}

#[derive(Clone)]
pub struct Name<'a> {
    env: &'a JNIEnv<'a>,
    this: JClass<'a>,
    str: JFieldID<'a>,
}

#[derive(Clone)]
pub struct Position<'a> {
    env: &'a JNIEnv<'a>,
    this: JClass<'a>,
    at: JFieldID<'a>,
    to: JFieldID<'a>,
}

#[derive(Clone)]
pub struct Offset<'a> {
    env: &'a JNIEnv<'a>,
    this: JClass<'a>,
    value: JFieldID<'a>,
}

impl<'a> App<'a> {
    pub fn new(env: &'a JNIEnv<'a>) -> Self {
        let this = env.find_class("test/App").unwrap();
        let fun = env.get_field_id("test/App", "fun", "Ljava/lang/Object;").unwrap();
        let arg = env.get_field_id("test/App", "arg", "Ljava/lang/Object;").unwrap();
        Self { env, this, fun, arg }
    }
    pub fn init(&self, fun: JValue, arg: JValue) -> JValue<'a> {
        let this = self.env.alloc_object(self.this).unwrap();
        self.env.set_field_unchecked(this, self.fun, fun).unwrap();
        self.env.set_field_unchecked(this, self.arg, arg).unwrap();
        this.into()
    }
}

impl<'a> Name<'a> {
    pub fn new(env: &'a JNIEnv<'a>) -> Self {
        let this = env.find_class("test/Name").unwrap();
        let str = env.get_field_id("test/Name", "str", "Ljava/lang/String;").unwrap();
        Self { env, this, str }
    }
    pub fn init<'b>(&self, str: &'b str) -> JValue<'a> {
        let this = self.env.alloc_object(self.this).unwrap();
        let str = self.env.new_string(str).unwrap().into();
        self.env.set_field_unchecked(this, self.str, str).unwrap();
        this.into()
    }
}

impl<'a> Offset<'a> {
    pub fn new(env: &'a JNIEnv<'a>) -> Self {
        let this = env.find_class("test/Offset").unwrap();
        let value = env.get_field_id("test/Offset", "value", "J").unwrap();
        Self { env, this, value }
    }
    pub fn init(&self, value: i64) -> JValue<'a> {
        let this = self.env.alloc_object(self.this).unwrap();
        self.env.set_field_unchecked(this, self.value, value.into()).unwrap();
        this.into()
    }
}

impl<'a> Position<'a> {
    pub fn new(env: &'a JNIEnv<'a>) -> Self {
        let this = env.find_class("test/Position").unwrap();
        let at = env.get_field_id("test/Position", "at", "J").unwrap();
        let to = env.get_field_id("test/Position", "to", "J").unwrap();
        Self { env, this, at, to }
    }
    pub fn init(&self, at: i64, to: i64) -> JValue<'a> {
        let this = self.env.alloc_object(self.this).unwrap();
        self.env.set_field_unchecked(this, self.at, at.into()).unwrap();
        self.env.set_field_unchecked(this, self.to, to.into()).unwrap();
        this.into()
    }
}

impl<'a> Env<'a> {
    pub fn new(env: &'a JNIEnv) -> Self {
        let app = App::new(env);
        let name = Name::new(env);
        let position = Position::new(env);
        let offset = Offset::new(env);
        Env { app, name, position, offset }
    }

    pub fn tree(&self, ast: &Ast) -> JValue<'a> {
        match ast {
            Ast::App { fun, arg } => self.app.init(self.tree(fun), self.tree(arg)),
            Ast::Name { str } => self.name.init(str.as_str()),
            Ast::Offset { value } => self.offset.init(*value),
            Ast::Position { at, to } => self.position.init(*at, *to),
        }
    }
}


#[no_mangle]
pub extern "system" fn Java_test_Bench_rBench(env: JNIEnv, _: JClass, ast: jlong) -> jweak {
    let ast = unsafe { Box::from_raw(ast as isize as usize as *mut Ast) };
    // println!("{:?}", ast);
    let env = Env::new(&env);
    // let now = Instant::now();
    let obj = env.tree(&ast);
    // println!("Elapsed {}ms", now.elapsed().as_millis());
    let _ = Box::into_raw(ast);
    obj.l().unwrap().into_inner()
}
