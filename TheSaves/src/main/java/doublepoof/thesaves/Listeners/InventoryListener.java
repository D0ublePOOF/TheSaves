package doublepoof.thesaves.Listeners;

import doublepoof.thesaves.*;
import doublepoof.thesaves.GUI.CreateGUI;
import doublepoof.thesaves.GUI.MainGUI;
import doublepoof.thesaves.Operations.AddDesc;
import doublepoof.thesaves.Operations.Rename;
import doublepoof.thesaves.Operations.Search;
import doublepoof.thesaves.UtilClasses.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static doublepoof.thesaves.TheSaves.*;
import static doublepoof.thesaves.UtilClasses.GUIItems.*;

public class InventoryListener implements Listener {
    private static final Pattern judgeNum = Pattern.compile("[0-9]*");

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        String name = player.getName();
        String mainGUIname = name + "的存档";
        Inventory inventory = e.getInventory();

        if (inventory == null || e.getSlot() == -999) {
            return;
        }

        int size = inventory.getSize();

        // MainGUI部分
        if (mainGUIname.equals(inventory.getName())) {
            e.setCancelled(true);

            ItemStack itemStack = inventory.getItem(e.getSlot());
            if (itemStack == null) {
                return;
            }
            String displayName = itemStack.getItemMeta().getDisplayName();
            if ("§c没有上一页了".equals(displayName) && e.getSlot() == size - 8) {
                player.sendMessage("§4[§c!§4] §c没有上一页了");
            } else if ("§c没有下一页了".equals(displayName) && e.getSlot() == size - 2) {
                player.sendMessage("§4[§c!§4] §c没有下一页了");
            } else if ("§f搜索存档".equals(displayName) && e.getSlot() == size - 5) {
                player.closeInventory();
                Search.initialization(player);
            } else if ("§a上一页".equals(displayName) && e.getSlot() == size - 8) {
                ConfigurationSection fatherSection = SAVES.getConfigurationSection("Players." + player.getName());
                List<String> pSaves = new ArrayList<>(fatherSection.getKeys(false));
                int page = 1;
                for (int i = 0; i < pSaves.size(); i += size - 9) {
                    if (pSaves.get(i).equals(inventory.getItem(0).getItemMeta().getDisplayName())) {
                        page = (i / (size - 9)) + 1;
                        break;
                    }
                }
                MainGUI.initialization(player);
                MainGUI.setGUI(page - 1, player, fatherSection, pSaves);
                player.closeInventory();
                MainGUI.openGUI(player);
            } else if ("§a下一页".equals(displayName) && e.getSlot() == size - 2) {
                ConfigurationSection fatherSection = SAVES.getConfigurationSection("Players." + player.getName());
                List<String> pSaves = new ArrayList<>(fatherSection.getKeys(false));
                int page = 1;
                for (int i = 0; i < pSaves.size(); i += size - 9) {
                    if (pSaves.get(i).equals(inventory.getItem(0).getItemMeta().getDisplayName())) {
                        page = (i / (size - 9)) + 1;
                        break;
                    }
                }
                MainGUI.initialization(player);
                MainGUI.setGUI(page + 1, player, fatherSection, pSaves);
                player.closeInventory();
                MainGUI.openGUI(player);
            } else {
                ConfigurationSection saveTemp = SAVES.getConfigurationSection("Players." + player.getName() + "." + itemStack.getItemMeta().getDisplayName());
                boolean onGroundLimit = CONFIG.getBoolean("Load_OnGround");
                String freeze = CONFIG.getString("Loaded_DontMove");
                int freezeTime;
                String[] location = saveTemp.getString("location").split(",");

                if (onGroundLimit && !player.isOnGround()) {
                    player.sendMessage("§4[§c!§4] §c你需要站好才能传送");
                    return;
                }

                if (judgeNum.matcher(freeze).matches()) {
                    freezeTime = Integer.parseInt(freeze);
                } else if ("false".equalsIgnoreCase(freeze)) {
                    freezeTime = 0;
                } else {
                    throw new IllegalArgumentException(String.format("[TheSaves] config.yml中Loaded_DontMove的值应该是正整数或0或false！([%s])", freeze));
                }

                player.closeInventory();
                saveTemp = SAVES.getConfigurationSection("Players." + player.getName());
                saveTemp.set(itemStack.getItemMeta().getDisplayName(), null);

                getInstance().saveYamlConfiguration();

                double x = Double.parseDouble(location[0]);
                double y = Double.parseDouble(location[1]);
                double z = Double.parseDouble(location[2]);
                float pitch = Float.parseFloat(location[3]);
                float yaw = Float.parseFloat(location[4]);
                World world = Bukkit.getWorld(location[5]);
                player.teleport(new Location(world, x, y, z, pitch, yaw));
                PlayerStates.isFreezeMoving.add(player.getName());
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, freezeTime * 20, 0));

                // 延迟执行
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        PlayerStates.isFreezeMoving.remove(player.getName());
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.teleport(new Location(world, x, y, z, pitch, yaw));
                        List<String> operations = CONFIG.getStringList("Loaded_Operations");
                        performOperations(player, operations);
                    }
                };
                long delayMillis = freezeTime * 1000L;
                timer.schedule(task, delayMillis);
            }
        // CreateGUI部分
        } else if ("§a创建新的存档".equals(inventory.getName())) {
            e.setCancelled(true);

            ItemStack itemStack = inventory.getItem(e.getSlot());

            if(itemStack == null) {
                return;
            }

            String displayName = itemStack.getItemMeta().getDisplayName();
            if (e.getSlot() == 13) {
                List<String> desc = inventory.getItem(13).getItemMeta().getLore();
                if (e.getClick().isLeftClick()) {
                    String maxSize = CONFIG.getString("Save_Lore_Line_Limit");
                    int lineMaxSize;
                    if (!judgeNum.matcher(maxSize).matches() && !"false".equalsIgnoreCase(maxSize)) {
                        throw new IllegalArgumentException(String.format("[TheSaves] config.yml中Save_Lore_Line_Limit的值应该是正整数或0或false！([%s])", maxSize));
                    } else {
                        lineMaxSize = Integer.parseInt(maxSize);
                        // ADMIN能收到的警告部分
                        if (lineMaxSize >= 6 && player.hasPermission("TheSaves.admin")) {
                            player.sendMessage("§4[§c!§4] §cconfig.yml中Save_Lore_Line_Limit的值可能过大(" + maxSize + ")");
                        }
                    }
                    if ("false".equalsIgnoreCase(maxSize) || lineMaxSize == 0) {
                        player.sendMessage("§4[§c!§4] §c服务器不允许你为存档添加简介！");
                        return;
                    }

                    lineMaxSize -= 1;

                    if (desc != null && desc.size() > lineMaxSize) {
                        player.sendMessage("§4[§c!§4] §c这个存档的简介行数超过了上限！");
                        return;
                    }

                    Location location = player.getLocation();
                    PlayerStates.playerLocation.put(player.getName(), location);

                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 0));
                    PlayerStates.isFreezeMoving.add(player.getName());

                    player.closeInventory();
                    AddDesc.initialization(player);
                } else if (e.getClick().isRightClick()) {
                    if (desc == null) {
                        player.sendMessage("§4[§c!§4] §c这个存档已经没有简介了");
                        return;
                    }

                    desc.remove(desc.size() - 1);
                    ItemStack temp = inventory.getItem(13);
                    ItemMeta tempMeta = temp.getItemMeta();
                    tempMeta.setLore(desc);
                    temp.setItemMeta(tempMeta);
                    CreateGUI.setIcon(player, 13, temp);

                    player.closeInventory();
                    CreateGUI.openGUI(player);
                }
            } else if ("§e重命名".equals(displayName) && e.getSlot() == 28) {
                Location location = player.getLocation();
                PlayerStates.playerLocation.put(player.getName(), location);

                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 0));
                PlayerStates.isFreezeMoving.add(player.getName());

                player.closeInventory();

                Rename.initialization(player);
            } else if ("§b确认".equals(displayName) && e.getSlot() == 30) {
                boolean onGroundLimit = CONFIG.getBoolean("Save_OnGround");
                if (onGroundLimit && !player.isOnGround()) {
                    player.sendMessage("§4[§c!§4] §c你需要站好才能存档");
                    return;
                }

                boolean iconEnchanted = inventory.getItem(13).getEnchantments().containsKey(Enchantment.DAMAGE_ALL);

                List<String> desc = inventory.getItem(13).getItemMeta().getLore();

                if (desc == null) {
                    desc = new ArrayList<>();
                }

                String playerX = String.valueOf(player.getLocation().getX());
                String playerY = String.valueOf(player.getLocation().getY());
                String playerZ = String.valueOf(player.getLocation().getZ());
                String pitch = String.valueOf(player.getLocation().getPitch());
                String yaw = String.valueOf(player.getLocation().getYaw());
                String world = player.getWorld().getName();
                String location = playerX + "," + playerY + "," + playerZ + "," + yaw + "," + pitch + "," + world;
                CreateGUI.createNewConfiguration(player, inventory.getItem(13).getItemMeta().getDisplayName(), String.valueOf(inventory.getItem(13).getType()), iconEnchanted, desc, location);

                List<String> operations = CONFIG.getStringList("Saved_Operations");
                performOperations(player, operations);

                player.closeInventory();
            } else if ("§f图标无附魔光效".equals(displayName) && e.getSlot() == 32) {
                ItemStack temp = inventory.getItem(13);
                ItemMeta tempMeta = temp.getItemMeta();
                tempMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                temp.setItemMeta(tempMeta);
                CreateGUI.setIcon(player, 13, temp);
                CreateGUI.setIcon(player, 32, enchanted);

                player.closeInventory();
                CreateGUI.openGUI(player);
            } else if ("§d图标有附魔光效".equals(displayName) && e.getSlot() == 32) {
                ItemStack temp = inventory.getItem(13);
                ItemMeta tempMeta = temp.getItemMeta();
                tempMeta.removeEnchant(Enchantment.DAMAGE_ALL);
                temp.setItemMeta(tempMeta);
                CreateGUI.setIcon(player, 13, temp);
                CreateGUI.setIcon(player, 32, notEnchanted);

                player.closeInventory();
                CreateGUI.openGUI(player);
            } else if ("§a随机获取一个新的图标".equals(displayName) && e.getSlot() == 34) {
                boolean iconEnchanted = inventory.getItem(13).getEnchantments().containsKey(Enchantment.DAMAGE_ALL);
                CreateGUI.randomNewIcon(player, iconEnchanted);

                player.closeInventory();
                CreateGUI.openGUI(player);
            }
        }
    }

    @EventHandler
    public void onInventoryThrow(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();
        String mainGUIname = name + "的存档";
        Inventory inventory = e.getPlayer().getInventory();

        if (inventory == null) {
            return;
        }

        if (mainGUIname.equals(inventory.getName())) {
            e.setCancelled(true);
        } else if ("§a创建新的存档".equals(inventory.getName())) {
            e.setCancelled(true);
        }
    }
    private void performOperations(Player player, List<String> operations) {
        for(String s : operations) {
            String[] strings = s.split(" ");
            if("tell".equalsIgnoreCase(strings[0])) {
                player.sendMessage(strings[1].replaceAll("&", "§"));
            } else if("title".equalsIgnoreCase(strings[0])) {
                player.sendTitle(strings[1].replaceAll("&", "§"), "");
            } else if("player-command".equalsIgnoreCase(strings[0])) {
                player.performCommand(strings[1].replaceAll("/", ""));
            } else if("op-command".equalsIgnoreCase(strings[0])) {
                if(!player.isOp()) {
                    player.setOp(true);
                    player.performCommand(strings[1].replaceAll("/", ""));
                    player.setOp(false);
                } else {
                    player.performCommand(strings[1].replaceAll("/", ""));
                }
            } else if("console-command".equalsIgnoreCase(strings[0])) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), strings[1].replaceAll("/", ""));
            }
        }
    }
}
