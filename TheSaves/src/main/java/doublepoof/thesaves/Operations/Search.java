package doublepoof.thesaves.Operations;

import doublepoof.thesaves.GUI.MainGUI;
import doublepoof.thesaves.UtilClasses.PlayerStates;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static doublepoof.thesaves.TheSaves.*;

/**
 * @author DoublePOOF
 */
public class Search {
    private static Player player;

    public static void initialization(Player player) {
        Search.player = player;

        player.sendMessage("§e[§6*§e] §f请在聊天框输入你要搜索的内容§7，§c不允许超过24个字符§7(输入cancel取消)：");

        PlayerStates.isSearching.add(player.getName());
    }

    public static void setNewMainGUI(String search) {
        if(SAVES.getConfigurationSection("Players." + player.getName()) == null) {
            return;
        }
        ConfigurationSection fatherSection = SAVES.getConfigurationSection("Players." + player.getName());
        List<String> playerSaves = new ArrayList<>(fatherSection.getKeys(false));

        playerSaves.removeIf(target -> !isSearchingTarget(search, target));

        returnMainGUI(playerSaves, fatherSection);
    }

    public static boolean returnMainGUI(List<String> pSave, ConfigurationSection fatherSection) {
        MainGUI.initialization(player);
        MainGUI.setGUI(1, player, fatherSection, pSave);
        MainGUI.openGUI(player);

        return true;
    }

    /**
     * 在不区分大小写的情况下，查找搜索内容是否是目标内容的子序列
     * @param search 搜索内容
     * @param target 目标内容
     * @return 是则返回true，不是则返回false
     */
    private static boolean isSearchingTarget(String search, String target) {
        int searchPointer = 0, targetPointer = 0;
        search = search.toLowerCase();
        target = target.toLowerCase();

        // targetPointer不断后移，并比较两个指针所处位置的字符，若相同，searchPointer后移
        while(searchPointer != search.length() && targetPointer != target.length()) {
            char searchCharacter = search.charAt(searchPointer), targetCharacter = target.charAt(targetPointer);
            if(searchCharacter == targetCharacter) {
                searchPointer += 1;
            }
            targetPointer += 1;
        }

        // 如果遍历完了search字符串，那么就代表着搜索的内容是目标内容的子序列
        return searchPointer == search.length();
    }
}
