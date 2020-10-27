package net.dirtcraft.discord.discordlink;

import com.google.inject.Inject;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandManager;
import net.dirtcraft.discord.discordlink.Commands.SpongeCommandManager;
import net.dirtcraft.discord.discordlink.Configuration.ConfigManager;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.Events.*;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
@Plugin(
        id = "discord-link",
        name = "Discord Link",
        description = "Handles gamechats on the DirtCraft Discord.",
        authors = {
                "juliann",
                "ShinyAfro"
        },
        dependencies = {
                @Dependency(id = "sponge-discord-lib", optional = true),
                @Dependency(id = "ultimatechat", optional = true),
                @Dependency(id = "dirt-database-lib", optional = true)
        }
)
public class DiscordLink {

    private static DiscordCommandManager discordCommandManager;
    private static SpongeCommandManager spongeCommandManager;


    @DefaultConfig(sharedRoot = false)
    @Inject private ConfigurationLoader<CommentedConfigurationNode> loader;
    @Inject private Logger logger;
    @Inject private PluginContainer container;

    private static DiscordLink instance;
    private ConfigManager configManager;
    private Storage storage;

    @Listener (order = Order.AFTER_PRE)
    public void onPreInit(GameConstructionEvent event) {
        instance = this;
        if (!Sponge.getPluginManager().isLoaded("sponge-discord-lib")) {
            logger.error("Sponge-Discord-Lib is not installed! " + container.getName() + " will not load.");
            return;
        }
        if (!Sponge.getPluginManager().isLoaded("dirt-database-lib")) {
            logger.error("Dirt-Database-Lib is not installed! " + container.getName() + " will not load.");
            return;
        }
        this.configManager = new ConfigManager(loader);
        this.storage = new Storage();
        discordCommandManager = new DiscordCommandManager();
        getJDA().addEventListener(new DiscordEvents(discordCommandManager));
        logger.info("Discord Link initializing...");
    }

    @Listener(order = Order.FIRST)
    public void onConstruction(GameConstructionEvent event){
        Sponge.getEventManager().registerListeners(instance, new ServerBootHandler());
    }

    @Listener(order = Order.PRE)
    public void onGameInit(GameInitializationEvent event) {
        Sponge.getEventManager().registerListeners(instance, new SpongeEvents(instance, storage));
        spongeCommandManager = new SpongeCommandManager(this, storage);
        Utility.setStatus();
        Utility.setTopic();

        if (SpongeDiscordLib.getServerName().toLowerCase().contains("pixel")) {
            Sponge.getEventManager().registerListeners(instance, new NormalChat());
        } else {
            Sponge.getEventManager().registerListeners(instance, new UltimateChat());
        }
    }

    public static JDA getJDA() {
        return SpongeDiscordLib.getJDA();
    }

    public static Guild getGuild(){
        return SpongeDiscordLib.getJDA().getTextChannelById(SpongeDiscordLib.getGamechatChannelID()).getGuild();
    }

    public static DiscordLink getInstance() {
        return instance;
    }

    public static DiscordCommandManager getDiscordCommandManager(){
        return discordCommandManager;
    }

    public Storage getStorage(){
        return storage;
    }

    public void saveConfig(){
        configManager.save();
    }

}
