package cloud.nextflow.nexusban.managers.messages;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageManager extends NexusManager {
    private File configFile;
    private FileConfiguration config;
    private static MessageManager messageManager;

    public MessageManager(NexusBan nexusBan) {
        super(nexusBan, "Message Manager");
    }

    public String loadMessage(Player player, String configName, String... params) {
        // check if config.getString() is null and put try catch
        String message = Objects.requireNonNull(config.getString(configName));
        message = replaceOccurrence(message, params);
        message = replacePAPI(player, message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String loadMessagePrefix(Player player, String configName, String... params) {
        // put try catch and make sure config.getString() isn't null
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("prefix")))
                + " " + loadMessage(player, configName, params);
    }

//    public String loadMessages(Player player, String configName, String... params) {
//        List<String> messagesRaw = Objects.requireNonNull(config.getStringList(configName));
//        List<String> messages = new ArrayList<>();
//        for (String message : messagesRaw) {
//            messages.add(loadMessage())
//        }
//    }

    @Override
    public void register() throws ManagerException {
        nexusBan.saveResource("messages.yml", false);
        configFile = new File(nexusBan.getDataFolder(), "messages.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        messageManager = this;
    }

    private String replaceOccurrence(String main, String... toReplaceList) {
        int counter = 1;
        // check counter in messages.yml to make sure it adds up
        for (String toReplace : toReplaceList) {
            main = main.replace("$" + counter++, toReplace);
        }
        return main;
    }

    private String replacePAPI(Player player, String main) {
        return PlaceholderAPI.setPlaceholders(player, main);
    }

    public FileConfiguration get() {
        return config;
    }

    public void save() throws ManagerException {
        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new ManagerException("Couldn't save the messages.yml file to disk", e);
//            nexusBan.getLogger().severe("Couldn't save the messages.yml file to disk");
//            nexusBan.getServer().getPluginManager().disablePlugin(nexusBan);
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static MessageManager getMessageManager() {
        return messageManager;
    }
}
