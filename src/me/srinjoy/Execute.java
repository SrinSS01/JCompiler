package me.srinjoy;

import org.joor.Reflect;
import org.joor.ReflectException;

import java.util.Objects;

public class Execute {
    private final String Class;
    private final String Content;

    public Execute(String aClass, String content) {
        this.Class = aClass;
        this.Content = content;
    }

    public Class<?> execute(){
        try {
            return Reflect.compile(Class,Content).get();
        } catch (ReflectException e) {
            e.printStackTrace();
            Objects.requireNonNull(Main.jda.getTextChannelById(Events.channel)).sendMessage("```"+e.getMessage()+"```").queue();
            return null;
        }
    }

}
