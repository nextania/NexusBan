package cloud.nextflow.nexusban.managers.types;

import cloud.nextflow.nexusban.NexusBan;

public abstract class NexusManager {
    private final NexusBan nexusBan;

    public NexusManager(NexusBan nexusBan) {
        this.nexusBan = nexusBan;
    }

    public abstract void register();

    public NexusBan getNexusBan() {
        return nexusBan;
    }
}
