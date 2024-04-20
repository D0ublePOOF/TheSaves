package doublepoof.thesaves.GUI;

import doublepoof.thesaves.TheSaves;
import doublepoof.thesaves.UtilClasses.BuildItem;
import doublepoof.thesaves.UtilClasses.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

import static doublepoof.thesaves.UtilClasses.GUIItems.*;
import static doublepoof.thesaves.TheSaves.saves;

public class CreateGUI {
    // 无法使用的方块数字ID
    private static final int[] illegalIDs = {
        0, 8, 9, 10, 11, 26, 34, 36, 43, 51, 55, 59, 60, 62, 63, 64, 68, 71, 74, 75, 83, 90, 92, 93, 94, 104, 105, 115, 117, 118, 119, 124, 125, 127, 132, 140, 141, 142, 144, 149, 150, 176, 177, 178, 181, 193, 194, 195, 196, 197, 209
    };

    private static final TheSaves theSaves = TheSaves.getInstance();
    private static final BuildItem buildItem = new BuildItem();
    private static Inventory theGUI;
    private static String saveName;

    public static void initialization(Player player, String name) {
        saveName = name;

        theGUI = Bukkit.createInventory(player, 45, "§a创建新的存档");

        int random = (int) (Math.random()*2267);
        while (Material.getMaterial(random) == null || binarySearch(illegalIDs, random)) {
            random = (int) (Math.random()*2267);
        }

        theGUI.setItem(13, buildItem.makeItem(String.valueOf(random), saveName, new String[]{}, 1, false));
        theGUI.setItem(22, createGUItips);
        theGUI.setItem(28, rename);
        theGUI.setItem(30, check);
        theGUI.setItem(32, notEnchanted);
        theGUI.setItem(34, randomNewIcon);

        PlayerStates.createGUIs.put(player.getName(), theGUI);
    }

    public static ItemStack getIcon(Player player, int index) {
        Inventory temp = PlayerStates.createGUIs.get(player.getName());

        return temp.getItem(index);
    }

    public static boolean setIcon(Player player, int index, ItemStack itemStack) {
        Inventory temp = PlayerStates.createGUIs.get(player.getName());
        temp.setItem(index, itemStack);
        PlayerStates.createGUIs.put(player.getName(), temp);

        return true;
    }

    public static boolean randomNewIcon(Player player, boolean enchanted) {
        Inventory temp = PlayerStates.createGUIs.get(player.getName());

        ItemStack itemStack = temp.getItem(13);
        Material origin = itemStack.getType();
        String name = itemStack.getItemMeta().getDisplayName();
        if(!saveName.equals(name)) {
            saveName = name;
        }
        List<String> l = itemStack.getItemMeta().getLore();
        String[] lore = new String[]{};
        if(l != null) {
            lore = new String[l.size()];
            for(int i = 0; i < l.size(); i++) {
                lore[i] = l.get(i);
            }
        }

        int random = (int) (Math.random()*2267);
        while (Material.getMaterial(random) == null || origin.equals(Material.getMaterial(random)) || binarySearch(illegalIDs, random)) {
            random = (int) (Math.random()*2267);
        }

        temp.setItem(13, buildItem.makeItem(String.valueOf(random), saveName, lore, 1, enchanted));

        PlayerStates.createGUIs.put(player.getName(), temp);

        return true;
    }

    public static boolean createNewConfiguration(Player player, String saveName, String iconType, boolean enchanted, List<String> desc, String playerLocation) {
        ConfigurationSection saveTemp = saves.getConfigurationSection("Players." + player.getName());
        saveTemp.createSection(saveName);
        saveTemp = saveTemp.getConfigurationSection(saveName);

        saveTemp.createSection("icon");
        saveTemp.createSection("enchanted");
        saveTemp.createSection("description");
        saveTemp.createSection("location");

        saveTemp.set("icon", iconType);
        saveTemp.set("enchanted", enchanted);
        saveTemp.set("description", desc);
        saveTemp.set("location", playerLocation);

        try {
            saves.save(new File(theSaves.getDataFolder(), "playerSaves.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean openGUI(Player player) {
        player.openInventory(PlayerStates.createGUIs.get(player.getName()));

        return true;
    }

    private static boolean binarySearch(int[] array, int target) {
        int i = 0, j = array.length - 1;
        int m;
        while (i <= j) {
            m = (i + j) >>> 1;
            if(target < array[m]) {
                j = m - 1;
            } else if(array[m] < target) {
                i = m + 1;
            } else {
                return true;
            }
        }
        return false;
    }
}
