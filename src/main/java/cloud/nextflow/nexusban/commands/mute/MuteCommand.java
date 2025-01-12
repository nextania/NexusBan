package cloud.nextflow.nexusban.commands.mute;

import cloud.nextflow.nexusban.commands.type.NexusCommand;
import cloud.nextflow.nexusban.database.types.exceptions.DatabaseException;
import cloud.nextflow.nexusban.events.PunishmentEvent;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.database.DatabaseManager;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import cloud.nextflow.nexusban.managers.punishments.PunishmentManager;
import cloud.nextflow.nexusban.types.Punishment;
import cloud.nextflow.nexusban.types.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MuteCommand extends NexusCommand {
    public MuteCommand(MessageManager messageManager, ConfigManager configManager) {
        super(messageManager, configManager, "mute", "command to mute someone permanently",
                "/mute <person> <reason>", "nexusban.mute");

    }

    @Override
    public boolean onRun(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (commandSender instanceof Player punisher) {
            if (args.length < 1) {
                Map<String, String> params = new HashMap<>();
                params.put("usage", getUsage());
                commandSender.sendMessage(messageManager.loadMessage(punisher, "chat.number-args", params));
                return false;
            }
            Player punished;
            boolean silent = args[0].equals("-s");
            if (silent) {
                if (args.length < 2) {
                    Map<String, String> params = new HashMap<>();
                    params.put("usage", getUsage());
                    commandSender.sendMessage(messageManager.loadMessage(punisher, "chat.number-args", params));
                    return false;
                }
                punished = Bukkit.getPlayerExact(args[1]);
            } else {
                punished = Bukkit.getPlayerExact(args[0]);
            }
            if (punished == null) {
                Map<String, String> params = new HashMap<>();
                params.put("player", args[0]);
                punisher.sendMessage(messageManager.loadMessage(punisher, "chat.player-not-found", params));
                return false;
            }
            String reason = String.join("", Arrays.copyOfRange(args, silent ? 3 : 2, args.length));
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
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
