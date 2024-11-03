package cloud.nextflow.nexusban.listeners.types;

import cloud.nextflow.nexusban.NexusBan;
import org.bukkit.event.Listener;

public abstract class NexusListener implements Listener {
    private NexusBan nexusBan;

    public NexusListener(NexusBan nexusBan) {
        this.nexusBan = nexusBan;
    }

    public NexusBan getNexusBan() {
        return nexusBan;
    }

    public void setNexusBan(NexusBan nexusBan) {
        this.nexusBan = nexusBan;
    }
}
