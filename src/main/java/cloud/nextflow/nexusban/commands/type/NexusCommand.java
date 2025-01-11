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

import java.util.List;

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
        if (!permission.isBlank()) {
            if (!commandSender.hasPermission(permission)) {
                if (commandSender instanceof Player) {
                    commandSender.sendMessage(messageManager.loadMessagePrefix(((Player) commandSender), "chat.no-permission"));
                } else {
                    commandSender.sendMessage(messageManager.loadMessagePrefix(null, "chat.no-permission"));
                }
                return false;
            }
        }

        return onRun(commandSender, command, label, args);
    }

    public abstract boolean onRun(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

    @Override
    public abstract @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

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
