#include "jni.h"        // JNI header provided by JDK
#include <stdio.h>      // C Standard IO Header
#include <sys/time.h>
#include <string>
#include <memory>

struct Ast;
struct App {
    std::unique_ptr<Ast> fun;
};
struct Name { std::string str; };
union AnyAst { App app; Name name; };
struct Ast {
    short typ;
    AnyAst ast;
     };

struct App_ {
    JNIEnv *env;
    jclass self;
    jfieldID fun;
    jfieldID arg;

    App_(JNIEnv *env) :
        env(env),
        self(env->FindClass("test/App")),
        fun(env->GetFieldID(self, "fun", "Ljava/lang/Object;")),
        arg(env->GetFieldID(self, "arg", "Ljava/lang/Object;"))
    {}
    jobject init(jobject fun, jobject arg) {
        jobject obj = env->AllocObject(self);
        env->SetObjectField(obj,this->fun,fun);
        env->SetObjectField(obj,this->arg,arg);
        return obj;
    }
};
 struct Name_ {
     JNIEnv *env;
     jclass self;
     jfieldID str;

     Name_(JNIEnv *env) :
        env(env),
         self(env->FindClass("test/Name")),
         str(env->GetFieldID(self, "str", "Ljava/lang/String;"))
     {}
     jobject init(char* str) {
         jobject obj = env->AllocObject(self);
         env->SetObjectField(obj,this->str,env->NewStringUTF(str));
         return obj;
     }
 };
 struct Offset_ {
     JNIEnv *env;
     jclass self;
     jfieldID value;

     Offset_(JNIEnv *env) :
        env(env),
         self(env->FindClass("test/Offset")),
         value(env->GetFieldID(self, "value", "J"))
     {}
     jobject init(long long value) {
         jobject obj = env->AllocObject(self);
         env->SetLongField(obj,this->value,value);
         return obj;
     }
 };
 struct Position_ {
     JNIEnv *env;
     jclass self;
     jfieldID at;
     jfieldID to;

     Position_(JNIEnv *env) :
        env(env),
         self(env->FindClass("test/Position")),
         at(env->GetFieldID(self, "at", "J")),
         to(env->GetFieldID(self, "to", "J"))
     {}
     jobject init(long long at, long long to) {
         jobject obj = env->AllocObject(self);
         env->SetLongField(obj,this->at,at);
         env->SetLongField(obj,this->to,to);
         return obj;
     }
 };

struct Env {
    App_ app;
     Name_ name;
     Offset_ offset;
     Position_ position;

    Env(JNIEnv *env) : app(App_(env)), name(Name_(env)), offset(Offset_(env)), position(Position_(env)) {}

};

Ast ast(long long typ, long long depth) {
    if (depth == 0) {
        if (typ == 0) return Ast(0, AnyAst{.name=Name{""}});
    }
//    return Ast{typ:3, ast:AnyAst{app:App{fun:ast(typ,depth-1), arg:ast(typ,depth-1)}}};
}

jobject tree(Env *env, long long typ, long long depth) {
    if (depth == 0) {
        if (typ == 0) return env->name.init("");
        if (typ == 1) return env->offset.init(0);
        if (typ == 2) return env->position.init(0,0);
    }
    return env->app.init(tree(env, typ, depth-1), tree(env,typ,depth-1));
}

// Implementation of the native method
extern "C" JNIEXPORT jobject JNICALL Java_test_Bench_tree(JNIEnv *env, jobject obj, jlong typ, jlong depth) {
    Env e = Env(env);
    return tree(&e, typ, depth);
}
