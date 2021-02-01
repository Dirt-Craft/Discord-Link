package net.dirtcraft.discordlink.commands.discord.notify;

import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.spongediscordlib.users.MessageSource;
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