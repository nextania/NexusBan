package cloud.nextflow.nexusban.commands.ban;

import cloud.nextflow.nexusban.commands.type.NexusCommand;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TempBan extends NexusCommand {
    public Ban(MessageManager messageManager, ConfigManager configManager) {
        super(messageManager, configManager, "tempban", "command to tempban a person for some time", "", "nexusban.command.ban.temp");
    }

    public boolean validateUsage() {

    }

    public boolean onRun(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

    }
}
