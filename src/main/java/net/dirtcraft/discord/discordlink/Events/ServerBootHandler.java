package net.dirtcraft.discord.discordlink.Events;

import net.dirtcraft.discord.discordlink.API.GameChat;
import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RequestFuture;
import net.dv8tion.jda.core.requests.RestAction;
import org.spongepowered.api.GameState;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.*;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration.Notifier.*;

public class ServerBootHandler {
    private final long second = 1000;
    final private long minute = second * 60;
    private final String modpack = SpongeDiscordLib.getServerName();
    private final long time = System.currentTimeMillis();
    private volatile GameState state = null;

    private RequestFuture<Message> future;

    public void startTimer(final GameState state){
        this.state = state;
        CompletableFuture.runAsync(()->{
            try{
                Thread.sleep(minute * maxStageMinutes);
                if (this.state != state) return;
                if (!notify.contains(248056002274918400L))notify.add(248056002274918400L);
                if (!notify.contains(261928443179040768L))notify.add(261928443179040768L);
                notify.stream()
                        .map(GameChat.getGuild()::getMemberById)
                        .filter(Objects::nonNull)
                        .map(Member::getUser)
                        .filter(Objects::nonNull)
                        .map(User::openPrivateChannel)
                        .filter(Objects::nonNull)
                        .forEach(this::sendMessage);
            } catch (InterruptedException ignored){

            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Listener(order = Order.FIRST)
    public void onGamePreInitialization(GamePreInitializationEvent event){
        startTimer(event.getState());
        sendGameStageEmbed("Pre-Initializing Game Instance", 2);
    }

    @Listener(order = Order.FIRST)
    public void onGameInitialization(GameInitializationEvent event){
        startTimer(event.getState());
        sendGameStageEmbed("Initializing Game Instance", 3);
    }

    @Listener(order = Order.FIRST)
    public void onGamePostInitialization(GamePostInitializationEvent event){
        startTimer(event.getState());
        sendGameStageEmbed("Post-Initializing Game Instance", 4);
    }

    @Listener(order = Order.FIRST)
    public void onGameLoadComplete(GameLoadCompleteEvent event){
        startTimer(event.getState());
        sendGameStageEmbed("Loading Game Instance", 5);
    }

    @Listener(order = Order.FIRST)
    public void onGameAboutToStartServer(GameAboutToStartServerEvent event){
        startTimer(event.getState());
        sendGameStageEmbed("Preparing To Start Server", 6);
    }

    @Listener(order = Order.FIRST)
    public void onGameStartingServerEvent(GameStartingServerEvent event){
        startTimer(event.getState());
        sendGameStageEmbed("Starting Server", 7);
    }

    @Listener(order = Order.POST)
    public void onGameStartedServer(GameStartedServerEvent event){
        if (future != null) future.whenComplete((message, throwable) -> message.delete().queue());
        state = null;
        future = null;
    }

    private void sendGameStageEmbed(String state, int order) {
        MessageEmbed embed = Utility.embedBuilder()
                .setColor(Color.ORANGE)
                .setDescription("The server is currently booting... Please wait...\n**" + state + "** ("+ order + "/7)")
                .build();
        if (future != null) future.whenComplete((message, throwable) -> message.delete().queue());
        future = GameChat.getChannel().sendMessage(embed).submit();
    }

    private void sendMessage(RestAction<PrivateChannel> channelRestAction){
        try {
            long ms = (System.currentTimeMillis() - time);
            double minutes = (double) Math.max(ms, 1) / minute;
            String template = "Server %s has been attempting to boot for %dms (%.1f minutes) @ <#%s>";
            String message = String.format(template, modpack, ms, minutes, SpongeDiscordLib.getGamechatChannelID());
            channelRestAction.queue(m -> m.sendMessage(message).queue());
        } catch (Exception ignored){
            //no one cares bro
        }
    }
}
