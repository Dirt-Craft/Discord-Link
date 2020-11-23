package net.dirtcraft.discord.discordlink.Commands.Discord.notify;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Member;

import java.util.Optional;
import java.util.stream.Collectors;

public class List implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, java.util.List<String> args) throws DiscordCommandException {
        try {
            String staff = PluginConfiguration.Notifier.notify.stream()
                    .map(Utility::getMemberById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Member::getEffectiveName)
                    .map(s->" **-** " + s)
                    .collect(Collectors.joining("\n"));
            source.sendCommandResponse("People to notify:", staff, 30);
        } catch (Exception e){
            throw new DiscordCommandException(e.getMessage());
        }
    }
}