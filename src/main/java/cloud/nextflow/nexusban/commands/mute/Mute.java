package cloud.nextflow.nexusban.commands.mute;

import cloud.nextflow.nexusban.commands.type.NexusCommand;
import cloud.nextflow.nexusban.database.types.exceptions.DatabaseException;
import cloud.nextflow.nexusban.events.PunishmentEvent;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.database.DatabaseManager;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import cloud.nextflow.nexusban.types.Punishment;
import cloud.nextflow.nexusban.types.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

public class Mute extends NexusCommand {
    public Mute(MessageManager messageManager, ConfigManager configManager) {
        super(messageManager, configManager, "mute", "command to mute someone permanently",
                "/mute <person> <reason>", "nexusban.command.mute");

    }

    @Override
    public boolean validateUsage(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) return false;
        boolean silent = args[0].equals("-s");
        if (silent) {
            return args.length >= 2;
        }
        return true;
    }

    @Override
    public boolean onRun(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player punisher) {
            Player punished;
            boolean silent = args[0].equals("-s");
            if (silent) {
                punished = Bukkit.getPlayerExact(args[1]);
            } else {
                punished = Bukkit.getPlayerExact(args[0]);
            }
            if (!validatePlayerExists(args[0], punisher, punished)) return false;
            String reason = String.join("", Arrays.copyOfRange(args, (silent ? 2 : 1), args.length));
            String caseID = "";
            long startDate = Instant.now().toEpochMilli();
            try {
                caseID = DatabaseManager.getDatabaseManager().getDbUtils().createPunishment(PunishmentType.MUTE, punished.getUniqueId().toString(),
                        punisher.getUniqueId().toString(), reason, startDate, 0, "", true);
            } catch (DatabaseException exception) {
                punisher.sendMessage(messageManager.loadMessage(punisher, "chat.database-error", new HashMap<>()));
                nexusBan.getLogger().log(Level.SEVERE, "There was a severe error in creating a punishment", exception);
            }
            Punishment punishment = new Punishment(PunishmentType.MUTE, caseID, punished.getUniqueId().toString(), punisher.getUniqueId().toString(),
                    reason, startDate, silent);
            PunishmentEvent punishmentEvent = new PunishmentEvent(punishment);
            Bukkit.getPluginManager().callEvent(punishmentEvent);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTab(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            arguments.add("-s");
            Bukkit.getOnlinePlayers().forEach(player -> arguments.add(player.getName()));
        } else if (args.length == 2) {
            if (args[0].equals("-s")) {
                Bukkit.getOnlinePlayers().forEach(player -> arguments.add(player.getName()));
            } else {
                arguments.add("Reason");
            }
        } else if (args.length == 3) {
            if (args[0].equals("-s")) {
                arguments.add("Reason");
            }
        }
        return arguments;
    }
}
