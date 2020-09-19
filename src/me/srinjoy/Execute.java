package me.srinjoy;

import org.joor.Reflect;

public class Execute {
    private final String Class;
    private final String Content;

    public Execute(String aClass, String content) {
        this.Class = aClass;
        this.Content = content;
    }

    public Class<?> execute(){
        return Reflect.compile(Class,Content).get();
    }

}
