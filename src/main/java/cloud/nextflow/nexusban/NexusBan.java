package cloud.nextflow.nexusban;

import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.commands.CommandManager;
import cloud.nextflow.nexusban.managers.database.DatabaseManager;
import cloud.nextflow.nexusban.managers.listeners.ListenerManager;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import cloud.nextflow.nexusban.managers.types.NexusManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class NexusBan extends JavaPlugin {
    private ListenerManager listenerManager;
    private MessageManager messageManager;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        NexusManager[] nexusManagers;

        listenerManager = new ListenerManager(this);
        messageManager = new MessageManager(this);
        commandManager = new CommandManager(this);
        databaseManager = new DatabaseManager(this);

        nexusManagers = new NexusManager[]{ listenerManager, messageManager, commandManager, databaseManager };
        // Plugin startup logic
        saveDefaultConfig();
        loadManagers(nexusManagers);
        getLogger().info("NexusBan has been enabled!");
    }

    private void loadManagers(NexusManager[] nexusManagers) {
        for (NexusManager nexusManager : nexusManagers) {
            String managerName = nexusManager.getManagerName();
            try {
                nexusManager.register();
            } catch (ManagerException exception) {
                getLogger().log(Level.SEVERE, "Could not register the " + managerName + "!", exception);
                getServer().getPluginManager().disablePlugin(this);
            }
            getLogger().info("Loaded the " + managerName + "!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveDefaultConfig();
        getLogger().info("NexusBan has been enabled!");
    }
}
