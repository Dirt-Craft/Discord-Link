package net.dirtcraft.discord.discordlink.Commands.Sources;

import java.util.function.Consumer;

public interface ScheduledSender {
    void sendDiscordResponse(String message);

    default int getCharLimit(){
        return 1024;
    }

    default boolean sanitise(){
        return true;
    }

    static ScheduledSender getSender(Consumer<String> output, int limit, boolean sanitise){
        return new ScheduledSender() {
            @Override
            public void sendDiscordResponse(String message) {
                output.accept(message);
            }

            @Override
            public int getCharLimit(){
                return limit;
            }

            @Override
            public boolean sanitise(){
                return sanitise;
            }
        };
    }
}
