use crate::jni_sys::*;
use std::time::Instant;
use crate::Ast;

#[derive(Copy, Clone)]
pub struct JNIEnv {
    e: *mut crate::jni_sys::JNIEnv
}

#[derive(Clone)]
pub struct Env {
    app: App,
    name: Name,
    offset: Offset,
    position: Position,
}

#[derive(Clone)]
pub struct App {
    env: JNIEnv,
    this: jclass,
    fun: jfieldID,
    arg: jfieldID,
}

#[derive(Clone)]
pub struct Name {
    env: JNIEnv,
    this: jclass,
    str: jfieldID,
}

#[derive(Clone)]
pub struct Position {
    env: JNIEnv,
    this: jclass,
    at: jfieldID,
    to: jfieldID,
}

#[derive(Clone)]
pub struct Offset {
    env: JNIEnv,
    this: jclass,
    value: jfieldID,
}


fn s(str: &str) -> *const i8 {
    str.as_ptr() as *const i8
}

impl App {
    pub fn new(env: JNIEnv) -> Self {
        unsafe {
            let this = (**env.e).FindClass.unwrap()(env.e, s("test/App\0"));
            let fun = (**env.e).GetFieldID.unwrap()(env.e, this, s("fun\0"), s("Ljava/lang/Object;\0"));
            let arg = (**env.e).GetFieldID.unwrap()(env.e, this, s("arg\0"), s("Ljava/lang/Object;\0"));
            Self { env, this, fun, arg }
        }
    }
    pub fn init(&mut self, fun: jobject, arg: jobject) -> jobject {
        unsafe {
            let this = (**self.env.e).AllocObject.unwrap()(self.env.e, self.this);
            (**self.env.e).SetObjectField.unwrap()(self.env.e, this, self.fun, fun);
            (**self.env.e).SetObjectField.unwrap()(self.env.e, this, self.arg, arg);
            this
        }
    }
}

impl Name {
    pub fn new(env: JNIEnv) -> Self {
        unsafe {
            let this = (**env.e).FindClass.unwrap()(env.e, s("test/Name\0"));
            let str = (**env.e).GetFieldID.unwrap()(env.e, this, s("str\0"), s("Ljava/lang/String;\0"));
            Self { env, this, str }
        }
    }
    pub fn init(&mut self, str: &str) -> jobject {
        unsafe {
            let this = (**self.env.e).AllocObject.unwrap()(self.env.e, self.this);
            let str = (**self.env.e).NewStringUTF.unwrap()(self.env.e, s(str));
            (**self.env.e).SetObjectField.unwrap()(self.env.e, this, self.str, str);
            this
        }
    }
}

impl Offset {
    pub fn new(env: JNIEnv) -> Self {
        unsafe {
            let this = (**env.e).FindClass.unwrap()(env.e, s("test/Offset\0"));
            let value = (**env.e).GetFieldID.unwrap()(env.e, this, s("value\0"), s("J\0"));
            Self { env, this, value }
        }
    }
    pub fn init(&mut self, value: i64) -> jobject {
        unsafe {
            let this = (**self.env.e).AllocObject.unwrap()(self.env.e, self.this);
            (**self.env.e).SetLongField.unwrap()(self.env.e, this, self.value, value.into());
            this.into()
        }
    }
}

impl Position {
    pub fn new(env: JNIEnv) -> Self {
        unsafe {
            let this = (**env.e).FindClass.unwrap()(env.e, s("test/Position\0"));
            let at = (**env.e).GetFieldID.unwrap()(env.e, this, s("at\0"), s("J\0"));
            let to = (**env.e).GetFieldID.unwrap()(env.e, this, s("to\0"), s("J\0"));
            Self { env, this, at, to }
        }
    }
    pub fn init(&self, at: i64, to: i64) -> jobject {
        unsafe {
            let this = (**self.env.e).AllocObject.unwrap()(self.env.e, self.this);
            (**self.env.e).SetLongField.unwrap()(self.env.e, this, self.at, at);
            (**self.env.e).SetLongField.unwrap()(self.env.e, this, self.to, to);
            this
        }
    }
}

impl Env {
    pub fn new(env: JNIEnv) -> Self {
        let app = App::new(env);
        let name = Name::new(env);
        let offset = Offset::new(env);
        let position = Position::new(env);
        Env { app, name, offset, position }
    }

    pub fn tree(&mut self, ast: &Ast) -> jobject {
        match ast {
            Ast::App { fun, arg } => {
                let fun = self.tree(fun);
                let arg = self.tree(arg);
                self.app.init(fun, arg)
            }
            Ast::Name { str } => self.name.init(str.as_str()),
            Ast::Offset { value } => self.offset.init(*value),
            Ast::Position { at, to } => self.position.init(*at, *to),
        }
    }
}


#[no_mangle]
pub extern "system" fn Java_test_Bench_rBenchUnsafe(env: JNIEnv, _: jclass, ast: jlong) -> jobject {
    let ast = unsafe { Box::from_raw(ast as isize as usize as *mut Ast) };
    // println!("{:?}", ast);
    let mut env = Env::new(env);
    // println!("\n\n\n===========\n\n\n");
    // let now = Instant::now();
    let obj = env.tree(&ast);
    // println!("Elapsed {}ms", now.elapsed().as_millis());
    let _ = Box::into_raw(ast);
    obj
}
