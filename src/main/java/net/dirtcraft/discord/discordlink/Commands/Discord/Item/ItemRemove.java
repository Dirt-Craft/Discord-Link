package net.dirtcraft.discord.discordlink.Commands.Discord.Item;

import javafx.util.Pair;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemRemove implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        User player = parseMinecraft(args)
                .map(PlatformUser::getUser)
                .orElseThrow(()->new DiscordCommandException("Player not online / Specified!"));
        int index = Optional.ofNullable(args.isEmpty()? null: args.get(0))
                .filter(s->s.matches("\\d+"))
                .map(Integer::parseInt)
                .orElseThrow(()->new DiscordCommandException("You must specify a slot to clear!"));
        Inventory slot = player.getInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(index)));
        source.sendMessage("``" + slot.peek().map(s->s.getType().getTranslation().get()).orElse("Empty") + " has been removed.``");
        slot.set(ItemStack.empty());
    }
}
