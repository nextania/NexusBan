package cloud.nextflow.nexusban.managers.config;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;

public class ConfigManager extends NexusManager {

    public ConfigManager(NexusBan nexusBan) {
        super(nexusBan, "Config Manager");
    }

    @Override
    public void register() throws ManagerException {

    }

    public boolean getVerboseMode() {
        return nexusBan.getConfig().getBoolean("verbose");
    }
}
