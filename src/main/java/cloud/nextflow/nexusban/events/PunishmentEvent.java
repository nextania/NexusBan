package cloud.nextflow.nexusban.events;

import cloud.nextflow.nexusban.types.Punishment;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PunishmentEvent extends Event implements Cancellable {
    private final Punishment punishment;
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled;

    public PunishmentEvent(Punishment punishment) {
        this.punishment = punishment;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public Punishment getPunishment() {
        return punishment;
    }
}
