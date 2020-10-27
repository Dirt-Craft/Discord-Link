package net.dirtcraft.discord.discordlink.Commands.Discord.notify;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dv8tion.jda.api.entities.Member;

import java.util.Objects;
import java.util.stream.Collectors;

public class List implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, java.util.List<String> args) throws DiscordCommandException {
        try {
            String staff = PluginConfiguration.Notifier.notify.stream()
                    .map(GameChat.getGuild()::getMemberById)
                    .filter(Objects::nonNull)
                    .map(Member::getEffectiveName)
                    .map(s->" **-** " + s)
                    .collect(Collectors.joining("\n"));
            GameChat.sendEmbed("People to notify:", staff, 30);
        } catch (Exception e){
            throw new DiscordCommandException(e.getMessage());
        }
    }
}