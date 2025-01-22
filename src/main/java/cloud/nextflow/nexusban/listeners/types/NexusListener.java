package cloud.nextflow.nexusban.listeners.types;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import org.bukkit.event.Listener;

public abstract class NexusListener implements Listener {
    protected NexusBan nexusBan;
    protected final MessageManager messageManager;

    public NexusListener(NexusBan nexusBan) {
        this.nexusBan = nexusBan;
        this.messageManager = MessageManager.getMessageManager();
    }

    public NexusBan getNexusBan() {
        return nexusBan;
    }

    public void setNexusBan(NexusBan nexusBan) {
        this.nexusBan = nexusBan;
    }
}
