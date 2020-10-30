package net.dirtcraft.discord.discordlink;

import com.google.inject.Inject;
import net.dirtcraft.discord.discordlink.Commands.Sponge.UnVerify;
import net.dirtcraft.discord.discordlink.Commands.Sponge.Verify;
import net.dirtcraft.discord.discordlink.Configuration.ConfigManager;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.Events.*;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.JDA;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

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
        logger.info("Discord Link initializing...");
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
        getJDA().addEventListener(new DiscordEvents());
        logger.info("Discord Link initialized");
    }

    @Listener(order = Order.FIRST)
    public void onConstruction(GameConstructionEvent event){
        Sponge.getEventManager().registerListeners(instance, new ServerBootHandler());
    }

    @Listener(order = Order.PRE)
    public void onGameInit(GameInitializationEvent event) {
        Sponge.getEventManager().registerListeners(instance, new SpongeEvents(instance, storage));
        this.registerCommands();
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

    public static DiscordLink getInstance() {
        return instance;
    }

    public Storage getStorage(){
        return storage;
    }

    public void saveConfig(){
        configManager.save();
    }

    private void registerCommands(){
        CommandSpec verify = CommandSpec.builder()
                .description(Text.of("Verifies your Discord account"))
                .executor(new Verify(storage))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("code"))))
                .build();

        CommandSpec unverify = CommandSpec.builder()
                .description(Text.of("Unverifies your Discord account"))
                .executor(new UnVerify(storage))
                .build();

        Sponge.getCommandManager().register(this, verify, "verify", "link");
        Sponge.getCommandManager().register(this, unverify, "unverify", "unlink");
    }

}
