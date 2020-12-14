package net.dirtcraft.discord.discordlink.Storage.tables;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public abstract class Votes {
    protected abstract Connection getConnection();

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

    public void updateUsername(UUID uuid, String username)  {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE votedata SET Username = ?, Last_Vote = ? WHERE UUID = ?")) {

            ps.setString(1, username);
            ps.setTimestamp(2, Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS)));
            ps.setString(3, uuid.toString());

            ps.executeUpdate();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void createVoteRecord(UUID uuid, String username)  {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO votedata (UUID, Username, Last_Vote) VALUES (?, ?, ?)")) {

            ps.setString(1, uuid.toString());
            ps.setString(2, username);
            ps.setTimestamp(3, Timestamp.from(Instant.now().minus(1, ChronoUnit.DAYS)));

            ps.execute();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Optional<VoterData> getPlayerVoteData(UUID uuid){
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM votedata WHERE UUID = ?")) {

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return Optional.empty();

            final int votes = rs.getInt("Votes");
            final Timestamp lastVote = rs.getTimestamp("Last_Vote");
            final String username = rs.getString("Username");

            rs.close();

            return Optional.of(new VoterData(uuid, votes, username, lastVote));

        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    public static class VoterData {
        private final Timestamp lastVote;
        private final String username;

        private VoterData(UUID uuid, int votes, String username, Timestamp lastVote){
            this.lastVote = lastVote;
            this.username = username;
        }

        public String getUsername(){
            if (username == null || username.trim().isEmpty()) return null;
            else return username;
        }

        public boolean hasVotedInPastWeek(){
            return lastVote != null && lastVote.toInstant().isAfter(Instant.now().minus(8, ChronoUnit.DAYS));
        }
    }
}