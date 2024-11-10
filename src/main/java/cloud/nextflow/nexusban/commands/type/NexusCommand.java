package cloud.nextflow.nexusban.commands.type;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class NexusCommand implements TabExecutor {
    private final String name;
    private String description;
    private String usage;
    private List<String> permissions;
    private final MessageManager messageManager;
    private final NexusBan nexusBan;

    public NexusCommand(MessageManager messageManager, String name) {
        this.name = name;
        this.messageManager = messageManager;
        this.nexusBan = messageManager.getNexusBan();
    }

    public NexusCommand(MessageManager messageManager, String name, String description, String usage) {
        this(messageManager, name);
        this.description = description;
        this.usage = usage;
    }

    public NexusCommand(MessageManager messageManager, String name, String description, String usage, List<String> permissions) {
        this(messageManager, name, description, usage);
        this.permissions = permissions;
    }

    @Override
    public abstract boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings);

    @Override
    public abstract @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings);

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public NexusBan getNexusBan() {
        return nexusBan;
    }
}
