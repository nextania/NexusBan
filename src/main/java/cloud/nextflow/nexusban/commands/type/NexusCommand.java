package cloud.nextflow.nexusban.commands.type;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class NexusCommand implements TabExecutor {
    private final String name;
    private String description;
    private String usage;
    private String permission;
    private final MessageManager messageManager;
    private final NexusBan nexusBan;

    public NexusCommand(MessageManager messageManager, String name) {
        this.name = name;
        this.permission = "";
        this.messageManager = messageManager;
        this.nexusBan = messageManager.getNexusBan();
    }

    public NexusCommand(MessageManager messageManager, String name, String description, String usage) {
        this(messageManager, name);
        this.description = description;
        this.usage = usage;
    }

    public NexusCommand(MessageManager messageManager, String name, String description, String usage, String permission) {
        this(messageManager, name, description, usage);
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
    public abstract @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings);

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public NexusBan getNexusBan() {
        return nexusBan;
    }
}
