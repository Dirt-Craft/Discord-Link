package net.dirtcraft.discordlink.users.discord;

import com.google.common.collect.ImmutableList;
import net.dirtcraft.discordlink.utility.Pair;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRole;
import net.dirtcraft.spongediscordlib.users.roles.DiscordRoles;
import net.dirtcraft.spongediscordlib.users.roles.RoleManager;
import net.dv8tion.jda.api.JDA;

import java.util.*;
import java.util.stream.Stream;

import static net.dirtcraft.discordlink.storage.PluginConfiguration.Roles.*;

public class RoleManagerImpl extends RoleManager {
    private final Map<String, DiscordRole> nameMap = new HashMap<>();
    private final List<DiscordRole> roles = new ArrayList<>();
    private final JDA jda;

    public RoleManagerImpl(JDA jda) {
        this.jda = jda;
        Stream.of(
                new Pair<DiscordRole, DiscordRole.RoleSupplier>(DiscordRoles.OWNER,     () -> ownerRoleID       ),
                new Pair<DiscordRole, DiscordRole.RoleSupplier>(DiscordRoles.DIRTY,     () -> dirtyRoleID       ),
                new Pair<DiscordRole, DiscordRole.RoleSupplier>(DiscordRoles.ADMIN,     () -> adminRoleID       ),
                new Pair<DiscordRole, DiscordRole.RoleSupplier>(DiscordRoles.MOD,       () -> moderatorRoleID   ),
                new Pair<DiscordRole, DiscordRole.RoleSupplier>(DiscordRoles.HELPER,    () -> helperRoleID      ),
                new Pair<DiscordRole, DiscordRole.RoleSupplier>(DiscordRoles.STAFF,     () -> staffRoleID       ),
                new Pair<DiscordRole, DiscordRole.RoleSupplier>(DiscordRoles.NITRO,     () -> nitroRoleID       ),
                new Pair<DiscordRole, DiscordRole.RoleSupplier>(DiscordRoles.DONOR,     () -> donatorRoleID     ),
                new Pair<DiscordRole, DiscordRole.RoleSupplier>(DiscordRoles.VERIFIED,  () -> verifiedRoleID    ),
                new Pair<DiscordRole, DiscordRole.RoleSupplier>(DiscordRoles.MUTED,     () -> mutedRoleID       )
        ).forEach(role->registerRole(role.getKey(), role.getValue()));
    }

    @Override
    public DiscordRole getRole(String name) {
        return nameMap.getOrDefault(name, DiscordRoles.NONE);
    }

    @Override
    public DiscordRole getRole(int ordinal) {
        return ordinal >= 0 && roles.size() < ordinal? DiscordRoles.NONE: roles.get(ordinal);
    }

    @Override
    public int getOrdinal(DiscordRole role) {
        return roles.indexOf(role);
    }

    @Override
    public List<DiscordRole> getRoles() {
        return ImmutableList.copyOf(roles);
    }

    @Override
    public void registerRole(DiscordRole role) {
        if (nameMap.containsKey(role.getName())) return;
        nameMap.put(role.getName(), role);
        roles.add(role);
        setFields(role, jda);
    }

    private void registerRole(DiscordRole role, DiscordRole.RoleSupplier roleSupplier) {
        if (nameMap.containsKey(role.getName())) return;
        nameMap.put(role.getName(), role);
        roles.add(role);
        setFields(role, jda, roleSupplier);
        role.reload();
    }
}
