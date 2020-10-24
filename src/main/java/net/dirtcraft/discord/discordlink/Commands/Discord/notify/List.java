package net.dirtcraft.discord.discordlink.Commands.Discord.notify;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Objects;
import java.util.stream.Collectors;

public class List implements DiscordCommandExecutor {
    @Override
    public void execute(GuildMember source, java.util.List<String> args, MessageReceivedEvent event) throws DiscordCommandException {
        try {
            String staff = PluginConfiguration.Notifier.notify.stream()
                    .map(GameChat.getGuild()::getMemberById)
                    .filter(Objects::nonNull)
                    .map(Member::getEffectiveName)
                    .collect(Collectors.joining("\n"));
            GameChat.sendMessage(staff, 30);
        } catch (Exception e){
            throw new DiscordCommandException(e.getMessage());
        }
    }
}
