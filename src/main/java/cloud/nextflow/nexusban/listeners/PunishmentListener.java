package cloud.nextflow.nexusban.listeners;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.events.PunishmentEvent;
import cloud.nextflow.nexusban.listeners.types.NexusListener;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import cloud.nextflow.nexusban.managers.players.PlayerManager;
import cloud.nextflow.nexusban.managers.punishments.PunishmentManager;
import cloud.nextflow.nexusban.managers.time.TimeManager;
import cloud.nextflow.nexusban.types.Punishment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PunishmentListener extends NexusListener {
    public PunishmentListener(NexusBan nexusBan) {
        super(nexusBan);
    }

    @EventHandler
    public void onPunishment(PunishmentEvent event) {
        Punishment punishment = event.getPunishment();
        List<Player> staffList = PlayerManager.getPlayerManager().findStaff(punishment.getPunishmentType());
        Map<String, String> messageParams = new HashMap<>();
        Map<String, String> hoverParams = new HashMap<>();
        Player punishedPlayer = Bukkit.getPlayer(UUID.fromString(punishment.getPunishedUUID()));
        Player punisherPlayer = Bukkit.getPlayer(UUID.fromString(punishment.getPunisherUUID()));
        messageParams.put("reason", punishment.getReason());
        messageParams.put("punishee", punishedPlayer.getName());
        messageParams.put("punisher", punisherPlayer.getName());
        switch (punishment.getPunishmentType()) {
            case IPBAN:
                onBan(punishment, staffList, messageParams, punisherPlayer, punishedPlayer, true);
                break;
            case BAN:
                onBan(punishment, staffList, messageParams, punisherPlayer, punishedPlayer, false);
                break;
//            case KICK:
//                onKick(punishment, staffList, messageParams, punishedPlayer);
//                break;
            case MUTE:
                onMute(punishment, staffList, messageParams, punishedPlayer);
                break;
//            case WARN:
//                onWarn(punishment, staffList, messageParams, punishedPlayer);
//                break;
            default:
                break;
        }
        PunishmentManager.addPunishment(punishment);
        nexusBan.getLogger().info("Punishment event has been called!");
    }

    public void onMute(Punishment punishment, List<Player> staffList, Map<String, String> messageParams, Player punishedPlayer) {
        TextComponent textComponent = new TextComponent();
        if (punishment.getSilent()) {
            staffList.forEach(p -> {
                if (punishment.isPermanent()) {
                    textComponent.setText(messageManager
                            .loadMessagePrefix(p, "chat.mute.silent.permanent.message", messageParams));
                } else {
                    messageParams.put("time", "CONVERT MS TIME TO WORDS");
                    textComponent.setText(messageManager
                            .loadMessagePrefix(p, "chat.mute.silent.temporary.message", messageParams));
                }
                p.spigot().sendMessage(textComponent);
            });
        } else {
            List<String> messages = messageManager.loadMessages(punishedPlayer,
                    "chat.mute." + (punishment.isPermanent() ? "permanent" : "temporary") + ".message",
                    messageParams);
            messageManager.broadcastMessageList(messages);
        }
        Map<String, String> params = new HashMap<>();
        params.put("reason", punishment.getReason());
        Player punisher = Bukkit.getPlayer(UUID.fromString(punishment.getPunisherUUID()));
        if (punishment.isPermanent()) {
            List<String> messages = messageManager.loadMessages(punisher, "chat.mute.message.permanent.message", params);
            for (String message : messages) {
                punishedPlayer.sendMessage(message);
            }
        } else {
            if (punishment.getEndDate() > Instant.now().toEpochMilli()) return;
            params.put("time-remaining", "");
            List<String> messages = messageManager.loadMessages(punisher, "chat.mute.message.temporary.message", params);
            for (String message : messages) {
                punishedPlayer.sendMessage(message);
            }
        }
    }

    public void onBan(Punishment punishment, List<Player> staffList, Map<String, String> messageParams, Player punisherPlayer, Player punishedPlayer, boolean ip) {
        TextComponent textComponent = new TextComponent();
        String banType = ip ? "ipban" : "ban";
        if (punishment.getSilent()) {
            staffList.forEach(p -> {
                if (punishment.isPermanent()) {
                    textComponent.setText(messageManager
                            .loadMessagePrefix(p, "chat." + banType + ".silent.permanent.message", messageParams));
                } else {
                    messageParams.put("time", "CONVERT MS TIME TO WORDS");
                    textComponent.setText(messageManager
                            .loadMessagePrefix(p, "chat." + banType + ".silent.temporary.message", messageParams));
                }
                p.spigot().sendMessage(textComponent);
            });
        } else {
            List<String> messages = messageManager.loadMessages(punishedPlayer,
                    "chat." + banType + "." + (punishment.isPermanent() ? "permanent" : "temporary") + ".message",
                    messageParams);
            messageManager.broadcastMessageList(messages);
        }
        Instant startDate = Instant.ofEpochMilli(punishment.getStartDate());
        Map<String, String> params = new HashMap<>();
        params.put("id", punishment.getCaseID());
        params.put("reason", punishment.getReason());
        params.put("punisher", punisherPlayer.getName());
        params.put("date", TimeManager.convertToDateTime(startDate));
        if (punishment.isPermanent()) {
            punishedPlayer.kickPlayer(String.join("\n", messageManager.loadMessages("kick.ban.permanent", params)));
        } else {
            Instant endDate = Instant.ofEpochMilli(punishment.getEndDate());
            params.put("remaining", TimeManager.timeRemaining(startDate, endDate));
            punishedPlayer.kickPlayer(String.join("\n", messageManager.loadMessages("kick.ban.temporary", params)));
        }
    }
}
