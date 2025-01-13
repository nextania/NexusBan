package cloud.nextflow.nexusban.listeners;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.listeners.types.NexusListener;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import cloud.nextflow.nexusban.managers.punishments.PunishmentManager;
import cloud.nextflow.nexusban.types.Punishment;
import cloud.nextflow.nexusban.types.PunishmentType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListener extends NexusListener {
    public PlayerListener(NexusBan nexusBan) {
        super(nexusBan);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Punishment punishment = PunishmentManager.findPunishment(player.getUniqueId().toString(),
                PunishmentType.MUTE);
        MessageManager messageManager = MessageManager.getMessageManager();
        if (punishment == null) return;
        Map<String, String> params = new HashMap<>();
        params.put("reason", punishment.getReason());
        if (punishment.isPermanent()) {
            event.setCancelled(true);
            List<String> messages = messageManager.loadMessages(player, "chat.mute.message.permanent.message", params);
            for (String message : messages) {
                player.sendMessage(message);
            }
        } else {
            if (punishment.getEndDate() > Instant.now().toEpochMilli()) return;
            event.setCancelled(true);
            params.put("time-remaining", "");
            List<String> messages = messageManager.loadMessages(player, "chat.mute.message.temporary.message", params);
            for (String message : messages) {
                player.sendMessage(message);
            }
        }
    }
}
