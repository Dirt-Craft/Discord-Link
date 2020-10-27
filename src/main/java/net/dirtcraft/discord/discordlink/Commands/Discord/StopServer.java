package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

public class StopServer implements DiscordCommandExecutor {
    private final boolean gracefulExit;

    public StopServer(boolean graceful){
        this.gracefulExit = graceful;
    }

    @Override
    public void execute(MessageSource source, String command, List<String> args) {
        GameChat.sendEmbed("Discord-Link Reboot", "Attempting to reboot the server.", 15);
        try{
            Thread.sleep(50);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        FMLCommonHandler.instance().exitJava(-1, !gracefulExit);
    }
}
