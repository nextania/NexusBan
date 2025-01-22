package cloud.nextflow.nexusban.listeners;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.listeners.types.NexusListener;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import cloud.nextflow.nexusban.managers.punishments.PunishmentManager;
import cloud.nextflow.nexusban.managers.time.TimeManager;
import cloud.nextflow.nexusban.types.Punishment;
import cloud.nextflow.nexusban.types.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.Instant;
import java.util.*;

public class PlayerListener extends NexusListener {
    public PlayerListener(NexusBan nexusBan) {
        super(nexusBan);
    }

//    @EventHandler
//    public void onPlayerLogin(PlayerLoginEvent event) {
//        Player punished = event.getPlayer();
//        String punishedUUID = punished.getUniqueId().toString();
//        String ip = Objects.requireNonNull(event.getAddress()).getHostName();
//        Punishment punishment = PunishmentManager.findPunishment(punishedUUID, PunishmentType.BAN);
//        if (punishment == null) {
//            punishment = PunishmentManager.findPunishment(punishedUUID, PunishmentType.IPBAN);
//            if (punishment == null) {
//                punishment = PunishmentManager.findIpPunishment(event.getAddress().getHostName());
//                if (punishment == null) return;
//            }
//        }
//        OfflinePlayer punisher = Bukkit.getOfflinePlayer(UUID.fromString(punishment.getPunisherUUID()));
//        Instant startDate = Instant.ofEpochMilli(punishment.getStartDate());
//        Map<String, String> params = new HashMap<>();
//        params.put("id", punishment.getCaseID());
//        params.put("reason", punishment.getReason());
//        params.put("punisher", punisher.getName());
//        params.put("date", TimeManager.convertToDateTime(startDate));
//        if (punishment.isPermanent()) {
//            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, String.join("\n", messageManager.loadMessages(punished, "kick.ban.permanent", params)));
//        } else {
//            Instant endDate = Instant.ofEpochMilli(punishment.getEndDate());
//            params.put("remaining", TimeManager.timeRemaining(startDate, endDate));
//            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, String.join("\n", messageManager.loadMessages(punished, "kick.ban.temporary", params)));
//        }
//    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrePlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            String punishedUUID = event.getUniqueId().toString();
            String ip = Objects.requireNonNull(event.getAddress()).getHostName();
            Punishment punishment = PunishmentManager.findPunishment(punishedUUID, PunishmentType.BAN);
            if (punishment == null) {
                punishment = PunishmentManager.findPunishment(punishedUUID, PunishmentType.IPBAN);
                if (punishment == null) {
                    punishment = PunishmentManager.findIpPunishment(event.getAddress().getHostName());
                    if (punishment == null) return;
                }
            }
            OfflinePlayer punisher = Bukkit.getOfflinePlayer(UUID.fromString(punishment.getPunisherUUID()));
            Instant startDate = Instant.ofEpochMilli(punishment.getStartDate());
            Map<String, String> params = new HashMap<>();
            params.put("id", punishment.getCaseID());
            params.put("reason", punishment.getReason());
            params.put("punisher", punisher.getName());
            params.put("date", TimeManager.convertToDateTime(startDate));
            if (punishment.isPermanent()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, String.join("\n", messageManager.loadMessages("kick.ban.permanent", params)));
            } else {
                Instant endDate = Instant.ofEpochMilli(punishment.getEndDate());
                params.put("remaining", TimeManager.timeRemaining(startDate, endDate));
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, String.join("\n", messageManager.loadMessages("kick.ban.temporary", params)));
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Punishment punishment = PunishmentManager.findPunishment(player.getUniqueId().toString(),
                PunishmentType.MUTE);
        MessageManager messageManager = MessageManager.getMessageManager();
        if (punishment == null) return;
        Map<String, String> params = new HashMap<>();
        params.put("id", punishment.getCaseID());
        params.put("reason", punishment.getReason());
        Instant startDate = Instant.ofEpochMilli(punishment.getStartDate());
        if (punishment.isPermanent()) {
            event.setCancelled(true);
            List<String> messages = messageManager.loadMessages(player, "chat.mute.message.permanent.message", params);
            for (String message : messages) {
                player.sendMessage(message);
            }
        } else {
            if (punishment.getEndDate() > Instant.now().toEpochMilli()) return;
            event.setCancelled(true);
            Instant endDate = Instant.ofEpochMilli(punishment.getEndDate());
            params.put("remaining", TimeManager.timeRemaining(startDate, endDate));
            List<String> messages = messageManager.loadMessages(player, "chat.mute.message.temporary.message", params);
            for (String message : messages) {
                player.sendMessage(message);
            }
        }
    }
}
