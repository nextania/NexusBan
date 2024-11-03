package cloud.nextflow.nexusban;

import cloud.nextflow.nexusban.listeners.PlayerListener;
import cloud.nextflow.nexusban.listeners.PunishmentListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class NexusBan extends JavaPlugin {
    private PunishmentListener punishmentListener;
    private PlayerListener playerListener;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        // register punishment listener
        punishmentListener = new PunishmentListener(this);
        getServer().getPluginManager().registerEvents(punishmentListener, this);
        // register player listener
        playerListener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getLogger().info("NexusBan has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveDefaultConfig();
        getLogger().info("NexusBan has been enabled!");
    }
}
