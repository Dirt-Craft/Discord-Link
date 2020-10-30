package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
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
            GameChat.sendEmbed("Discord-Link Reboot", "Attempting to reboot the server.");
            source.getMessage().delete().queue(s->{},e->{});
            Thread.sleep(555);
        } catch (Throwable ignored){

        } finally {
            CompletableFuture.runAsync(()->FMLCommonHandler.instance().exitJava(-1, !gracefulExit));
        }
    }
}
