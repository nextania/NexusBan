package cloud.nextflow.nexusban.managers.punishments;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;
import cloud.nextflow.nexusban.types.Punishment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PunishmentManager extends NexusManager {
    private static Map<String, String> playerCache = new HashMap<>();
    private static List<Punishment> temporaryPunishments = new ArrayList<>();
    private static List<Punishment> permanentPunishments = new ArrayList<>();

    public PunishmentManager(NexusBan nexusBan) {
        super(nexusBan, "Punishment Manager");
    }

    @Override
    public void register() throws ManagerException {

    }

    public static void addTemporaryPunishment(Punishment punishment) throws ManagerException {
        if (punishment.isPermanent()) throw new ManagerException("Can not add a permanent punishment to a temporary punishments list");
        temporaryPunishments.add(punishment);
    }

    public static void addPermanentPunishment(Punishment punishment) throws ManagerException {
        if (!punishment.isPermanent()) throw new ManagerException("Can not add a temporary punishment to a permanent punishments list");
        permanentPunishments.add(punishment);
    }

    public static List<Punishment> getTemporaryPunishments() {
        return temporaryPunishments;
    }

    public static List<Punishment> getPermanentPunishments() {
        return permanentPunishments;
    }
}
