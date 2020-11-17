package net.dirtcraft.discord.discordlink.Storage;

import net.dirtcraft.plugin.dirtdatabaselib.DirtDatabaseLib;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class Database {

    public boolean isVerified(UUID uuid) {
        boolean result;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE uuid = ?")) {

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            result = rs.next();

            rs.close();

        } catch (SQLException exception) {
            exception.printStackTrace();
            result = false;
        }

        return result;
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

    public boolean validCode(String code) {
        boolean result;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM verification WHERE code = ?")) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return false;
            } else {
                result = rs.getString("uuid") == null;
                rs.close();
            }

            return result;

        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
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

    @Nullable
    public String getDiscordUser(UUID uuid) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT discordid FROM verification WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return null;
            } else {
                String discordID = rs.getString("discordid");
                rs.close();
                return discordID;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

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

    @Nullable
    public String getUUIDfromDiscordID(String discordID) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM verification WHERE discordid = ?")) {

            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                rs.close();
                return null;
            } else {
                String uuid = rs.getString("uuid");
                rs.close();
                return uuid;
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
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

    private Connection getConnection() {
        return DirtDatabaseLib.getConnection("playerdata", null);
    }

    public static class VerificationData {
        final UUID uuid;
        final Long discordId;
        final String code;
        VerificationData(String uuid, String discord, String code){
            this.discordId = discord == null? null : Long.parseLong(discord);
            this.uuid = uuid == null? null : UUID.fromString(uuid);
            this.code = code;
        }

        public Optional<UUID> getUUID(){
            return Optional.ofNullable(uuid);
        }

        public Optional<Long> getDiscordId(){
            return Optional.ofNullable(discordId);
        }

        public Optional<String> getCode(){
            return Optional.ofNullable(code);
        }
    }
}
