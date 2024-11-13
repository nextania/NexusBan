package cloud.nextflow.nexusban.managers.commands;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.commands.TestCommand;
import cloud.nextflow.nexusban.commands.type.NexusCommand;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;

import java.util.ArrayList;
import java.util.List;

public class CommandManager extends NexusManager {
    private final List<NexusCommand> nexusCommands = new ArrayList<>();

    public CommandManager(NexusBan nexusBan) {
        super(nexusBan, "Command Manager");
        // add all commands into list
//        nexusCommands.add(new TestCommand())
    }

    @Override
    public void register() throws ManagerException {

    }
}
