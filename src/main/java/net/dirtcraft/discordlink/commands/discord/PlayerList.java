package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.spongediscordlib.commands.DiscordCommandExecutor;
import net.dirtcraft.discordlink.storage.Permission;
import net.dirtcraft.discordlink.users.platform.PlatformPlayerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerList implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) {
        final List<String> players = PlatformProvider.getPlayers().stream()
                .filter(PlatformPlayer::notVanished)
                .sorted(this::sortPlayer)
                .map(this::formatPlayer)
                .collect(Collectors.toList());

        EmbedBuilder embed = Utility.embedBuilder();
        if (players.size() == 1) embed.addField("__**" + players.size() + "** player online__", String.join("\n", players), false);
        else if (!players.isEmpty()) embed.addField("__**" + players.size() + "** players online__", String.join("\n", players), false);
        else embed.setDescription("There are no players playing **" + SpongeDiscordLib.getServerName() + "**!");
        embed.setFooter("Requested By: " + source.getUser().getAsTag(), source.getUser().getAvatarUrl());
        source.sendCommandResponse(embed.build());
    }

    private String formatPlayer(PlatformPlayer platformPlayer){
        String name = platformPlayer.getNameAndPrefix();
        name = Utility.sanitiseMinecraftText(name);
        if (platformPlayer.hasPermission(Permission.ROLES_STAFF)) name = "**" + name + "**";
        return name;
    }

    private int sortPlayer(PlatformPlayer a, PlatformPlayer b){
        if (a.hasPermission(Permission.ROLES_STAFF) && !b.hasPermission(Permission.ROLES_STAFF)) return -1;
        else if (b.hasPermission(Permission.ROLES_STAFF) && !a.hasPermission(Permission.ROLES_STAFF)) return 1;
        else return a.getName().compareTo(b.getName());
    }


}
