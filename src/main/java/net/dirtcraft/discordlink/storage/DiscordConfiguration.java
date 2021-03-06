package net.dirtcraft.discordlink.storage;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class DiscordConfiguration {
    @Setting(value = "Discord")
    private Discord discord = new Discord();


    @ConfigSerializable
    public static class Discord {

        @Setting(value = "Gamechat Channel ID")
        public static String GAMECHAT_CHANNEL_ID = "";

        @Setting(value = "Server Name")
        public static String SERVER_NAME = "N/A";

        @Setting(value = "Discord Bot Token")
        public static String TOKEN = "";
    }

}
