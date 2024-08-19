package doublepoof.thesaves.Listeners;

import doublepoof.thesaves.GUI.CreateGUI;
import doublepoof.thesaves.GUI.MainGUI;
import doublepoof.thesaves.UtilClasses.PlayerStates;
import doublepoof.thesaves.Operations.Search;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

import static doublepoof.thesaves.TheSaves.*;

/**
 * @author DoublePOOF
 */
public class ChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();

        // 正在搜寻状态
        if(PlayerStates.isSearching.contains(name)) {
            e.setCancelled(true);

            ConfigurationSection fatherSection = SAVES.getConfigurationSection("Players." + player.getName());
            List<String> pSaves = new ArrayList<>(fatherSection.getKeys(false));

            if("cancel".equalsIgnoreCase(e.getMessage())) {
                player.sendMessage("§2[§a√§2] §a已取消搜索操作");
                MainGUI.setGUI(1, player, fatherSection, pSaves);
                MainGUI.openGUI(player);
            } else {
                Search.setNewMainGUI(e.getMessage());
            }

            PlayerStates.isSearching.remove(name);
        }

        // 正在加入存档简介状态
        if(PlayerStates.isAddingDesc.contains(name)) {
            e.setCancelled(true);

            if("cancel".equalsIgnoreCase(e.getMessage())) {
                player.sendMessage("§2[§a√§2] §a已取消加入存档简介操作");
            } else {
                String desc = e.getMessage().replaceAll("&", "§");

                if(tooLongDesc(desc)) {
                    player.sendMessage("§4[§c!§4] §c这一行简介太长了，请重新输入：");
                    return;
                }

                ItemStack temp = CreateGUI.getIcon(player, 13);
                ItemMeta tempMeta = temp.getItemMeta();

                List<String> descriptions = tempMeta.getLore() == null ? new ArrayList<>() : tempMeta.getLore();

                descriptions.add(desc);
                tempMeta.setLore(descriptions);

                temp.setItemMeta(tempMeta);

                CreateGUI.setIcon(player, 13, temp);
            }
            playerCompletedCreateGUIOperation(player);
        }

        // 正在重命名状态
        if(PlayerStates.isRenaming.contains(name)) {
            e.setCancelled(true);

            if("cancel".equalsIgnoreCase(e.getMessage())) {
                player.sendMessage("§2[§a√§2] §a已取消重命名操作");
            } else {
                String saveName = e.getMessage().replaceAll("&", "§");

                if(isExisted(player, saveName)) {
                    player.sendMessage("§4[§c!§4] §c该存档名已存在，请重新输入：");
                    return;
                }
                if(tooLongName(saveName)) {
                    player.sendMessage("§4[§c!§4] §c存档名太长了，请重新输入：");
                    return;
                }
                if(isIllegalName(saveName)) {
                    player.sendMessage("§4[§c!§4] §c存档名不能携带空格，请重新输入：");
                    return;
                }

                ItemStack temp = CreateGUI.getIcon(player, 13);
                ItemMeta tempMeta = temp.getItemMeta();
                tempMeta.setDisplayName(saveName);
                temp.setItemMeta(tempMeta);

                CreateGUI.setIcon(player, 13, temp);
            }
            playerCompletedCreateGUIOperation(player);
        }
    }

    public static boolean isExisted(Player player, String saveName) {
        ConfigurationSection temp = SAVES.getConfigurationSection("Players." + player.getName());
        return temp.getKeys(false).contains(saveName);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();
        if(PlayerStates.isSearching.contains(name) || PlayerStates.isRenaming.contains(name) || PlayerStates.isAddingDesc.contains(name)) {
            player.sendMessage("§4[§c!§4] §c你目前不能使用指令");
            e.setCancelled(true);
        }
    }

    public static boolean isIllegalName(String name) {
        return name.contains(" ");
    }

    public static boolean tooLongName(String name) {
        return name.length() > 24;
    }

    public static boolean tooLongDesc(String desc) {
        return desc.length() > 64;
    }

    /**
     * 玩家完成操作后，解除其冻结状态，将其传送回原点并打开CreateGUI
     * @param player 要操作的玩家
     */
    private void playerCompletedCreateGUIOperation(Player player) {
        String name = player.getName();
        // 传送回原点
        player.teleport(PlayerStates.playerLocation.get(name));
        // 解除冻结状态（正在做xxx的状态）
        PlayerStates.playerLocation.remove(name);
        PlayerStates.isRenaming.remove(name);
        PlayerStates.isAddingDesc.remove(name);
        PlayerStates.isFreezeMoving.remove(name);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        // 打开CreateGUI
        CreateGUI.openGUI(player);
    }
}