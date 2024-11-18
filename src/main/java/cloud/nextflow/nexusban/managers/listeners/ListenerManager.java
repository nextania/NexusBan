package cloud.nextflow.nexusban.managers.listeners;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.listeners.PlayerListener;
import cloud.nextflow.nexusban.listeners.PunishmentListener;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.types.NexusManager;

public class ListenerManager extends NexusManager {

    public ListenerManager(NexusBan nexusBan) {
        super(nexusBan, "Listener Manager");
    }

    @Override
    public void register() throws ManagerException {
        // register punishment listener
        PunishmentListener punishmentListener = new PunishmentListener(nexusBan);
        nexusBan.getServer().getPluginManager().registerEvents(punishmentListener, nexusBan);
        if (ConfigManager.getConfigManager().getVerboseMode()) {
            nexusBan.getLogger().info("Loaded the punishment listener!");
        }
        // register player listener
        PlayerListener playerListener = new PlayerListener(nexusBan);
        nexusBan.getServer().getPluginManager().registerEvents(playerListener, nexusBan);
        if (ConfigManager.getConfigManager().getVerboseMode()) {
            nexusBan.getLogger().info("Loaded the player listener!");
        }
    }
}
