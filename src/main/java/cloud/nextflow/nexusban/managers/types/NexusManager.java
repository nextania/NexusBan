package cloud.nextflow.nexusban.managers.types;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;

public abstract class NexusManager {
    protected final NexusBan nexusBan;
    protected final String managerName;

    public NexusManager(NexusBan nexusBan, String managerName) {
        this.nexusBan = nexusBan;
        this.managerName = managerName;
    }

    public abstract void register() throws ManagerException;

    public NexusBan getNexusBan() {
        return nexusBan;
    }

    public String getManagerName() {
        return managerName;
    }
}