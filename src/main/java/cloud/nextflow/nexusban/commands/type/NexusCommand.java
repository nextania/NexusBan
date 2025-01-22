package cloud.nextflow.nexusban.commands.type;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NexusCommand implements TabExecutor {
    private final String name;
    private String description;
    private String usage;
    private String permission;
    protected final MessageManager messageManager;
    protected final ConfigManager configManager;
    protected final NexusBan nexusBan;

    public NexusCommand(MessageManager messageManager, ConfigManager configManager, String name) {
        this.name = name;
        this.permission = "";
        this.messageManager = messageManager;
        this.configManager = configManager;
        this.nexusBan = messageManager.getNexusBan();
    }

    public NexusCommand(MessageManager messageManager, ConfigManager configManager, String name, String description, String usage) {
        this(messageManager, configManager, name);
        this.description = description;
        this.usage = usage;
    }

    public NexusCommand(MessageManager messageManager, ConfigManager configManager, String name, String description, String usage, String permission) {
        this(messageManager, configManager, name, description, usage);
        this.permission = permission;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!validateUsage(commandSender, command, label, args)) {
            Map<String, String> params = new HashMap<>();
            params.put("usage", getUsage());
            commandSender.sendMessage(messageManager.loadMessage(null, "chat.number-args", params));
            return false;
        }
        if (!permission.isBlank()) {
            if (!commandSender.hasPermission(permission)) {
                commandSender.sendMessage(messageManager.loadMessagePrefix(null, "chat.no-permission"));
                return false;
            }
        }

        return onRun(commandSender, command, label, args);
    }

    protected boolean validatePlayerExists(String playerName, Player punisher, Player punished) {
        if (punished == null) {
            Map<String, String> params = new HashMap<>();
            params.put("player", playerName);
            punisher.sendMessage(messageManager.loadMessagePrefix(null, "chat.player-not-found", params));
            return false;
        }
        return true;
    }

    public abstract boolean validateUsage(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

    public abstract boolean onRun(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

    public abstract @Nullable List<String> onTab(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return onTab(commandSender, command, label, args);
    }

    public NexusBan getNexusBan() {
        return nexusBan;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public String getPermission() {
        return permission;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
