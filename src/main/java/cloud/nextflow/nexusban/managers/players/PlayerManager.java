package cloud.nextflow.nexusban.managers.players;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;
import cloud.nextflow.nexusban.types.PunishmentType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerManager extends NexusManager {
    private static PlayerManager playerManager;

    public PlayerManager(NexusBan nexusBan) {
        super(nexusBan, "Player Manager");
    }

    @Override
    public void register() throws ManagerException {
        playerManager = this;
    }

    public List<Player> findStaff() {
        return nexusBan.getServer().getOnlinePlayers()
                .stream().filter(p -> p.hasPermission("nexusban.silent"))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Player> findStaff(PunishmentType punishmentType) {
        return nexusBan.getServer().getOnlinePlayers()
                .stream().filter(p -> p.hasPermission("nexusban.silent." + punishmentType.name().toLowerCase()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Player> getExemptStaff() {
        return nexusBan.getServer().getOnlinePlayers()
                .stream().filter(p -> p.hasPermission("nexusban.immune"))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Player> getExemptStaff(PunishmentType punishmentType) {
        return nexusBan.getServer().getOnlinePlayers()
                .stream().filter(p -> p.hasPermission("nexusban.immune." + punishmentType.name().toLowerCase()))
                .collect(Collectors.toUnmodifiableList());
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }
}
