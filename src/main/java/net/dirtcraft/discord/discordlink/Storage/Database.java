package net.dirtcraft.discord.discordlink.Storage;

import net.dirtcraft.discord.discordlink.Storage.tables.Verification;
import net.dirtcraft.plugin.dirtdatabaselib.DirtDatabaseLib;

import java.sql.Connection;

public class Database extends Verification {

    protected Connection getConnection() {
        return DirtDatabaseLib.getConnection("playerdata", null);
    }

}
