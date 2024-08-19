package doublepoof.thesaves.Listeners;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static doublepoof.thesaves.TheSaves.*;

/**
 * @author DoublePOOF
 */
public class JoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if(SAVES.getConfigurationSection("Players." + player.getName()) == null) {
            ConfigurationSection temp = SAVES.getConfigurationSection("Players");
            temp.createSection(player.getName());
        }
    }
}
