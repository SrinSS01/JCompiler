package me.srinjoy

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.requests.GatewayIntent.*
import net.dv8tion.jda.api.utils.cache.CacheFlag.EMOTE
import net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE

object Main {
    @JvmStatic fun main(args: Array<String>) {
        val token = System.getenv("TOKEN") ?: if (args.isEmpty())
            throw Exception("Token not found!!")
        else args[0]
        JDABuilder.createDefault(
            token,
            GUILD_MESSAGES,
            GUILD_MESSAGE_REACTIONS,
            GUILD_VOICE_STATES,
            GUILD_EMOJIS
        ).disableCache(VOICE_STATE, EMOTE).build().run {
            addEventListener(Events())
            presence.setStatus(OnlineStatus.ONLINE)
            presence.activity = Activity.playing("Java")
            awaitReady().guilds.forEach {
                it.run {
                    println(name)
                    upsertCommand("stop", "stop the bot").queue()
                    upsertCommand(
                        CommandData("compile", "compile the given java code")
                            .addOptions(
                                OptionData(OptionType.STRING, "code", "```java code```", true),
                                OptionData(OptionType.STRING, "args", "arguments"),
                                OptionData(OptionType.STRING, "stdin", "inputs for stdin"),
                            )
                    ).queue()
                }
            }
        }
    }
}