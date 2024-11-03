package cloud.nextflow.nexusban.types;

public class Punishment {
    private String caseID;
    private String punishedUUID;
    private String punisherUUID;
    private String reason;
    private long startDate;
    private long endDate;
    private String time;
    private boolean permanent;
    
    public Punishment(String caseID, String punishedUUID, String punisherUUID, String reason, long startDate, long endDate, String time) {
        this.caseID = caseID;
        this.punishedUUID = punishedUUID;
        this.punisherUUID = punisherUUID;
        this.reason = reason;
        this.startDate = startDate;
        this.endDate = endDate;
        this.time = time;
        this.permanent = false;
    }
    
    public Punishment(String caseID, String punishedUUID, String punisherUUID, String reason, long startDate) {
        this.caseID = caseID;
        this.punishedUUID = punishedUUID;
        this.punisherUUID = punisherUUID;
        this.reason = reason;
        this.startDate = startDate;
        this.permanent = true;
    }

    public String getCaseID() {
        return caseID;
    }

    public void setCaseID(String caseID) {
        this.caseID = caseID;
    }

    public String getpunishedUUID() {
        return punishedUUID;
    }

    public void setpunishedUUID(String punishedUUID) {
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
}
