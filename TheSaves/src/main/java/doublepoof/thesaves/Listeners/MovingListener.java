package doublepoof.thesaves.Listeners;

import doublepoof.thesaves.UtilClasses.PlayerStates;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author DoublePOOF
 */
public class MovingListener implements Listener {
    @EventHandler
    public void onMoving(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();
        Location location = player.getLocation();

        if(PlayerStates.isFreezeMoving.contains(name)) {
            player.teleport(location);
        }
    }
}
