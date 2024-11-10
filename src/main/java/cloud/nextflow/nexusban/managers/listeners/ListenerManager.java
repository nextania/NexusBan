package cloud.nextflow.nexusban.managers.listeners;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.listeners.PlayerListener;
import cloud.nextflow.nexusban.listeners.PunishmentListener;
import cloud.nextflow.nexusban.managers.types.NexusManager;

public class ListenerManager extends NexusManager {
    private PunishmentListener punishmentListener;
    private PlayerListener playerListener;

    public ListenerManager(NexusBan nexusBan) {
        super(nexusBan, "ListenerManager");
    }

    @Override
    public void register() throws ManagerException {
        // register punishment listener
        punishmentListener = new PunishmentListener(nexusBan);
        nexusBan.getServer().getPluginManager().registerEvents(punishmentListener, nexusBan);
        if (getVerboseMode()) {
            nexusBan.getLogger().info("Loaded the punishment listener!");
        }
        // register player listener
        playerListener = new PlayerListener(nexusBan);
        nexusBan.getServer().getPluginManager().registerEvents(playerListener, nexusBan);
        if (getVerboseMode()) {
            nexusBan.getLogger().info("Loaded the player listener!");
        }
    }
}
