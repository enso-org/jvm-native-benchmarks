#include "jni.h"   // JNI header provided by JDK
#include <stdio.h> // C Standard IO Header
#include <string>
#include <memory>
#include <variant>

struct Ast;
struct App
{
    std::unique_ptr<Ast> fun;
    std::unique_ptr<Ast> arg;
};
struct Name
{
    std::string str;
};
struct Offset
{
    long long value;
};
struct Position
{
    long long at;
    long long to;
};
struct Ast
{
    std::variant<Name, Offset, Position, App> ast;
};

struct App_
{
    JNIEnv *env;
    jclass self;
    jfieldID fun;
    jfieldID arg;

    App_(JNIEnv *env) : env(env),
                        self(env->FindClass("test/App")),
                        fun(env->GetFieldID(self, "fun", "Ljava/lang/Object;")),
                        arg(env->GetFieldID(self, "arg", "Ljava/lang/Object;"))
    {
    }
    jobject init(jobject fun, jobject arg)
    {
        jobject obj = env->AllocObject(self);
        env->SetObjectField(obj, this->fun, fun);
        env->SetObjectField(obj, this->arg, arg);
        return obj;
    }
};
struct Name_
{
    JNIEnv *env;
    jclass self;
    jfieldID str;

    Name_(JNIEnv *env) : env(env),
                         self(env->FindClass("test/Name")),
                         str(env->GetFieldID(self, "str", "Ljava/lang/String;"))
    {
    }
    jobject init(const char *str)
    {
        jobject obj = env->AllocObject(self);
        env->SetObjectField(obj, this->str, env->NewStringUTF(str));
        return obj;
    }
};
struct Offset_
{
    JNIEnv *env;
    jclass self;
    jfieldID value;

    Offset_(JNIEnv *env) : env(env),
                           self(env->FindClass("test/Offset")),
                           value(env->GetFieldID(self, "value", "J"))
    {
    }
    jobject init(long long value)
    {
        jobject obj = env->AllocObject(self);
        env->SetLongField(obj, this->value, value);
        return obj;
    }
};
struct Position_
{
    JNIEnv *env;
    jclass self;
    jfieldID at;
    jfieldID to;

    Position_(JNIEnv *env) : env(env),
                             self(env->FindClass("test/Position")),
                             at(env->GetFieldID(self, "at", "J")),
                             to(env->GetFieldID(self, "to", "J"))
    {
    }
    jobject init(long long at, long long to)
    {
        jobject obj = env->AllocObject(self);
        env->SetLongField(obj, this->at, at);
        env->SetLongField(obj, this->to, to);
        return obj;
    }
};

struct Env
{
    App_ app;
    Name_ name;
    Offset_ offset;
    Position_ position;

    Env(JNIEnv *env) : app(App_(env)), name(Name_(env)), offset(Offset_(env)), position(Position_(env)) {}
};

Ast ast(long long typ, long long depth)
{
    if (depth == 0)
    {
        if (typ == 0)
            return Ast{Name{"x"}};
        if (typ == 1)
            return Ast{Offset{1}};
        if (typ == 2)
            return Ast{Position{2, 3}};
    }
    return Ast{App{std::make_unique<Ast>(ast(typ, depth - 1)), std::make_unique<Ast>(ast(typ, depth - 1))}};
}

extern "C" JNIEXPORT jlong JNICALL Java_test_Bench_cAst(JNIEnv env, jclass obj, jlong typ, jlong depth)
{
    return (long long)std::make_unique<Ast>(ast(typ, depth)).release();
}

jobject build(Env *env, Ast *ast)
{
    int index = ast->ast.index();
    if (index == 0)
        return env->name.init(std::get<Name>(ast->ast).str.c_str());
    if (index == 1)
        return env->offset.init(std::get<Offset>(ast->ast).value);
    if (index == 2)
    {
        Position &position = std::get<Position>(ast->ast);
        return env->position.init(position.at, position.to);
    }

    App &app = std::get<App>(ast->ast);
    return env->app.init(build(env, app.fun.get()), build(env, app.arg.get()));
}

// Implementation of the native method
extern "C" JNIEXPORT jobject JNICALL Java_test_Bench_cBench(JNIEnv *env, jobject obj, jlong ast_ptr)
{
    Ast *ast = (Ast *)ast_ptr;
    Env e = Env(env);
    return build(&e, ast);
}
