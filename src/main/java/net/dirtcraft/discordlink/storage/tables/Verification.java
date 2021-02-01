package net.dirtcraft.discordlink.storage.tables;

import net.dirtcraft.discordlink.users.GuildMember;
import net.dirtcraft.discordlink.DiscordLink;
import net.dirtcraft.discordlink.users.UserManagerImpl;
import net.dirtcraft.discordlink.users.platform.PlatformUserImpl;
import net.dirtcraft.discordlink.users.platform.PlatformProvider;
import net.dirtcraft.discordlink.utility.Utility;
import net.dirtcraft.spongediscordlib.users.DiscordMember;
import net.dirtcraft.spongediscordlib.users.platform.PlatformUser;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class Verification extends Mutes {

    public void createRecord(String discordID, String code) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO verification (discordid, code) VALUES (?, ?)")) {

            ps.setString(1, discordID);
            ps.setString(2, code);

            ps.execute();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void updateRecord(String code, UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE verification SET uuid = ?, code = NULL WHERE code = ?")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, code);

            ps.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteRecord(UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM verification WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deleteRecord(String discordId) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM verification WHERE discordid = ?")) {
            ps.setString(1, discordId);

            ps.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Optional<Verification.VerificationData> getPendingData(String code) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE code = ?")) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String uuid = rs.getString("uuid");
                String discordId = rs.getString("discordid");
                return Optional.of(new Verification.VerificationData(uuid, discordId, code));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Verification.VerificationData> getVerificationData(String discordID) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordID);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String code = rs.getString("code");
                String uuid = rs.getString("uuid");
                String discordId = rs.getString("discordid");
                return Optional.of(new Verification.VerificationData(uuid, discordId, code));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Verification.VerificationData> getVerificationData(UUID id) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE uuid = ?")) {

            ps.setString(1, id.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String code = rs.getString("code");
                String uuid = rs.getString("uuid");
                String discordId = rs.getString("discordid");
                return Optional.of(new Verification.VerificationData(uuid, discordId, code));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    public class VerificationData {
        final UUID uuid;
        final Long discordId;
        final String code;

        VerificationData(String uuid, String discord, String code) {
            this.discordId = discord == null ? null : Long.parseLong(discord);
            this.uuid = uuid == null ? null : UUID.fromString(uuid);
            this.code = code;
        }

        public Optional<UUID> getUUID() {
            return Optional.ofNullable(uuid);
        }

        public Optional<Long> getDiscordId() {
            return Optional.ofNullable(discordId);
        }

        public Optional<String> getCode() {
            return Optional.ofNullable(code);
        }

        public Optional<User> getDiscordUser() {
            DiscordLink discordLink = DiscordLink.get();
            JDA jda = discordLink.getJda();
            return getDiscordId()
                    .map(jda::retrieveUserById)
                    .map(RestAction::submit)
                    .map(CompletableFuture::join);
        }

        public Optional<PlatformUser> getMinecraftUser() {
            return getUUID().flatMap(PlatformProvider::getPlayerOffline);
        }

        public Optional<Member> getMember() {
            return getDiscordId().flatMap(Utility::getMemberById);
        }

        public Optional<DiscordMember> getGuildMember() {
            DiscordLink discordLink = DiscordLink.get();
            UserManagerImpl userManager = discordLink.getUserManager();
            return getMember().map(userManager::getMember);
        }

        public Optional<String> getName() {
            Optional<String> name = getMinecraftUser().flatMap(PlatformUser::getNameIfPresent);
            if (name.isPresent()) return name;
            else return getUUID()
                    .map(UUID::toString)
                    .map(Verification.this::getLastKnownUsername);
        }

        public void deleteRecord() {
            if (getUUID().isPresent()) {
                getUUID().ifPresent(Verification.this::deleteRecord);
            } else getDiscordId()
                    .map(String::valueOf)
                    .ifPresent(Verification.this::deleteRecord);
        }
    }
}
