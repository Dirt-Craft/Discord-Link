package net.dirtcraft.discordlink.commands.discord;

import net.dirtcraft.spongediscordlib.users.MessageSource;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.users.MessageSourceImpl;
import net.dirtcraft.discordlink.commands.discord.item.ItemList;
import net.dirtcraft.discordlink.commands.discord.item.ItemRemove;
import net.dirtcraft.discordlink.commands.DiscordCommandImpl;
import net.dirtcraft.discordlink.commands.DiscordCommandTree;
import net.dirtcraft.spongediscordlib.exceptions.DiscordCommandException;
import net.dirtcraft.discordlink.storage.PluginConfiguration;
import net.dirtcraft.discordlink.utility.Utility;
import net.dv8tion.jda.api.EmbedBuilder;

public class ItemBase extends DiscordCommandTree {
    DiscordCommandImpl list = DiscordCommandImpl.builder()
            .setDescription("Lists a players items via DM.")
            .setCommandUsage("<Username>")
            .setRequiredRoles(DiscordRoles.MOD)
            .setCommandExecutor(new ItemList())
            .build();

    public ItemBase(){
        DiscordCommandImpl remove = DiscordCommandImpl.builder()
                .setDescription("Removes a specific slot from a player.")
                .setCommandUsage("<Username> <ID>")
                .setRequiredRoles(DiscordRoles.MOD)
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
