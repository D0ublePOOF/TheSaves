package doublepoof.thesaves.Operations;

import doublepoof.thesaves.GUI.MainGUI;
import doublepoof.thesaves.UtilClasses.PlayerStates;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static doublepoof.thesaves.TheSaves.saves;

public class Search {
    private static Player player;

    public static void initialization(Player player) {
        Search.player = player;

        player.sendMessage("§e[§6*§e] §f请在聊天框输入你要搜索的内容§7，§c不允许超过24个字符§7(输入cancel取消)：");

        PlayerStates.isSearching.add(player.getName());
    }

    public static void setNewMainGUI(String search) {
        if(saves.getConfigurationSection("Players." + player.getName()) == null) {
            return;
        }
        ConfigurationSection fatherSection = saves.getConfigurationSection("Players." + player.getName());
        List<String> pSaves = new ArrayList<>(fatherSection.getKeys(false));

        pSaves.removeIf(target -> !isSearchingTarget(search, target));

        returnMainGUI(pSaves, fatherSection);
    }

    public static boolean returnMainGUI(List<String> pSave, ConfigurationSection fatherSection) {
        MainGUI.initialization(player);
        MainGUI.setGUI(1, player, fatherSection, pSave);
        MainGUI.openGUI(player);

        return true;
    }

    private static boolean isSearchingTarget(String search, String target) {
        Map<Character, Integer> temp = new HashMap<>();
        target = target.toLowerCase();
        search = search.toLowerCase();
        for(int i = 0; i < target.length(); i++) {
            char c = target.charAt(i);
            if(!temp.containsKey(target.charAt(i))) {
                temp.put(c, 1);
            } else {
                temp.put(c, temp.get(c) + 1);
            }
        }
        for(int i = 0; i < search.length(); i++) {
            char c = search.charAt(i);
            if(temp.containsKey(c)) {
                temp.put(c, temp.get(c) - 1);
                if(temp.get(c) == 0) {
                    temp.remove(c);
                }
            } else {
                return false;
            }
        }
        return true;
    }
}
