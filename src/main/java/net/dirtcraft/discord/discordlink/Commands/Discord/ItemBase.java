package net.dirtcraft.discord.discordlink.Commands.Discord;

import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Commands.Discord.Item.ItemList;
import net.dirtcraft.discord.discordlink.Commands.Discord.Item.ItemRemove;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommand;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandTree;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Storage.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;

public class ItemBase extends DiscordCommandTree {
    DiscordCommand list = DiscordCommand.builder()
            .setDescription("Lists a players items via DM.")
            .setCommandUsage("<Username>")
            .setRequiredRoles(Roles.MOD)
            .setCommandExecutor(new ItemList())
            .build();

    public ItemBase(){
        DiscordCommand remove = DiscordCommand.builder()
                .setDescription("Removes a specific slot from a player.")
                .setCommandUsage("<Username> <ID>")
                .setRequiredRoles(Roles.MOD)
                .setCommandExecutor(new ItemRemove())
                .build();

        register(list, "list");
        register(remove, "remove");
    }

    @Override
    public void defaultResponse(MessageSource member, String command, java.util.List<String> args) throws DiscordCommandException {
        if (!args.isEmpty() && !defaults.contains(args.get(0))) {
            list.process(member, command, args);
            return;
        }
        EmbedBuilder embed = Utility.embedBuilder();
        String pre = PluginConfiguration.Main.discordCommand;
        getCommandMap().forEach((alias, cmd)->{
            if (!cmd.hasPermission(member)) return;
            String header = pre + command + " " + alias + " " + cmd.getUsage();
            embed.addField(header, cmd.getDescription(), false);
            embed.setFooter("Requested By: " + member.getUser().getAsTag(), member.getUser().getAvatarUrl());
        });
        member.sendCommandResponse(embed.build(), 30);
    }
}
