package cloud.nextflow.nexusban;

import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.commands.CommandManager;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.database.DatabaseManager;
import cloud.nextflow.nexusban.managers.listeners.ListenerManager;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import cloud.nextflow.nexusban.managers.types.NexusManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class NexusBan extends JavaPlugin {
    private static NexusBan nexusBan;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        NexusManager[] nexusManagers;
        NexusManager[] earlyNexusManagers;

        nexusBan = this;

        ListenerManager listenerManager = new ListenerManager(this);
        CommandManager commandManager = new CommandManager(this);
        ConfigManager configManager = new ConfigManager(this);
        MessageManager messageManager = new MessageManager(this);

        earlyNexusManagers = new NexusManager[]{configManager, messageManager};
        nexusManagers = new NexusManager[]{listenerManager, commandManager};
        // Plugin startup logic
        saveDefaultConfig();
        loadManagers(earlyNexusManagers, nexusManagers);
        getLogger().info("NexusBan has been enabled!");
    }

    private void loadManagers(NexusManager[] earlyNexusManagers, NexusManager[] nexusManagers) {
        for (NexusManager earlyNexusManager : earlyNexusManagers) {
            registerManager(earlyNexusManager);
            //getLogger().info("Loaded the early nexus manager: " + earlyNexusManager.getManagerName());
        }
        databaseManager = new DatabaseManager(this);
        registerManager(databaseManager);
        //getLogger().info("Loaded early managers");
        for (NexusManager nexusManager : nexusManagers) {
            registerManager(nexusManager);
        }
        //getLogger().info("Loaded regular managers");
    }

    private void registerManager(NexusManager nexusManager) {
        String managerName = nexusManager.getManagerName();
        try {
            nexusManager.register();
        } catch (ManagerException exception) {
            getLogger().log(Level.SEVERE, "Could not register the " + managerName + "!", exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Loaded the " + managerName + "!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveDefaultConfig();
        getLogger().info("NexusBan has been enabled!");
    }

    public static NexusBan getNexusBan() {
        return nexusBan;
    }
}
