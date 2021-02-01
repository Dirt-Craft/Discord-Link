package net.dirtcraft.discordlink.storage;

import net.dirtcraft.discordlink.storage.tables.Verification;
import net.dirtcraft.plugin.dirtdatabaselib.DirtDatabaseLib;

import java.sql.Connection;

public class Database extends Verification {

    protected Connection getConnection() {
        return DirtDatabaseLib.getConnection("playerdata", null);
    }

}
