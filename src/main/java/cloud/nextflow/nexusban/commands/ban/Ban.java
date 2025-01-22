package cloud.nextflow.nexusban.commands.ban;

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
import java.util.stream.Collectors;

public class BanCommand extends NexusCommand {
    public BanCommand(MessageManager messageManager, ConfigManager configManager) {
        super(messageManager, configManager, "ban", "command to ban someone", "/ban <player> <reason>", "nexusban.command.ban");
    }

    public boolean onRun(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // to properly unban an offline player by name instead of UUID, don't unban them by their name, but
        // instead unban them when they log back on and check if the UUID matches the original to make sure
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
            String reason = String.join("", Arrays.copyOfRange(args, (silent ? 2 : 1), args.length));
            String caseID = "";
            long startDate = Instant.now().toEpochMilli();
            try {
                caseID = DatabaseManager.getDatabaseManager().getDbUtils().createPunishment(PunishmentType.BAN, punished.getUniqueId().toString(),
                        punisher.getUniqueId().toString(), reason, startDate, 0, "", true, Objects.requireNonNull(punished.getAddress()).getHostName());
            } catch (DatabaseException exception) {
                punisher.sendMessage(messageManager.loadMessage(punisher, "chat.database-error", new HashMap<>()));
                nexusBan.getLogger().log(Level.SEVERE, "There was a severe error in creating a punishment", exception);
            }
            Punishment punishment = new Punishment(PunishmentType.BAN, caseID, punished.getUniqueId().toString(), punisher.getUniqueId().toString(),
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
            arguments.add("<Name>");
            for (Player player : Bukkit.getOnlinePlayers()) {
                arguments.add(player.getName());
            }
        } else if (args.length == 2) {
            if (args[0].equals("-s")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    arguments.add(player.getName());
                }
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
