package doublepoof.thesaves.UtilClasses;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerStates {
    public static Map<String, Inventory> createGUIs = new HashMap<>();
    public static Map<String, Inventory> mainGUIs = new HashMap<>();

    public static List<String> isSearching = new ArrayList<>();
    public static List<String> isFreezeMoving = new ArrayList<>();
    public static Map<String, Location> playerLocation = new HashMap<>();
    public static List<String> isAddingDesc = new ArrayList<>();
    public static List<String> isRenaming = new ArrayList<>();
}
