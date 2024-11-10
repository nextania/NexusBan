package cloud.nextflow.nexusban.managers.commands;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.commands.type.NexusCommand;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends NexusManager {
    private final List<NexusCommand> nexusCommands = new ArrayList<>();

    public CommandManager(NexusBan nexusBan) {
        super(nexusBan, "CommandManager");
        // add all commands into list
    }

    @Override
    public void register() throws ManagerException {

    }
}
