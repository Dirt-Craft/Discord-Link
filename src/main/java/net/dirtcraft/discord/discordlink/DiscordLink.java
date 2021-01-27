package net.dirtcraft.discord.discordlink;

import com.google.inject.Inject;
import net.dirtcraft.discord.discordlink.Commands.Sponge.UnVerify;
import net.dirtcraft.discord.discordlink.Commands.Sponge.Verify;
import net.dirtcraft.discord.discordlink.Commands.Sponge.prefix.*;
import net.dirtcraft.discord.discordlink.Events.*;
import net.dirtcraft.discord.discordlink.Storage.ConfigManager;
import net.dirtcraft.discord.discordlink.Storage.Database;
import net.dirtcraft.discord.discordlink.Storage.Permission;
import net.dirtcraft.discord.discordlink.Storage.Settings;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.JDA;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.GameState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.network.ChannelBinding;
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
                "ShinyAfro",
                "Worive"
        },
        dependencies = {
                @Dependency(id = "sponge-discord-lib", optional = true),
                @Dependency(id = "ultimatechat", optional = true),
                @Dependency(id = "dirt-database-lib", optional = true)
        }
)
public class DiscordLink extends ServerBootHandler {
    private static DiscordLink instance;
    private static JDA jda;

    @DefaultConfig(sharedRoot = false)
    @Inject private ConfigurationLoader<CommentedConfigurationNode> loader;
    @Inject private Logger logger;
    @Inject private PluginContainer container;
    private ChannelBinding.RawDataChannel channel;
    private ConfigManager configManager;
    private Database storage;

    @Override
    @Listener (order = Order.PRE)
    public void onGameConstruction(GameConstructionEvent event) {
        logger.info("Discord Link initializing...");
        SpongeDiscordLib.getInstance().onPreInit(event);
        if (!Sponge.getPluginManager().isLoaded("sponge-discord-lib")) {
            logger.error("Sponge-Discord-Lib is not installed! " + container.getName() + " will not load.");
            return;
        }
        if (!Sponge.getPluginManager().isLoaded("dirt-database-lib")) {
            logger.error("Dirt-Database-Lib is not installed! " + container.getName() + " will not load.");
            return;
        }
        if ((jda = SpongeDiscordLib.getJDA()) == null) {
            logger.error("JDA failed to connect to discord gateway! " + container.getName() + " will not load.");
            return;
        }
        this.configManager = new ConfigManager(loader);
        this.storage = new Database();
        instance = this;

        getJDA().addEventListener(new DiscordEvents());
        super.onGameConstruction(event);
        logger.info("Discord Link initialized");
    }

    @Override
    @Listener(order = Order.PRE)
    public void onGameInitialization(GameInitializationEvent event) {
        super.onGameInitialization(event);
        if (instance == null) return;
        Sponge.getEventManager().registerListeners(instance, new SpongeEvents(instance, storage));
        this.registerCommands();
        Utility.setStatus();
        Utility.setTopic();

        channel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, Settings.ROOT_CHANNEL);
        channel.addListener(new PluginMessageHandler());
        if (SpongeDiscordLib.getServerName().toLowerCase().contains("pixel")) {
            Sponge.getEventManager().registerListeners(instance, new NormalChat());
        } else {
            Sponge.getEventManager().registerListeners(instance, new UltimateChat());
        }
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

        CommandSpec clear = CommandSpec.builder()
                .permission(Permission.PREFIX_CLEAR)
                .executor(new Clear())
                .arguments(GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("Target"))))
                .build();

        CommandSpec toggle = CommandSpec.builder()
                .permission(Permission.PREFIX_TOGGLE)
                .executor(new Toggle())
                .arguments(GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("Target"))))
                .build();

        CommandSpec group = CommandSpec.builder()
                .permission(Permission.PREFIX_GROUP)
                .executor(new Group())
                .arguments(GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("Target"))),
                        GenericArguments.optional(GenericArguments.onlyOne(GenericArguments.string(Text.of("Group")))))
                .build();

        CommandSpec test = CommandSpec.builder()
                .permission(Permission.PREFIX_TEST)
                .executor(new Test())
                .arguments(GenericArguments.flags()
                        .flag("s")
                        .valueFlag(GenericArguments.string(Text.of("ArrowColor")), "a")
                        .valueFlag(GenericArguments.string(Text.of("BracketColor")), "c")
                        .buildWith(GenericArguments.seq(
                                GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("Target"))),
                                GenericArguments.remainingJoinedStrings(Text.of("Prefix")))))
                .build();

        CommandSpec set = CommandSpec.builder()
                .permission(Permission.PREFIX_MODIFY)
                .executor(new Set())
                .arguments(GenericArguments.flags()
                        .flag("s")
                        .valueFlag(GenericArguments.string(Text.of("ArrowColor")), "a")
                        .valueFlag(GenericArguments.string(Text.of("BracketColor")), "c")
                        .buildWith(GenericArguments.seq(
                                GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("Target"))),
                                GenericArguments.remainingJoinedStrings(Text.of("Prefix")))))
                .build();

        CommandSpec prefix = CommandSpec.builder()
                .permission(Permission.PREFIX_USE)
                .child(clear, "clear", "none", "c")
                .child(toggle, "toggle", "arrow", "star", "t")
                .child(set, "set", "s")
                .child(group, "group", "g")
                .child(test, "debug", "test", "d")
                .build();

        Sponge.getCommandManager().register(this, verify, "verify", "link");
        Sponge.getCommandManager().register(this, unverify, "unverify", "unlink");
        Sponge.getCommandManager().register(this, prefix, "prefix");
    }

    public void saveConfig(){
        configManager.save();
    }

    public Database getStorage(){
        return storage;
    }

    public Logger getLogger(){
        return logger;
    }

    public ChannelBinding.RawDataChannel getChannel(){
        return channel;
    }

    public static DiscordLink getInstance() {
        return instance;
    }

    public static JDA getJDA() {
        return jda;
    }

    private static boolean isReady(){
        return instance != null &&
               jda != null &&
               Sponge.getGame().getState() == GameState.SERVER_STARTED;
    }

}
