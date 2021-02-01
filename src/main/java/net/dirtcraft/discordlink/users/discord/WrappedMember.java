package net.dirtcraft.discordlink.users.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class WrappedMember implements Member {
    protected final Member member;

    public WrappedMember(Member member){
        this.member = member;
    }

    @Nonnull
    @Override
    public User getUser() {
        return member.getUser();
    }

    @Nonnull
    @Override
    public Guild getGuild() {
        return member.getGuild();
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getPermissions() {
        return member.getPermissions();
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getPermissions(@Nonnull GuildChannel channel) {
        return member.getPermissions(channel);
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getPermissionsExplicit() {
        return member.getPermissionsExplicit();
    }

    @Nonnull
    @Override
    public EnumSet<Permission> getPermissionsExplicit(@Nonnull GuildChannel channel) {
        return member.getPermissionsExplicit(channel);
    }

    @Override
    public boolean hasPermission(@Nonnull Permission... permissions) {
        return member.hasPermission(permissions);
    }

    @Override
    public boolean hasPermission(@Nonnull Collection<Permission> permissions) {
        return member.hasPermission(permissions);
    }

    @Override
    public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Permission... permissions) {
        return member.hasPermission(channel, permissions);
    }

    @Override
    public boolean hasPermission(@Nonnull GuildChannel channel, @Nonnull Collection<Permission> permissions) {
        return member.hasPermission(channel, permissions);
    }

    @Override
    public JDA getJDA() {
        return member.getJDA();
    }

    @Nonnull
    @Override
    public OffsetDateTime getTimeJoined() {
        return member.getTimeJoined();
    }

    @Override
    public boolean hasTimeJoined() {
        return member.hasTimeJoined();
    }

    @Nullable
    @Override
    public OffsetDateTime getTimeBoosted() {
        return member.getTimeBoosted();
    }

    @Override
    public GuildVoiceState getVoiceState() {
        return member.getVoiceState();
    }

    @Nonnull
    @Override
    public List<Activity> getActivities() {
        return member.getActivities();
    }

    @Nonnull
    @Override
    public OnlineStatus getOnlineStatus() {
        return member.getOnlineStatus();
    }

    @Nonnull
    @Override
    public OnlineStatus getOnlineStatus(@Nonnull ClientType type) {
        return member.getOnlineStatus(type);
    }

    @Nonnull
    @Override
    public EnumSet<ClientType> getActiveClients() {
        return member.getActiveClients();
    }

    @Override
    public String getNickname() {
        return member.getNickname();
    }

    @Nonnull
    @Override
    public String getEffectiveName() {
        return member.getEffectiveName();
    }

    @Nonnull
    @Override
    public List<Role> getRoles() {
        return member.getRoles();
    }

    @Override
    public Color getColor() {
        return member.getColor();
    }

    @Override
    public int getColorRaw() {
        return member.getColorRaw();
    }

    @Override
    public boolean canInteract(@Nonnull Member member) {
        return this.member.canInteract(member);
    }

    @Override
    public boolean canInteract(@Nonnull Role role) {
        return member.canInteract(role);
    }

    @Override
    public boolean canInteract(@Nonnull Emote emote) {
        return member.canInteract(emote);
    }

    @Nullable
    @Override
    public TextChannel getDefaultChannel() {
        return member.getDefaultChannel();
    }

    @Nonnull
    @Override
    public String getAsMention() {
        return member.getAsMention();
    }

    @Override
    public boolean isOwner(){
        return member.isOwner();
    }

    @Override
    public boolean isFake() {
        return member.isFake();
    }

    @Override
    public long getIdLong() {
        return member.getIdLong();
    }
}
