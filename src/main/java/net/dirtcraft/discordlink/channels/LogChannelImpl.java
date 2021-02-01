package net.dirtcraft.discordlink.channels;

import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dirtcraft.spongediscordlib.channels.LogChannel;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class LogChannelImpl extends DiscordChannelImpl implements LogChannel {
    LogChannelImpl(JDA jda, long channel) {
        super(jda, channel);
    }

    LogChannelImpl(JDA jda, long channel, boolean isPrivate) {
        super(jda, channel, isPrivate);
    }

    public void sendLog(String header, String message) {
        header = header == null? "" : header;
        MessageEmbed embed = Utility.embedBuilder()
                .addField(header, message, false)
                .setFooter("modpack: " + SpongeDiscordLib.getServerName())
                .build();
        sendMessage(embed);
    }
}
