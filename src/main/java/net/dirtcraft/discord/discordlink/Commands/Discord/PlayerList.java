package net.dirtcraft.discord.discordlink.Commands.Discord;

import io.github.nucleuspowered.nucleus.api.NucleusAPI;
import io.github.nucleuspowered.nucleus.api.service.NucleusAFKService;
import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.api.EmbedBuilder;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerList implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) {
        final Collection<Player> players = Sponge.getServer().getOnlinePlayers();
        final NucleusAFKService afkService = NucleusAPI.getAFKService().orElse(null);
        final ArrayList<String> playerNames = new ArrayList<>();

        for (Player player : players){
            if (player.get(Keys.VANISH).orElse(false)) continue;
            if (afkService == null || !afkService.isAFK(player)) playerNames.add(player.getName());
            else playerNames.add(player.getName() + " " + "—" + " " + "**AFK**");
        }

        playerNames.sort(String::compareToIgnoreCase);

        EmbedBuilder embed = Utility.embedBuilder();
        if (playerNames.size() > 1) {
            embed.addField("__**" + playerNames.size() + "** players online__", String.join("\n", playerNames), false);
        } else if (playerNames.size() == 1) {
            embed.addField("__**" + playerNames.size() + "** player online__", String.join("\n", playerNames), false);
        } else {
            embed.setDescription("There are no players playing **" + SpongeDiscordLib.getServerName() + "**!");
        }
        embed.setFooter("Requested By: " + source.getUser().getAsTag(), source.getUser().getAvatarUrl());

        DiscordLink
                .getJDA()
                .getTextChannelById(SpongeDiscordLib.getGamechatChannelID())
                .sendMessage(embed.build())
                .queue();
    }


}
