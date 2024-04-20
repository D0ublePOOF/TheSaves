package doublepoof.thesaves.GUI;

import doublepoof.thesaves.UtilClasses.GUIItems;
import doublepoof.thesaves.TheSaves;
import doublepoof.thesaves.UtilClasses.BuildItem;
import doublepoof.thesaves.UtilClasses.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static doublepoof.thesaves.TheSaves.saves;


public class MainGUI {
    private static final BuildItem buildItem = new BuildItem();
    private static Inventory theGUI;

    private static final Pattern judgeNum = Pattern.compile("[0-9]*");
    private static int rows;
    private static int capacity;

    public static void initialization(Player player) {
        String judgement = TheSaves.config.getString("GUI_Row");
        if(!judgeNum.matcher(judgement).matches()) {
            throw new IllegalArgumentException(String.format("[TheSaves] config.yml中GUI_Row的值应该是一个正整数！([%s])", judgement));
        } else {
            rows = Integer.parseInt(judgement);
            if(rows < 2 || rows > 6) {
                throw new IllegalArgumentException(String.format("[TheSaves] config.yml中GUI_Row的值应该大于等于2且小于等于6！([%d])", rows));
            }
        }

        capacity = (rows - 1) * 9;

        theGUI = Bukkit.createInventory(player, rows*9, player.getName() + "的存档");

        theGUI.setItem((rows * 9) - 5, GUIItems.search);

        PlayerStates.mainGUIs.put(player.getName(), theGUI);
    }

    public static boolean setGUI(int page, Player player, ConfigurationSection fatherSection, List<String> pSaves) {
        Inventory temp = PlayerStates.mainGUIs.get(player.getName());

        if(saves.getConfigurationSection("Players." + player.getName()) == null) {
            return false;
        }

        ConfigurationSection tempSection;

        ItemStack prev;
        if(page > 1) {
            prev = GUIItems.prev;
        } else {
            prev = GUIItems.noPrev;
        }
        ItemStack next;
        if(pSaves.size() - (capacity * (page - 1)) > capacity) {
            next = GUIItems.next;
        } else {
            next = GUIItems.noNext;
        }
        temp.setItem((rows * 9) - 8, prev);
        temp.setItem((rows * 9) - 2, next);
        List<ItemStack> itemStacks = new ArrayList<>();
        for(String s : pSaves) {
            tempSection = fatherSection.getConfigurationSection(s);
            String m = tempSection.getString("icon");
            boolean enchanted = tempSection.getBoolean("enchanted");
            List<String> tempDesc = tempSection.getStringList("description");
            String[] desc = new String[tempDesc.size()];
            for(int i = 0; i < tempDesc.size(); i++) {
                desc[i] = tempDesc.get(i);
            }
            itemStacks.add(buildItem.makeItem(m, s, desc, 1, enchanted));
        }
        int index = 0;
        for(int i = (page - 1) * capacity; i < Math.min(itemStacks.size(), page * capacity); i++) {
            temp.setItem(index, itemStacks.get(i));
            index++;
        }

        PlayerStates.mainGUIs.put(player.getName(), temp);
        return true;
    }

    public static boolean openGUI(Player player) {
        player.openInventory(PlayerStates.mainGUIs.get(player.getName()));

        return true;
    }
}
