package cloud.nextflow.nexusban.commands;

import cloud.nextflow.nexusban.commands.type.NexusCommand;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestCommand extends NexusCommand {
    public TestCommand(MessageManager messageManager, ConfigManager configManager) {
        super(messageManager, configManager, "test", "A command to test nexus ban", "/test", "nexusban.test");
    }

    @Override
    public boolean onRun(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player) {
            commandSender.sendMessage("You are player with name: " + ((Player) commandSender).getDisplayName());
        } else {
            commandSender.sendMessage("You are console!");
            commandSender.sendMessage("Name: " + commandSender.getName());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
