package cloud.nextflow.nexusban.listeners;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.events.PunishmentEvent;
import cloud.nextflow.nexusban.listeners.types.NexusListener;
import org.bukkit.event.EventHandler;

public class PunishmentListener extends NexusListener {

    public PunishmentListener(NexusBan nexusBan) {
        super(nexusBan);
    }

    @EventHandler
    public void onPunishment(PunishmentEvent event) {
        nexusBan.getServer().getLogger().info("Punishment event has been called!");
    }
}
