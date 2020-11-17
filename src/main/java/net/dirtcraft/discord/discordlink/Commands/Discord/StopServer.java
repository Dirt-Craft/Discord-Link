package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.Channels;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StopServer implements DiscordCommandExecutor {
    private final boolean gracefulExit;

    public StopServer(boolean graceful){
        this.gracefulExit = graceful;
    }

    @Override
    public void execute(MessageSource source, String command, List<String> args) {
        try {
            if (!source.isPrivateMessage()) source.getMessage().delete().queue(s->{},e->{});
            else source.sendCommandResponse("Forced Reboot Scheduled.", "Attempting to reboot the server.");
            Channels.getDefaultChat().sendMessage(source, "Forced Reboot Scheduled.", "Attempting to reboot the server.");
            Thread.sleep(555);
        } catch (Throwable ignored){

        } finally {
            CompletableFuture.runAsync(()->FMLCommonHandler.instance().exitJava(-1, !gracefulExit));
        }
    }
}
