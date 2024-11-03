package cloud.nextflow.nexusban.commands.type;

import org.bukkit.command.TabExecutor;

import java.util.List;

public abstract class NexusCommand implements TabExecutor {
    private String name;
    private String description;
    private String usage;
    private List<String> permissions;

    public NexusCommand(String name) {
        this.name = name;
    }

    public NexusCommand(String name, String description, String usage) {
        this(name);
        this.description = description;
        this.usage = usage;
    }

    public NexusCommand(String name, String description, String usage, List<String> permissions) {
        this(name, description, usage);
        this.permissions = permissions;
    }
}
