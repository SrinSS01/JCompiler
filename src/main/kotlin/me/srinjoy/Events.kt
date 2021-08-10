package me.srinjoy

import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.joor.Reflect
import org.joor.ReflectException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class Events : ListenerAdapter() {
    override fun onReady(event: ReadyEvent) {
        println("${event.jda.selfUser.name} is ready!!")
    }
    override fun onSlashCommand(event: SlashCommandEvent) {
        println(event.name)
        when (event.name) {
            "stop" -> {
                event.reply("shutting down ${event.jda.selfUser.asMention}").setEphemeral(true).queue()
                event.jda.shutdown()
            }
            "compile" -> {
                println("compiling...")
                var code = event.getOption("code")?.asString!!.run {
                    if (contains("class")) this else {
                        event.reply("unable to find main class").setEphemeral(true).queue()
                        return
                    }
                }
                val args = event.getOption("args")?.asString?.split(" ")
                event.getOption("stdin")?.asString?.let { System.setIn(ByteArrayInputStream(it.toByteArray())) }
                if (!code.contains("```")) {
                    event.reply("enclose the code in a code block").setEphemeral(true).queue()
                    return
                }
                println("executing $code")
                code = code.replace(if (code.contains("```java")) "```java" else "```", "")
                val name = code.run {
                    (indexOf("class") + 5).let {
                        substring(it, indexOf("{", it))
                    }.trim()
                }
                println("class name: $name")
                val out = System.out
                val baosOut = ByteArrayOutputStream()
                System.setOut(PrintStream(baosOut))

                try {
                    Reflect.compile(name, code).call("main", args?.toTypedArray())
                    val stdout = String(baosOut.toByteArray())
                    println("stdout = $stdout")
                    event.reply("```output: $stdout```").queue()
                } catch (reflect: ReflectException) {
                    reflect.message?.let {
                        event.reply(it).setEphemeral(true).queue()
                        println("stderr = $it")
                    }
                    reflect.printStackTrace()
                }

                System.setOut(out)
            }
        }
    }
}