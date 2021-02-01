package net.dirtcraft.discordlink.commands.sources;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class ConsoleSource implements org.spongepowered.api.command.source.ConsoleSource {

    private CommandSource actualSource;

    public ConsoleSource(){
        actualSource = Sponge.getServer().getConsole();
    }

    @Override
    public void sendMessages(Iterable<Text> messages) {
        for (Text message : messages) sendMessage(message);
    }

    @Override
    public void sendMessages(Text... messages) {
        for (Text message : messages) sendMessage(message);
    }

    @Override
    public String getName() {
        return this.actualSource.getName();
    }

    @Override
    public boolean isSubjectDataPersisted() {
        return this.actualSource.isSubjectDataPersisted();
    }

    @Override
    public SubjectReference asSubjectReference() {
        return this.actualSource.asSubjectReference();
    }

    @Override
    public MessageChannel getMessageChannel() {
        return this.actualSource.getMessageChannel();
    }

    @Override
    public void setMessageChannel(MessageChannel channel) {
        this.actualSource.setMessageChannel(channel);
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return this.actualSource.getCommandSource();
    }

    @Override
    public SubjectCollection getContainingCollection() {
        return this.actualSource.getContainingCollection();
    }

    @Override
    public SubjectData getSubjectData() {
        return this.actualSource.getSubjectData();
    }

    @Override
    public SubjectData getTransientSubjectData() {
        return this.actualSource.getTransientSubjectData();
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        return this.actualSource.hasPermission(contexts, permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.actualSource.hasPermission(permission);
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        return this.actualSource.getPermissionValue(contexts, permission);
    }

    @Override
    public boolean isChildOf(SubjectReference parent) {
        return this.actualSource.isChildOf(parent);
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
        return this.actualSource.isChildOf(contexts, parent);
    }

    @Override
    public List<SubjectReference> getParents() {
        return this.actualSource.getParents();
    }

    @Override
    public List<SubjectReference> getParents(Set<Context> contexts) {
        return this.actualSource.getParents(contexts);
    }

    @Override
    public Optional<String> getOption(Set<Context> contexts, String key) {
        return this.actualSource.getOption(contexts, key);
    }

    @Override
    public String getIdentifier() {
        return this.actualSource.getIdentifier();
    }

    @Override
    public Set<Context> getActiveContexts() {
        return this.actualSource.getActiveContexts();
    }

}