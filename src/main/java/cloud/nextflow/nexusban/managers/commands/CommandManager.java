package cloud.nextflow.nexusban.managers.commands;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.commands.ban.BanCommand;
import cloud.nextflow.nexusban.commands.mute.MuteCommand;
import cloud.nextflow.nexusban.commands.test.TestCommand;
import cloud.nextflow.nexusban.commands.type.NexusCommand;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import cloud.nextflow.nexusban.managers.types.NexusManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandManager extends NexusManager {
    private final List<NexusCommand> nexusCommands = new ArrayList<>();
    private MessageManager messageManager;
    private ConfigManager configManager;

    public CommandManager(NexusBan nexusBan) {
        super(nexusBan, "Command Manager");
    }

    public void loadCommands() throws ManagerException {
        messageManager = MessageManager.getMessageManager();
        configManager = ConfigManager.getConfigManager();

        //add all commands
        nexusCommands.add(new TestCommand(messageManager, configManager));
        nexusCommands.add(new MuteCommand(messageManager, configManager));
    }

    @Override
    public void register() throws ManagerException {
        this.loadCommands();
        for (NexusCommand nexusCommand : nexusCommands) {
            try {
                Objects.requireNonNull(nexusBan.getCommand(nexusCommand.getName())).setExecutor(nexusCommand);
            } catch (NullPointerException exception) {
                throw new ManagerException("Issue with command? Perhaps command wasn't defined in plugin.yml? Command Name: " + nexusCommand.getName(), exception);
            }
            if (configManager.getVerboseMode()) {
                nexusBan.getLogger().info("Registered command: " + nexusCommand.getName());
            }
        }
    }
}
