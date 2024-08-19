package doublepoof.thesaves;

import doublepoof.thesaves.Listeners.*;
import doublepoof.thesaves.UtilClasses.Permissions;
import doublepoof.thesaves.UtilClasses.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @author DoublePOOF
 */
public final class TheSaves extends JavaPlugin {
    public static File CONFIGFILE;
    public static File SAVESFILE;
    public static YamlConfiguration CONFIG = new YamlConfiguration();
    public static YamlConfiguration SAVES = new YamlConfiguration();

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

    public void reload() {
        loadYamlConfiguration();
        saveYamlConfiguration();
        getWorld();
        getPermissions();
    }

    public void loadYamlConfiguration() {
        CONFIGFILE = new File(getInstance().getDataFolder(), "config.yml");
        SAVESFILE = new File(getInstance().getDataFolder(), "playerSaves.yml");

        if(!CONFIGFILE.exists()) {
            saveResource("config.yml", false);
        }
        if(!SAVESFILE.exists()) {
            saveResource("playerSaves.yml", false);
        }

        try {
            CONFIG.load(CONFIGFILE);
            SAVES.load(SAVESFILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveYamlConfiguration() {
        try {
            Class c = SAVES.getClass();
            c.getDeclaredMethods();
            SAVES.save(SAVESFILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getWorld() {
        Worlds.save_illegalWorlds = CONFIG.getStringList("Save_IllegalWorlds");
        Worlds.load_illegalWorlds = CONFIG.getStringList("Load_IllegalWorlds");
    }

    private void getPermissions() {
        Permissions.save_exPermissions = CONFIG.getStringList("Save_ExtraPermissions");
        Permissions.save_ilPermissions = CONFIG.getStringList("Save_IllegalPermissions");
        Permissions.load_exPermissions = CONFIG.getStringList("Load_ExtraPermissions");
        Permissions.load_ilPermissions = CONFIG.getStringList("Load_IllegalPermissions");
    }

    public static TheSaves getInstance() {
        return getPlugin(TheSaves.class);
    }
}
