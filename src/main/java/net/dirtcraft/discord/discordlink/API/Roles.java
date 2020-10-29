package net.dirtcraft.discord.discordlink.API;

import net.dv8tion.jda.api.entities.Role;

import static net.dirtcraft.discord.discordlink.Configuration.PluginConfiguration.Roles.*;

public enum Roles {
    OWNER   (ownerRoleID,   true, 'c',"Owner"           ),
    DIRTY   (dirtyRoleID,   true, 'e',"Manager"         ),
    ADMIN   (adminRoleID,   true, '4',"Admin"           ),
    MOD     (modRoleID  ,   true, 'b',"Moderator"       ),
    STAFF   (staffRoleID,   true, '5',"Helper"          ),
    NITRO   (nitroRoleID,   false,'a',"Nitro Booster"   ),
    DONOR   (donatorRoleID, false,'6',"Donor"           ),
    VERIFIED(verifiedRoleID,false,'7',"Verified"        ),
    NONE    (null,      false,'7',"None"            );

    private final long id;
    private final String name;
    private final char color;
    private final boolean isStaff;

    Roles(String id, boolean isStaff, char color, String name){
        this.id = id == null ? -1 : Long.parseLong(id);
        this.name = name;
        this.color = color;
        this.isStaff = isStaff;
    }

    public Role getRole(){
        return GameChat.getGuild().getRoleById(id);
    }

    public String getName(){
        return name;
    }

    public boolean isStaff(){
        return isStaff;
    }

    public String getStyle(){
        return this.isStaff ? "§" + color + "§l" : "§7";
    }

    public String getChevron(){
        return (color == '7' ? "§9" : "§" + color) + "§l»";
    }

}
