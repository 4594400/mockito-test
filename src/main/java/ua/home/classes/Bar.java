package ua.home.classes;


import ua.home.interfaces.Foo;

public class Bar {
    private Foo foo;

    public Bar(Foo foo) {
        this.foo = foo;
    }

    public String bar (String parameter){
        return foo.foo(parameter);
    }
}
