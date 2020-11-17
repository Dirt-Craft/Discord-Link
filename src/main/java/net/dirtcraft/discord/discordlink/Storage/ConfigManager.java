package net.dirtcraft.discord.discordlink.Storage;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;

@SuppressWarnings("UnstableApiUsage")
public class ConfigManager {
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationOptions options;
    private PluginConfiguration config = new PluginConfiguration();
    private CommentedConfigurationNode node = null;

    public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.loader = loader;
        options = ConfigurationOptions.defaults().setShouldCopyDefaults(true);
        this.load();
    }

    public void load() {
        try {
            node = loader.load(options);
            config = node.getValue(TypeToken.of(PluginConfiguration.class), config);
            loader.save(node);
        } catch (IOException | ObjectMappingException exception) {
            exception.printStackTrace();
        }
    }

    public void save(){
        if (node == null) return;
        try {
            loader.save(node.setValue(TypeToken.of(PluginConfiguration.class), config));
        } catch (IOException | ObjectMappingException exception) {
            exception.printStackTrace();
        }
    }
}
