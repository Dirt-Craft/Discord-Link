package net.dirtcraft.discordlink.commands.sponge;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.utility.Utility;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

public class UnVerify implements CommandExecutor {

    private final Database storage;

    public UnVerify(Database storage) {
        this.storage = storage;
    }

    @Override
    public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
        if (!(source instanceof Player)) throw new CommandException(Utility.format("&cOnly a player can unverify their Discord account!"));
        Player player = (Player) source;
        Task.builder()
                .async()
                .execute(()->execute(player))
                .submit(DiscordLink.get());
        return CommandResult.success();
    }

    private void execute(Player player){
        Verification.VerificationData data = storage.getVerificationData(player.getUniqueId()).orElse(null);
        if (data == null){
            player.sendMessage(Utility.format("&cYour account is not verified!"));
        } else {
            data.deleteRecord();
            String response = data.getDiscordUser()
                    .map(user->"&7The account &6" + user.getName() + "&8#&7" + user.getDiscriminator() + " has been &cunverified")
                    .orElse("&7Your account has been &cunverified");
            player.sendMessage(Utility.format(response));
            data.getGuildMember().ifPresent(member-> member.removeRoleIfPresent(DiscordRoles.VERIFIED));
        }
    }
}
