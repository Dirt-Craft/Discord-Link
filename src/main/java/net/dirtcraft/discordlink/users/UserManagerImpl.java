package net.dirtcraft.discordlink.users;

import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.spongediscordlib.users.UserManager;
import net.dirtcraft.discordlink.channels.ChannelManagerImpl;
import net.dirtcraft.discordlink.channels.DiscordChannelImpl;
import net.dirtcraft.discordlink.storage.Database;
import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.discordlink.users.discord.RoleManagerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.spongediscordlib.users.platform.PlatformPlayer;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserManagerImpl implements UserManager {
    private final ChannelManagerImpl channelManager;
    private final RoleManagerImpl roleManager;
    private final Database storage;

    public UserManagerImpl(ChannelManagerImpl channelManager, RoleManagerImpl roleManager, Database storage){
        this.channelManager = channelManager;
        this.roleManager = roleManager;
        this.storage = storage;
    }

    @Override
    public Optional<PlatformUser> getUser(String s){
        if (s.matches("<?@?!?(\\d+)>?")){
            long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return Optional.ofNullable(channelManager.getGuild().getMemberById(discordId))
                    .map(m->new GuildMember(storage, roleManager, m))
                    .flatMap(GuildMember::getPlayerData);
        } else {
            return PlatformProvider.getPlayerOffline(s);
        }
    }

    @Override
    public Optional<PlatformUser> getUser(UUID uuid){
        return PlatformProvider.getPlayerOffline(uuid);
    }

    @Override
    public Optional<DiscordMember> getMember(long id){
        return Optional.ofNullable(DiscordLink.get().getChannelManager().getGuild().getMemberById(id))
                .map(m->new GuildMember(storage, roleManager, m));
    }

    @Override
    public Optional<DiscordMember> getMember(UUID player){
        final Optional<DiscordMember> profile =  storage.getVerificationData(player)
                .flatMap(Verification.VerificationData::getDiscordId)
                .flatMap(Utility::getMemberById)
                .map(member->new GuildMember(storage, roleManager, member));
        profile.ifPresent(rawMember-> {
            GuildMember member = (GuildMember) rawMember;
            member.retrievedPlayer = true;
            member.user = PlatformProvider.getPlayerOffline(player)
                    .orElse(null);
        });
        return profile;
    }

    @Override
    public Optional<DiscordMember> getMember(String s){
        if (s.matches("<?@?!?(\\d+)>?")){
            long discordId = Long.parseLong(s.replaceAll("<?@?!?(\\d+)>?", "$1"));
            return Optional.ofNullable(channelManager.getGuild().getMemberById(discordId))
                    .map(m->new GuildMember(storage, roleManager, m));
        } else {
            return PlatformProvider.getPlayerOffline(s)
                    .map(PlatformUser::getUUID)
                    .flatMap(storage::getVerificationData)
                    .flatMap(Verification.VerificationData::getGuildMember);
        }
    }

    @Override
    public List<PlatformPlayer> getPlayers(){
        return PlatformProvider.getPlayers();
    }

    public MessageSourceImpl getMember(MessageReceivedEvent event){
        boolean isPrivate = event.getMessage().isFromType(ChannelType.PRIVATE);
        long channelId = event.getChannel().getIdLong();
        DiscordChannelImpl channel = channelManager.getChannel(channelId, isPrivate);
        Member member = channelManager.getGuild().retrieveMember(event.getAuthor()).complete();
        return new MessageSourceImpl(storage, member, channel, roleManager, event);
    }

    public GuildMember getMember(Member member){
        return new GuildMember(storage, roleManager, member);
    }
}
