package doublepoof.thesaves.Listeners;

import doublepoof.thesaves.TheSaves;
import doublepoof.thesaves.UtilClasses.PlayerStates;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;

import static doublepoof.thesaves.TheSaves.config;
import static doublepoof.thesaves.TheSaves.saves;

public class JoinListener implements Listener {
    TheSaves theSaves = TheSaves.getInstance();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if(saves.getConfigurationSection("Players." + player.getName()) == null) {
            ConfigurationSection temp = saves.getConfigurationSection("Players");
            temp.createSection(player.getName());
            save();
        }
    }

    public void save() {
        try {
            saves.save(new File(TheSaves.getInstance().getDataFolder(), "playerSaves.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
