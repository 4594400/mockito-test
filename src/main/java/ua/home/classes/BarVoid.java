package ua.home.classes;


import ua.home.interfaces.FooVoid;

public class BarVoid {
    private FooVoid foo;

    public BarVoid(FooVoid foo) {
        this.foo = foo;
    }

    public void bar(String parameter){
        foo.foo(parameter);
    }
}
