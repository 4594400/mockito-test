package ua.home.interfaces.impl;


import ua.home.interfaces.Foo;

public class FooImpl implements Foo {
    @Override
    public String foo(String parameter) {
        return parameter;
    }
}
