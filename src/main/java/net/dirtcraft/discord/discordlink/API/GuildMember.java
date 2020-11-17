package net.dirtcraft.discord.discordlink.API;

import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformPlayer;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

public class GuildMember extends WrappedMember {
    private PlatformUser user;
    private boolean retrievedPlayer;
    private List<Roles> roles;
    private Roles highestRank;

    public static Optional<GuildMember> fromPlayerId(UUID player){
        String memberID =  DiscordLink.getInstance().getStorage().getDiscordUser(player);
        if (memberID == null) return Optional.empty();

        final Member member = Utility.getMemberById(memberID).orElse(null);
        if (member == null) return Optional.empty();

        final GuildMember profile = new GuildMember(member);
        profile.user = PlatformUtils.getPlayerOffline(player).orElse(null);
        profile.retrievedPlayer = true;
        return Optional.of(profile);
    }

    public GuildMember(Member member){
        super(member);
        Collection<Role> discordRoles = member.getRoles();
        roles = new ArrayList<>();
        highestRank = Arrays.stream(Roles.values())
                .filter(e->discordRoles.contains(e.getRole()))
                .map(Roles::ordinal)
                .reduce(Integer::min)
                .map(integer -> Roles.values()[integer])
                .orElse(Roles.NONE);

        Arrays.stream(Roles.values())
                .filter(e->(e.isStaff() && highestRank.ordinal() <= e.ordinal()) || (discordRoles.contains(e.getRole())))
                .forEach(roles::add);
    }

    public Optional<PlatformPlayer> getPlayer(){
        if (!retrievedPlayer) return getPlayerData().flatMap(PlatformUtils::getPlayer);
        else return Optional.ofNullable(user).flatMap(PlatformUtils::getPlayer);
    }

    public Optional<PlatformUser> getPlayerData(){
        if (!retrievedPlayer) {
            final String playerId = DiscordLink.getInstance().getStorage().getUUIDfromDiscordID(member.getUser().getId());
            final Optional<PlatformUser> optData = Optional.ofNullable(playerId)
                    .map(UUID::fromString)
                    .flatMap(PlatformUtils::getPlayerOffline);
            retrievedPlayer = true;
            return optData;
        } else return Optional.ofNullable(user);
    }

    public void sendMessage(MessageEmbed embed) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(embed).queue());
    }

    public void sendMessage(String message) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(message).queue());
    }

    public boolean isStaff(){
        return roles.contains(Roles.STAFF);
    }

    public boolean isDonor(){
        return roles.contains(Roles.DONOR);
    }

    public boolean isBoosting(){
        return roles.contains(Roles.NITRO);
    }

    public boolean isVerified(){
        return roles.contains(Roles.VERIFIED);
    }

    public boolean hasRole(Roles role){
        return roles.contains(role);
    }

    public String getChevron(){
        return highestRank.getChevron();
    }

    public String getNameStyle(){
        return highestRank.getStyle();
    }

    @NonNull public Roles getHighestRank(){
        return highestRank;
    }
}
