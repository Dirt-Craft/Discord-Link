package net.dirtcraft.discordlink.channels;

import net.dirtcraft.spongediscordlib.channels.ChannelManager;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class ChannelManagerImpl implements ChannelManager {
    private final JDA jda;
    private LogChannelImpl logChannel;
    private GameChatChannelImpl defaultChannel;
    private long guild;

    public ChannelManagerImpl(JDA jda){
        this.jda = jda;
        loadFromConfig();
    }

    @Override
    public DiscordChannelImpl getChannel(long channel){
        return new DiscordChannelImpl(jda, channel);
    }

    @Override
    public DiscordChannelImpl getChannel(long channel, boolean isPrivate){
        return new DiscordChannelImpl(jda, channel, isPrivate);
    }

    @Override
    public GameChatChannelImpl getGameChat(){
        return defaultChannel;
    }

    @Override
    public LogChannelImpl getLogChannel() {
        return logChannel;
    }

    public TextChannel getDefaultChannel(){
        return jda.getTextChannelById(defaultChannel.getId());
    }

    public boolean isGamechat(MessageChannel channel){
        return channel.getIdLong() == defaultChannel.getId();
    }

    public Guild getGuild(){
        return jda.getGuildById(guild);
    }

    public void loadFromConfig(){
        logChannel = new LogChannelImpl(jda, Long.parseLong(PluginConfiguration.Main.serverLogChannelID));
        defaultChannel = new GameChatChannelImpl(jda, PluginConfiguration.Main.defaultChannelID);
        guild = getDefaultChannel() != null ? getDefaultChannel().getGuild().getIdLong() : -1;
    }
}
