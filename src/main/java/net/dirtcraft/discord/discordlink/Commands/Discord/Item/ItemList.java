package net.dirtcraft.discord.discordlink.Commands.Discord.Item;

import javafx.util.Pair;
import net.dirtcraft.discord.discordlink.API.MessageSource;
import net.dirtcraft.discord.discordlink.Commands.DiscordCommandExecutor;
import net.dirtcraft.discord.discordlink.Exceptions.DiscordCommandException;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemList implements DiscordCommandExecutor {
    @Override
    public void execute(MessageSource source, String command, List<String> args) throws DiscordCommandException {
        final Map<Integer, String> slotNames = Stream.of(
                new Pair<>(36, "Boots"),
                new Pair<>(37, "Pants"),
                new Pair<>(38, "Chest"),
                new Pair<>(39, "Helmet"),
                new Pair<>(40, "Offhand")
        ).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        User player = parseMinecraft(args)
                .map(PlatformUser::getUser)
                .orElseThrow(()->new DiscordCommandException("Player not online / Specified!"));
        List<String> slots = new ArrayList<>();
        slots.add(String.format("``%-2s | %-7s | %s``", "Id", "Type", "Contents"));
        player.getInventory().slots().forEach(slot->{
            int index = slot.getInventoryProperty(SlotIndex.class).map(SlotIndex::getValue).orElse(-1);
            String type = slotNames.getOrDefault(index, "Storage");
            String name = slot.peek().map(i->i.getType().getTranslation().get()).orElse("Empty");
            slots.add(String.format("``%02d | %-7s | %s``", index, type, name));
        });
        source.sendMessage(String.join("\n", slots));
    }
}
