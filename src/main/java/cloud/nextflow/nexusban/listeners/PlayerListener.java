package cloud.nextflow.nexusban.listeners;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.listeners.types.NexusListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener extends NexusListener {

    public PlayerListener(NexusBan nexusBan) {
        super(nexusBan);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }
}
