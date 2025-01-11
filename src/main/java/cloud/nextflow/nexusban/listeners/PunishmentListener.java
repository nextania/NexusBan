package cloud.nextflow.nexusban.listeners;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.events.PunishmentEvent;
import cloud.nextflow.nexusban.listeners.types.NexusListener;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import cloud.nextflow.nexusban.managers.players.PlayerManager;
import cloud.nextflow.nexusban.types.Punishment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class PunishmentListener extends NexusListener {

    public PunishmentListener(NexusBan nexusBan) {
        super(nexusBan);
    }

    @EventHandler
    public void onPunishment(PunishmentEvent event) {
        Punishment punishment = event.getPunishment();
        List<Player> staffList = PlayerManager.getPlayerManager().findStaff(punishment.getPunishmentType());
        switch (punishment.getPunishmentType()) {
            case BAN:
                TextComponent textComponent = new TextComponent();
                if (punishment.getSilent()) {
                    staffList.forEach(p -> {
                        MessageManager messageManager = MessageManager.getMessageManager();
                        if (punishment.isPermanent()) {
                            textComponent.setText(messageManager
                                    .loadMessagePrefix(p, "chat.ban.silent.permanent",
                                            Bukkit.getOfflinePlayer(punishment.getpunishedUUID()).getName(),
                                            Bukkit.getOfflinePlayer(punishment.getPunisherUUID()).getName(),
                                            punishment.getReason()));
                        } else {
                            textComponent.setText(messageManager
                                    .loadMessagePrefix(p, "chat.ban.silent.temporary",
                                            Bukkit.getOfflinePlayer(punishment.getpunishedUUID()).getName(),
                                            Bukkit.getOfflinePlayer(punishment.getPunisherUUID()).getName(),
                                            "TIME REPLACE LATER",
                                            punishment.getReason()));
                        }
                        p.spigot().sendMessage(textComponent);
                    });
                } else {
                    if (punishment.isPermanent()) {

                    } else {

                    }
                    nexusBan.getServer().broadcastMessage()
                }
                break;
            case KICK:
                break;
            case WARN:
                break;
            case IPBAN:
                break;
            default:
                break;
        }
        nexusBan.getLogger().info("Punishment event has been called!");
    }
}
