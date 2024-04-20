package doublepoof.thesaves.Listeners;

import doublepoof.thesaves.TheSaves;
import doublepoof.thesaves.UtilClasses.PlayerStates;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import java.io.File;

import static doublepoof.thesaves.TheSaves.saves;

public class QuitListener implements Listener {
    TheSaves theSaves = TheSaves.getInstance();
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();
        if(PlayerStates.playerLocation.containsKey(name)) {
            player.teleport(PlayerStates.playerLocation.get(name));
            PlayerStates.playerLocation.remove(name);
        }
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        PlayerStates.isSearching.remove(name);
        PlayerStates.isRenaming.remove(name);
        PlayerStates.isAddingDesc.remove(name);
        PlayerStates.isFreezeMoving.remove(name);
    }
}
