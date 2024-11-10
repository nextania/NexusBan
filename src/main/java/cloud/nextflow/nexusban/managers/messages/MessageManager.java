package cloud.nextflow.nexusban.managers.messages;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class MessageManager extends NexusManager {
    private File configFile;
    private FileConfiguration config;

    public MessageManager(NexusBan nexusBan) {
        super(nexusBan, "MessageManager");
    }

    public String loadMessage(Player player, String configName, String... params) {
        String message = config.getString(configName);
        message = replaceOccurrence(message, params);
        message = replacePAPI(player, message);
        return message;
    }

    @Override
    public void register() throws ManagerException {
        nexusBan.saveResource("messages.yml", false);
        configFile = new File(nexusBan.getDataFolder(), "messages.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private String replaceOccurrence(String main, String... toReplaceList) {
        int counter = 1;
        // check counter in messages.yml to make sure it adds up
        for (String toReplace : toReplaceList) {
            main = main.replace("$" + String.valueOf(counter), toReplace);
        }
        return main;
    }

    private String replacePAPI(Player player, String main) {
        return PlaceholderAPI.setPlaceholders(player, main);
    }

    public FileConfiguration get() {
        return config;
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            nexusBan.getLogger().severe("Couldn't save the messages.yml file to disk");
            nexusBan.getServer().getPluginManager().disablePlugin(nexusBan);
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
