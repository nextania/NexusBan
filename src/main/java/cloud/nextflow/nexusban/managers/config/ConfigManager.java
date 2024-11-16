package cloud.nextflow.nexusban.managers.config;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;

public class ConfigManager extends NexusManager {
    private static ConfigManager configManager;

    public ConfigManager(NexusBan nexusBan) {
        super(nexusBan, "Config Manager");
    }

    @Override
    public void register() throws ManagerException {
        configManager = this;
    }

    public boolean getVerboseMode() {
        return nexusBan.getConfig().getBoolean("verbose");
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
}
