package cloud.nextflow.nexusban.managers.time;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;

public class TimeManager extends NexusManager {
    public TimeManager(NexusBan nexusBan) {
        super(nexusBan, "Time Manager");
    }

    @Override
    public void register() throws ManagerException {

    }


}
