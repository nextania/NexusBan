package cloud.nextflow.nexusban.managers.messages;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MessageManager extends NexusManager {
    private File configFile;
    private FileConfiguration config;
    private static MessageManager messageManager;

    public MessageManager(NexusBan nexusBan) {
        super(nexusBan, "Message Manager");
    }

    @Override
    public void register() throws ManagerException {
        nexusBan.saveResource("messages.yml", false);
        configFile = new File(nexusBan.getDataFolder(), "messages.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        messageManager = this;
    }

    public List<String> loadMessages(Player player, String configName, Map<String, String> params) {
        List<String> messages = Objects.requireNonNull(config.getStringList(configName));
        messages.replaceAll(message -> replaceString(player, message, params));
        return messages;
    }

    public String loadMessage(Player player, String configName, Map<String, String> params) {
        String message = Objects.requireNonNull(config.getString(configName));
        message = replaceOccurrence(message, params);
        message = replacePAPI(player, message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String loadMessage(Player player, String configName) {
        String message = Objects.requireNonNull(config.getString(configName));
        message = replaceOccurrence(message, new HashMap<>());
        message = replacePAPI(player, message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String replaceString(Player player, String message, Map<String, String> params) {
        message = replaceOccurrence(message, params);
        message = replacePAPI(player, message);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String loadMessagePrefix(Player player, String configName, Map<String, String> params) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("prefix")))
                + " " + loadMessage(player, configName, params);
    }

    public String loadMessagePrefix(Player player, String configName) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("prefix")))
                + " " + loadMessage(player, configName, new HashMap<>());
    }

    private String replaceOccurrence(String main, Map<String, String> toReplace) {
        for (Map.Entry<String, String> paramVariable : toReplace.entrySet()) {
            main = main.replace("{" + paramVariable.getKey() + "}", paramVariable.getValue());
        }
        return main;
    }

    private String replacePAPI(Player player, String main) {
        return PlaceholderAPI.setPlaceholders(player, main);
    }

    public void sendMessageList(CommandSender sender, List<String> messages) {
        for (String message : messages) {
            sender.sendMessage(message);
        }
    }

    public void broadcastMessageList(List<String> messages) {
        for (String message : messages) {
            nexusBan.getServer().broadcastMessage(message);
        }
    }

    public FileConfiguration get() {
        return config;
    }

    public void save() throws ManagerException {
        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new ManagerException("Couldn't save the messages.yml file to disk", e);
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static MessageManager getMessageManager() {
        return messageManager;
    }
}
