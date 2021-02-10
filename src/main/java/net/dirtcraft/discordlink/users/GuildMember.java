package net.dirtcraft.discordlink.users;

import net.dirtcraft.discordlink.users.permission.PermissionProvider;
import net.dirtcraft.discordlink.users.permission.subject.PermissionResolver;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.users.roles.RoleManager;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.users.discord.WrappedMember;
import net.dirtcraft.discordlink.users.platform.PlatformPlayerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;

import java.util.*;

public class GuildMember extends WrappedMember implements DiscordMember {
    PlatformUser user;
    PermissionResolver permissions;
    boolean retrievedPlayer;
    boolean retrievedPermissions;
    private final RoleManager roleManager;
    private final Database storage;
    private Set<DiscordRole> roles;
    private DiscordRole highestRank;

    public GuildMember(Database storage, RoleManager roleManager, Member member){
        super(member);
        this.roleManager = roleManager;
        this.storage = storage;
        Collection<Role> discordRoles = member.getRoles();
        roles = new HashSet<>();
        highestRank = roleManager.getRoles().stream()
                .filter(e->discordRoles.contains(e.getRole()))
                .map(DiscordRole::ordinal)
                .reduce(Integer::min)
                .map(roleManager::getRole)
                .orElse(DiscordRoles.NONE);

        roleManager.getRoles().stream()
                .filter(e->(e.isStaff() && highestRank.getStaffLevel() >= e.getStaffLevel()) || (discordRoles.contains(e.getRole())))
                .forEach(roles::add);
    }

    public boolean hasInGamePermission(String permission){
        if (hasRole(DiscordRoles.DIRTY)) return true;
        if (!retrievedPlayer) getPlayerData();
        if (user == null) return false;
        if (!retrievedPermissions) permissions = PermissionProvider.INSTANCE
                .getPermission(user.getUUID())
                .orElse(null);
        if (permissions == null) return false;
        boolean perm =  permissions.hasPermission(permission);
        return perm;
    }

    @Override
    public Optional<PlatformPlayer> getPlayer(){
        if (!retrievedPlayer) return getPlayerData().flatMap(PlatformProvider::getPlayer);
        else return Optional.ofNullable(user).flatMap(PlatformProvider::getPlayer);
    }

    @Override
    public Optional<PlatformUser> getPlayerData(){
        if (!retrievedPlayer) {
            final Optional<PlatformUser> optData = storage.getVerificationData(getId())
                    .flatMap(Verification.VerificationData::getUUID)
                    .flatMap(PlatformProvider::getPlayerOffline);
            optData.ifPresent(u->this.user = u);
            retrievedPlayer = true;
            return optData;
        } else return Optional.ofNullable(user);
    }

    @Override
    public void sendPrivateMessage(MessageEmbed embed) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(embed).queue());
    }

    @Override
    public void sendPrivateMessage(String message) {
        member.getUser().openPrivateChannel().queue(dm-> dm.sendMessage(message).queue());
    }

    @Override
    public boolean isStaff(){
        return roles.contains(DiscordRoles.STAFF);
    }

    @Override
    public boolean isMuted(){
        return roles.contains(DiscordRoles.MUTED);
    }

    @Override
    public boolean isDonor(){
        return roles.contains(DiscordRoles.DONOR);
    }

    @Override
    public boolean isBoosting(){
        return roles.contains(DiscordRoles.NITRO);
    }

    @Override
    public boolean isVerified(){
        return roles.contains(DiscordRoles.VERIFIED);
    }

    @Override
    public boolean hasRole(DiscordRole role){
        return roles.contains(role);
    }

    @Override
    public String getChevron(){
        return highestRank.getChevron();
    }

    @Override
    public String getNameStyle(){
        return highestRank.getStyle();
    }

    @Override
    @NonNull public DiscordRole getHighestRank(){
        return highestRank;
    }

    @Override
    public void setRoleIfAbsent(DiscordRole role){
        try {
            Guild guild = getGuild();
            Role discordRole = role.getRole();
            if (discordRole == null || hasRole(role)) return;
            guild.addRoleToMember(member, discordRole).submit();
        } catch (Exception ignored){}
    }

    @Override
    public void removeRoleIfPresent(DiscordRole role){
        try {
            Guild guild = getGuild();
            Role discordRole = role.getRole();
            if (discordRole == null || !hasRole(role)) return;
            guild.removeRoleFromMember(member, discordRole).submit();
        } catch (Exception ignored){}
    }

    @Override
    public void tryChangeNickname(String name){
        try {
            if (name == null) return;
            getGuild().modifyNickname(this, name).submit();
        } catch (Exception ignored){ }
    }

    @Override
    public void reloadRoles(){
        Collection<Role> discordRoles = member.getRoles();
        roles = new HashSet<>();
        highestRank = roleManager.getRoles().stream()
                .filter(e->discordRoles.contains(e.getRole()))
                .map(DiscordRole::ordinal)
                .reduce(Integer::min)
                .map(roleManager::getRole)
                .orElse(DiscordRoles.NONE);

        roleManager.getRoles().stream()
                .filter(e->(e.isStaff() && highestRank.getStaffLevel() <= e.getStaffLevel()) || (discordRoles.contains(e.getRole())))
                .forEach(roles::add);
    }

    Member getWrappedMember(){
        return member;
    }

}
