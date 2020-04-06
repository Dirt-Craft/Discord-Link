package net.dirtcraft.discord.discordlink.Commands.Sponge;

import net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration;
import net.dirtcraft.discord.discordlink.Database.Storage;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility;
import net.dirtcraft.discord.spongediscordlib.SpongeDiscordLib;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.HierarchyException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import javax.annotation.Nullable;
import javax.rmi.CORBA.Util;
import java.net.MalformedURLException;
import java.net.URL;

public class Verify implements CommandExecutor {

    private final Storage storage;

    public Verify(Storage storage) {
        this.storage = storage;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (!(source instanceof Player)) throw new CommandException(Utility.format("&cOnly a player can verify their Discord account!"));
        Player player = (Player) source;

        if (args.<String>getOne("code").isPresent()) {
            String code = args.<String>getOne("code").get();
            Task.builder()
                    .async()
                    .execute(() -> {
                        if (storage.isVerified(player.getUniqueId())) {
                            @Nullable
                            User user = SpongeDiscordLib.getJDA().getUserById(storage.getDiscordUser(player.getUniqueId()));

                            player.sendMessage(Utility.format(user != null ?
                                    "&cYour account is already verified with &6" + user.getName() + "&8#&7" + user.getDiscriminator() + "&c!" :
                                    "&cYour account is already verified!"));
                            return;
                        }
                        if (!storage.validCode(code)) {
                            player.sendMessage(Utility.format("&cThe code &e" + code + "&c is not valid!"));
                            return;
                        }

                        storage.updateRecord(code, player.getUniqueId());

                        String discordID = storage.getDiscordUser(player.getUniqueId());

                        if (discordID == null) {
                            player.sendMessage(Utility.format("&cCould not verify your Discord account, please contact an Administrator!"));
                            return;
                        }

                        @Nullable
                        User user = SpongeDiscordLib.getJDA().getUserById(discordID);

                        if (user == null) {
                            Text.Builder discordJoin = Text.builder();
                            discordJoin.append(Utility.format("&cYour Discord account has been verified, but it is not in the DirtCraft Discord!\n&5&nClick Me&7 to &ajoin &7it"));
                            try {
                                discordJoin.onHover(TextActions.showText(Utility.format("&6Click me to join the DirtCraft Discord")));
                                discordJoin.onClick(TextActions.openUrl(new URL("http://discord.dirtcraft.gg/")));
                            } catch (MalformedURLException exception) {
                                discordJoin.onHover(TextActions.showText(Utility.format("&cMalformed URL, contact Administrator")));
                            }

                            player.sendMessage(discordJoin.build());
                            return;
                        }

                        String discordName = user.getName();
                        String discordTag = user.getDiscriminator();

                        player.sendMessage(Utility.format("&7Successfully verified &6" + player.getName() + "&7 with &6" + discordName + "&8#&7" + discordTag));

                        Guild guild = SpongeDiscordLib.getJDA().getGuildById(PluginConfiguration.Main.discordServerID);
                        Role verifiedRole = guild.getRoleById(PluginConfiguration.Roles.verifiedRoleID);
                        Role staffRole = guild.getRoleById(PluginConfiguration.Roles.staffRoleID);
                        Role donorRole = guild.getRoleById(PluginConfiguration.Roles.donatorRoleID);
                        Member member = guild.getMemberById(discordID);

                        if (!member.getRoles().contains(verifiedRole)) {
                            guild.getController().addSingleRoleToMember(member, verifiedRole).queue();
                        }
                        if (player.hasPermission("discordlink.donator") && !member.getRoles().contains(donorRole)) {
                            guild.getController().addSingleRoleToMember(member, donorRole).queue();
                        }
                        if (!member.getRoles().contains(staffRole)) {
                            try {
                                guild.getController().setNickname(member, player.getName()).queue();
                            } catch (HierarchyException e){
                                player.sendMessage(Utility.format("&cCould not modify discord name as your role exceeds the bots power level."));
                            }
                        }
                    })
                    .submit(DiscordLink.getInstance());
            return CommandResult.success();
        } else {
            Task.builder()
                    .async()
                    .execute(() -> {
                        if (storage.isVerified(player.getUniqueId())) {
                            String discordID = storage.getDiscordUser(player.getUniqueId());
                            @Nullable
                            User user = SpongeDiscordLib.getJDA().getUserById(discordID);
                            player.sendMessage(Utility.format(user != null ?
                                    "&cYour account is already verified with &6" + user.getName() + "&8#&7" + user.getDiscriminator() + "&c!" :
                                    "&cYour account is already verified!"));
                        } else {
                            Text.Builder text = Text.builder()
                                    .append(Utility.format("\n" + "&5&nClick Me&7 to link your &9Discord&7 account and unlock additional features!" + "\n"));

                                    try {
                                        text.onHover(TextActions.showText(Utility.format("&5&nClick Me&7 to verify your Discord account!")));
                                        text.onClick(TextActions.openUrl(new URL("http://verify.dirtcraft.gg/")));
                                    } catch (MalformedURLException exception) {
                                        text.onHover(TextActions.showText(Utility.format("&cMalformed URL, contact Administrator!")));
                                    }
                            PaginationList.builder()
                                    .title(Utility.format("&cDirtCraft &7Verification"))
                                    .padding(Utility.format("&4&m-"))
                                    .contents(text.build())
                                    .build()
                                    .sendTo(player);
                        }
                    })
                    .submit(DiscordLink.getInstance());

            return CommandResult.success();
        }
    }


}
