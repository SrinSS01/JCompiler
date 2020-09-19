package me.srinjoy;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class Events extends ListenerAdapter {
    static String channel;
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        channel = event.getChannel().getId();
        String message = event.getMessage().getContentRaw();
        if (message.contains("-java") && message.contains("```")){
            Object[] obj = format(message.replace("-java", "").trim());
            System.out.println("Class:" + obj[0]);
            System.out.println(obj[1]);
            Class<?> clazz = new Execute(obj[0].toString(), obj[1].toString()).execute();
            if (obj[2].equals(true)) {
                try {
                    clazz.getDeclaredMethod("main", String[].class).invoke(clazz.newInstance(), new Object[]{null});
                    Field stringBuffer = clazz.getDeclaredField("stringBuffer");
                    event.getChannel().sendMessage((StringBuilder)stringBuffer.get(clazz.newInstance())).queue();
                    stringBuffer.set(clazz.newInstance(), new StringBuilder());
                }
                catch (NoSuchMethodException e)
                    { event.getChannel().sendMessage("Constructor matching " + obj[0]+"() not found").queue(); e.printStackTrace(); }
                catch (IllegalAccessException e)
                    { event.getChannel().sendMessage("class "+obj[0]+" is not public").queue(); e.printStackTrace(); }
                catch (InvocationTargetException | InstantiationException | NoSuchFieldException e) {  e.printStackTrace();  }
            } else event.getChannel().sendMessage("main not found").queue();
        }
    }
    private Object[] format(String message){
        String Class  = "";boolean main = false;
        message = message.substring(message.indexOf("```") + 3, message.lastIndexOf("```"));
        message = (message.contains("java") && message.indexOf("java") == 0)? message.replace("java", "").trim(): message;
        message = message.substring(0, message.indexOf('{') +1)
                    + "\npublic static StringBuilder stringBuffer = new StringBuilder();"
                    + message.substring(message.indexOf('{') +1);
        StringBuilder stringBuilder = new StringBuilder();
        StringTokenizer stringTokenizer = new StringTokenizer(message, "\n");
        while (stringTokenizer.hasMoreTokens()){
            String code = stringTokenizer.nextToken();
            if (code.contains("public static StringBuilder stringBuffer = new StringBuilder();") && !message.contains("public "+Class+"()"))
                stringBuilder.append("\n").append("public ").append(Class).append("(){}").append("\n");
            if (code.contains("System.out.println")){
                String args = code.substring(code.indexOf("System.out.println") + "System.out.println".length());
                args = args.substring(1, args.lastIndexOf(')'));
                code = code.replace("System.out.println("+args+")", "stringBuffer.append("+args+").append(\"\\n\")");
            }
            if (code.contains("System.out.print")){
                String args = code.substring(code.indexOf("System.out.print") + "System.out.print".length());
                args = args.substring(1, args.lastIndexOf(')'));
                code = code.replace("System.out.print("+args+")", "stringBuffer.append("+args+")");
            }
            stringBuilder.append(code).append("\n");
            ArrayList<String> list = new ArrayList<>(Arrays.asList(code.split(" ")));
            if (list.contains("class")) {
                Class = list.get(list.indexOf("class") + 1);
                Class = (Class.contains("{"))? Class.replace('{', ' ').trim() : Class;
            }
            if (list.contains("void") && list.get(list.indexOf("void")+1).contains("main")) main = true;
        }
        return new Object[]{Class, stringBuilder, main};
    }
}
