package net.dirtcraft.discord.discordlink.Storage;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.DiscordLink;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUtils;
import net.dirtcraft.discord.discordlink.Utility.Utility;
import net.dirtcraft.plugin.dirtdatabaselib.DirtDatabaseLib;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Database {

    @Nullable
    public String getLastKnownUsername(String uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT Username FROM votedata WHERE UUID = ?")) {

            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return null;
            } else {
                String username = rs.getString("Username");
                rs.close();
                return username;
            }


        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

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

    public Optional<VerificationData> getPendingData(String code) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE code = ?")) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String uuid = rs.getString("uuid");
                String discordId = rs.getString("discordid");
                return Optional.of(new VerificationData(uuid, discordId, code));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<VerificationData> getVerificationData(String discordID) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordID);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String code = rs.getString("code");
                String uuid = rs.getString("uuid");
                String discordId = rs.getString("discordid");
                return Optional.of(new VerificationData(uuid, discordId, code));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<VerificationData> getVerificationData(UUID id) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE uuid = ?")) {

            ps.setString(1, id.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String code = rs.getString("code");
                String uuid = rs.getString("uuid");
                String discordId = rs.getString("discordid");
                return Optional.of(new VerificationData(uuid, discordId, code));
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    private Connection getConnection() {
        return DirtDatabaseLib.getConnection("playerdata", null);
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
            return getDiscordId()
                    .map(DiscordLink.getJDA()::retrieveUserById)
                    .map(RestAction::submit)
                    .map(CompletableFuture::join);
        }

        public Optional<PlatformUser> getMinecraftUser() {
            return getUUID().flatMap(PlatformUtils::getPlayerOffline);
        }

        public Optional<Member> getMember() {
            return getDiscordId().flatMap(Utility::getMemberById);
        }

        public Optional<GuildMember> getGuildMember() {
            return getMember().map(GuildMember::new);
        }

        public Optional<String> getName() {
            Optional<String> name = getMinecraftUser().flatMap(PlatformUser::getName);
            if (name.isPresent()) return name;
            else return getUUID()
                    .map(UUID::toString)
                    .map(Database.this::getLastKnownUsername);
        }

        public void deleteRecord() {
            if (getUUID().isPresent()) {
                getUUID().ifPresent(Database.this::deleteRecord);
            } else getDiscordId()
                    .map(String::valueOf)
                    .ifPresent(Database.this::deleteRecord);
        }
    }
}
