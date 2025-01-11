package cloud.nextflow.nexusban.types;

public class Punishment {
    private PunishmentType punishmentType;
    private String caseID;
    private String punishedUUID;
    private String punisherUUID;
    private String reason;
    private long startDate;
    private long endDate;
    private String time;
    private boolean permanent;
    private boolean silent;

    public Punishment(PunishmentType punishmentType, String caseID, String punishedUUID, String punisherUUID, String reason, long startDate, long endDate, String time, boolean silent) {
        this(punishmentType, caseID, punishedUUID, punisherUUID, reason, startDate, silent);
        this.endDate = endDate;
        this.time = time;
        this.permanent = false;
    }
    
    public Punishment(PunishmentType punishmentType, String caseID, String punishedUUID, String punisherUUID, String reason, long startDate, boolean silent) {
        this.punishmentType = punishmentType;
        this.caseID = caseID;
        this.punishedUUID = punishedUUID;
        this.punisherUUID = punisherUUID;
        this.reason = reason;
        this.startDate = startDate;
        this.silent = silent;
        this.permanent = true;
    }

    public boolean getSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public String getCaseID() {
        return caseID;
    }

    public void setCaseID(String caseID) {
        this.caseID = caseID;
    }

    public String getPunishedUUID() {
        return punishedUUID;
    }

    public void setPunishedUUID(String punishedUUID) {
        this.punishedUUID = punishedUUID;
    }

    public String getPunisherUUID() {
        return punisherUUID;
    }

    public void setPunisherUUID(String punisherUUID) {
        this.punisherUUID = punisherUUID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    public void setPunishmentType(PunishmentType punishmentType) {
        this.punishmentType = punishmentType;
    }
}
