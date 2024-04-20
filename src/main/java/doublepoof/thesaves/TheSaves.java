package doublepoof.thesaves;

import doublepoof.thesaves.Listeners.*;
import doublepoof.thesaves.UtilClasses.Permissions;
import doublepoof.thesaves.UtilClasses.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @author Administrator
 */
public final class TheSaves extends JavaPlugin {

    final private File configFile = new File(getDataFolder(), "config.yml");
    final private File playerSaves = new File(getDataFolder(), "playerSaves.yml");

    final public static YamlConfiguration config = new YamlConfiguration();
    final public static YamlConfiguration saves = new YamlConfiguration();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginCommand("TheSaves").setExecutor(new TheCommands());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new MovingListener(), this);

        reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean reload() {
        saveYamlFiles();
        getWorld();
        getPermissions();

        return true;
    }

    private void saveYamlFiles() {
        if(!configFile.exists()) {
            saveResource("config.yml", false);
        }
        if(!playerSaves.exists()) {
            saveResource("playerSaves.yml", false);
        }

        try {
            config.load(configFile);
            saves.load(playerSaves);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getWorld() {
        Worlds.save_illegalWorlds = config.getStringList("Save_IllegalWorlds");
        Worlds.load_illegalWorlds = config.getStringList("Load_IllegalWorlds");
    }

    private void getPermissions() {
        Permissions.save_exPermissions = config.getStringList("Save_ExtraPermissions");
        Permissions.save_ilPermissions = config.getStringList("Save_IllegalPermissions");
        Permissions.load_exPermissions = config.getStringList("Load_IllegalPermissions");
        Permissions.load_ilPermissions = config.getStringList("Load_IllegalPermissions");
    }

    public static TheSaves getInstance() {
        return getPlugin(TheSaves.class);
    }
}
