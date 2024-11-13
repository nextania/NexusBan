package cloud.nextflow.nexusban;

import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.commands.CommandManager;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.database.DatabaseManager;
import cloud.nextflow.nexusban.managers.listeners.ListenerManager;
import cloud.nextflow.nexusban.managers.messages.MessageManager;
import cloud.nextflow.nexusban.managers.types.NexusManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;

public final class NexusBan extends JavaPlugin {
    private ListenerManager listenerManager;
    private CommandManager commandManager;
    private DatabaseManager databaseManager;
    private static MessageManager messageManager;
    private static ConfigManager configManager;

    @Override
    public void onEnable() {
        NexusManager[] nexusManagers;
        NexusManager[] earlyNexusManagers;

        listenerManager = new ListenerManager(this);
        commandManager = new CommandManager(this);
        databaseManager = new DatabaseManager(this);
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);

        earlyNexusManagers = new NexusManager[]{ configManager, messageManager };
        nexusManagers = new NexusManager[]{ listenerManager, commandManager, databaseManager };
        // Plugin startup logic
        saveDefaultConfig();
        loadManagers(nexusManagers);
        getLogger().info("NexusBan has been enabled!");
    }

    private void loadManagers(NexusManager[] nexusManagers) {
        NexusManager configManager = Arrays.stream(nexusManagers).filter(r -> commandManager.getManagerName().equals("Config Manager")).findFirst().orElse(null);
        if (configManager == null) {
            getLogger().severe("Could not load the config manager");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerManager(configManager);
        for (NexusManager nexusManager : nexusManagers) {
            registerManager(nexusManager);
        }
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

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveDefaultConfig();
        getLogger().info("NexusBan has been enabled!");
    }
}
