package cloud.nextflow.nexusban.managers.punishments;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.database.DBUtils;
import cloud.nextflow.nexusban.database.types.exceptions.DatabaseException;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.database.DatabaseManager;
import cloud.nextflow.nexusban.managers.types.NexusManager;
import cloud.nextflow.nexusban.types.Punishment;
import cloud.nextflow.nexusban.types.PunishmentType;

import java.util.*;

public class PunishmentManager extends NexusManager {
    private static Map<String, String> playerCache = new HashMap<>();
    private static Map<Boolean, Punishment> punishmentCache = new HashMap<>();
    private static DBUtils dbUtils;

    public PunishmentManager(NexusBan nexusBan) {
        super(nexusBan, "Punishment Manager");
    }

    @Override
    public void register() throws ManagerException {
        dbUtils = DatabaseManager.getDatabaseManager().getDbUtils();
        loadPunishmentsDB();
    }

    public static void loadPunishmentsDB() throws ManagerException {
        try {
            dbUtils.deleteExpiredPunishments();
            for (Punishment punishment : dbUtils.getAllPunishments(false)) {
                punishmentCache.put(false, punishment);
            }
            for (Punishment punishment : dbUtils.getAllPunishments(true)) {
                punishmentCache.put(true, punishment);
            }
        } catch (DatabaseException exception) {
            throw new ManagerException("An issue with the database occurred", exception);
        }
    }

    public static void addPunishment(Punishment punishment) {
        punishmentCache.put(punishment.isPermanent(), punishment);
    }

    public static Punishment findPunishment(String caseID) {
        Optional<Punishment> punishmentOptional = punishmentCache.values().stream().filter(p -> p.getCaseID().equals(caseID)).findFirst();
        return punishmentOptional.orElse(null);
    }

    public static Punishment findPunishment(String punishedUUID, PunishmentType punishmentType) {
        Optional<Punishment> punishmentOptional = punishmentCache.values().stream().filter(p -> p.getPunishedUUID()
                .equals(punishedUUID) && p.getPunishmentType() == punishmentType).findFirst();
        return punishmentOptional.orElse(null);
    }
}
