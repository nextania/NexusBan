package cloud.nextflow.nexusban.commands.ban;

import cloud.nextflow.nexusban.commands.type.NexusCommand;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BanCommand extends NexusCommand {
    public BanCommand(MessageManager messageManager, ConfigManager configManager, String name, String description, String usage, String permission) {
        super(messageManager, configManager, name, description, usage, permission);
    }

    public boolean onRun(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
