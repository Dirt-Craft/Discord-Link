package net.dirtcraft.discord.discordlink.Storage.tables;

import net.dirtcraft.discord.discordlink.API.GuildMember;
import net.dirtcraft.discord.discordlink.API.Roles;
import net.dirtcraft.discord.discordlink.Utility.Compatability.Platform.PlatformUser;
import net.dirtcraft.discord.discordlink.Utility.Utility;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class Mutes extends Votes {
    protected abstract Connection getConnection();
    private final UUID DEFAULT = new UUID(0,0);

    public void registerMute(UUID staff, GuildMember subject, Timestamp expires, String reason){
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO discordmutedata " +
                     "(submitterMinecraft, subjectDiscord, subjectMinecraft, expires, reason) " +
                     "VALUES (?, ?, ?, ?, ?)")) {
            final String subjectUuid = subject.getPlayerData()
                    .map(PlatformUser::getUUID)
                    .map(UUID::toString)
                    .orElse(null);
            ps.setString(1, staff == null? DEFAULT.toString(): staff.toString());
            ps.setLong(2, subject.getIdLong());
            ps.setString(3, subjectUuid);
            ps.setTimestamp(4, expires);
            ps.setString(5, reason);
            ps.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void registerMute(long staff, GuildMember subject, Timestamp expires, String reason){
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO discordmutedata " +
                     "(submitterDiscord, subjectDiscord, subjectMinecraft, expires, reason) " +
                     "VALUES (?, ?, ?, ?, ?)")) {
            final String subjectUuid = subject.getPlayerData()
                    .map(PlatformUser::getUUID)
                    .map(UUID::toString)
                    .orElse(null);
            ps.setLong(1, staff);
            ps.setLong(2, subject.getIdLong());
            ps.setString(3, subjectUuid);
            ps.setTimestamp(4, expires);
            ps.setString(5, reason);
            ps.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deactivateMute(UUID staff, long subject){
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE discordmutedata " +
                     "SET active = ?, removedByMinecraft = ?, removed = ? " +
                     "WHERE subjectDiscord = ? AND active = ?")) {
            ps.setBoolean(1, false);
            ps.setString(2, staff == null? DEFAULT.toString(): staff.toString());
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setLong(4, subject);
            ps.setBoolean(5, true);
            ps.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void deactivateMute(long staff, long subject){
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE discordmutedata " +
                     "SET active = ?, removedByDiscord = ?, removed = ? " +
                     "WHERE subjectDiscord = ? AND active = ?")) {
            ps.setBoolean(1, false);
            ps.setLong(2, staff);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.setLong(4, subject);
            ps.setBoolean(5, true);
            ps.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Optional<MuteData> hasActiveMute(long discordId){
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM discordmutedata WHERE subjectDiscord = ? AND active = ?")) {
            ps.setLong(1, discordId);
            ps.setBoolean(2, true);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    long submitterDiscord = rs.getLong("submitterDiscord");
                    String submitterMinecraft = rs.getString("submitterMinecraft");
                    long subjectDiscord = rs.getLong("subjectDiscord");
                    String subjectMinecraft = rs.getString("subjectMinecraft");
                    long removedByDiscord = rs.getLong("removedByDiscord");
                    String removedByMinecraft = rs.getString("removedByMinecraft");
                    Timestamp submitted = rs.getTimestamp("submitted");
                    Timestamp removed = rs.getTimestamp("removed");
                    Timestamp expires = rs.getTimestamp("expires");
                    String reason = rs.getString("reason");
                    boolean active = rs.getBoolean("active");
                    MuteData data = new MuteData(id,
                            submitterDiscord, submitterMinecraft,
                            subjectDiscord, subjectMinecraft,
                            removedByDiscord, removedByMinecraft,
                            submitted, removed, expires,
                            reason, active);
                    return Optional.of(data);
                }
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    public void deactivateExpiredMutes(){
        getActiveMutes().stream()
                .filter(MuteData::expired)
                .peek(e->Utility.removeRoleIfPresent(e.subjectDiscord, Roles.MUTED))
                .forEach(e-> setMuteExpired(e.id));
    }

    public List<MuteData> getActiveMutes(){
        List<MuteData> entries = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM discordmutedata WHERE active = ?")) {
            ps.setBoolean(1, true);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    long submitterDiscord = rs.getLong("submitterDiscord");
                    String submitterMinecraft = rs.getString("submitterMinecraft");
                    long subjectDiscord = rs.getLong("subjectDiscord");
                    String subjectMinecraft = rs.getString("subjectMinecraft");
                    long removedByDiscord = rs.getLong("removedByDiscord");
                    String removedByMinecraft = rs.getString("removedByMinecraft");
                    Timestamp submitted = rs.getTimestamp("submitted");
                    Timestamp removed = rs.getTimestamp("removed");
                    Timestamp expires = rs.getTimestamp("expires");
                    String reason = rs.getString("reason");
                    boolean active = rs.getBoolean("active");
                    MuteData data = new MuteData(id,
                            submitterDiscord, submitterMinecraft,
                            subjectDiscord, subjectMinecraft,
                            removedByDiscord, removedByMinecraft,
                            submitted, removed, expires,
                            reason, active);
                    entries.add(data);
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return entries;
    }

    public void setMuteExpired(long id){
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE discordmutedata SET active = ? WHERE id = ?")) {
            ps.setBoolean(1, false);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void buildMuteTable(){
        String statment = "CREATE TABLE IF NOT EXISTS `discordmutedata` (\n" +
                "\t`id` BIGINT(20) NOT NULL AUTO_INCREMENT,\n" +
                "\t`submitterDiscord` BIGINT(20) NULL DEFAULT NULL,\n" +
                "\t`submitterMinecraft` CHAR(36) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',\n" +
                "\t`subjectDiscord` BIGINT(20) NOT NULL,\n" +
                "\t`subjectMinecraft` CHAR(36) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',\n" +
                "\t`removedByDiscord` BIGINT(20) NULL DEFAULT NULL,\n" +
                "\t`removedByMinecraft` CHAR(36) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',\n" +
                "\t`submitted` TIMESTAMP NOT NULL DEFAULT current_timestamp(),\n" +
                "\t`removed` TIMESTAMP NULL DEFAULT NULL,\n" +
                "\t`expires` TIMESTAMP NULL DEFAULT NULL,\n" +
                "\t`reason` VARCHAR(2000) NOT NULL COLLATE 'latin1_swedish_ci',\n" +
                "\t`active` BIT(1) NOT NULL DEFAULT b'1',\n" +
                "\tINDEX `id` (`id`) USING BTREE\n" +
                ");";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(statment)) {
            ps.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

    }

    @SuppressWarnings("FieldCanBeLocal")
    public static class MuteData {
        private final long id;
        private final long submitterDiscord;
        private final UUID submitterMinecraft;
        private final long subjectDiscord;
        private final UUID subjectMinecraft;
        private final long removedByDiscord;
        private final UUID removedByMinecraft;
        private final Timestamp submitted;
        private final Timestamp removed;
        private final Timestamp expires;
        private final String reason;
        private final boolean active;

        MuteData(long id, long submitterDiscord, String submitterMinecraft, long subjectDiscord, String subjectMinecraft, long removedByDiscord, String removedByMinecraft, Timestamp submitted, Timestamp removed, Timestamp expires, String reason, boolean active){
            this.id = id;
            this.submitterDiscord = submitterDiscord;
            this.submitterMinecraft = submitterMinecraft == null? null : UUID.fromString(submitterMinecraft);
            this.subjectDiscord = subjectDiscord;
            this.subjectMinecraft = subjectMinecraft == null? null : UUID.fromString(subjectMinecraft);
            this.removedByDiscord = removedByDiscord;
            this.removedByMinecraft = removedByMinecraft == null? null : UUID.fromString(removedByMinecraft);
            this.submitted = submitted;
            this.removed = removed;
            this.expires = expires;
            this.reason = reason;
            this.active = active;
        }

        public long getId(){
            return id;
        }

        public boolean isActive(){
            return active;
        }

        public boolean expired(){
            if (expires == null) return false;
            Timestamp now = Timestamp.from(Instant.now());
            return now.after(expires);
        }

        public String getReason(){
            return reason;
        }

        public long getTarget(){
            return subjectDiscord;
        }

        public Optional<Long> getSubmitter(){
            return Optional.ofNullable(submitterDiscord > 0? submitterDiscord : null);
        }

        public Optional<Long> getRemovedBy(){
            return Optional.ofNullable(removedByDiscord > 0? removedByDiscord : null);
        }

        public Optional<UUID> getTargetUUID(){
            return Optional.ofNullable(subjectMinecraft);
        }

        public Optional<UUID> getSubmitterUUID(){
            return Optional.ofNullable(submitterMinecraft);
        }

        public Optional<UUID> getRemovedUUID(){
            return Optional.ofNullable(removedByMinecraft);
        }

        public Optional<Timestamp> getRemoved(){
            return Optional.ofNullable(removed);
        }

        public Optional<Timestamp> getExpires(){
            return Optional.ofNullable(expires);
        }

        public Timestamp getSubmitted(){
            return submitted;
        }
    }
}