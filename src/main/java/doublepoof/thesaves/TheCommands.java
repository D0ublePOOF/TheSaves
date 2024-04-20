package doublepoof.thesaves;

import doublepoof.thesaves.GUI.CreateGUI;
import doublepoof.thesaves.GUI.MainGUI;
import doublepoof.thesaves.Listeners.ChatListener;
import doublepoof.thesaves.UtilClasses.Permissions;
import doublepoof.thesaves.UtilClasses.Worlds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static doublepoof.thesaves.TheSaves.config;
import static doublepoof.thesaves.TheSaves.saves;

public class TheCommands implements CommandExecutor {
    static String[][] commandsHelp = {
            {"§e注：A|B代表选择A或者B输入，<...>代表必填项，[<...>]代表选填项，(...)代表默认值，{<...>}代表特殊情况下不填"},
            {"§f/ts help", "§f指令说明：展示§n你可用的§f所有指令及对应的示例，简单来说就是帮助页面", "§7例子：无"},
            {"§f/ts list", "§f指令说明：展示你的存档，点击图标读取存档", "§7例子：无"},
            {"§f/ts create|ct <存档名>", "§f指令说明：创建一个指定存档名的存档，§n存档名必须唯一", "§7例子：/ms ct TEST", "§7例子说明：创建一个存档名为TEST的存档"},
            {""},
            {"§f/ts reload|rl", "§f指令说明：重载该插件的所有配置文件", "§7例子：无"},
    };

    TheSaves theSaves = TheSaves.getInstance();
    CommandSender sender;
    Player player;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        sender = commandSender;
        player = theSaves.getServer().getPlayer(sender.getName());

        if(args.length == 1) {
            if("list".equalsIgnoreCase(args[0])) {
                if(saves.getConfigurationSection("Players." + player.getName()) == null) {
                    playerNotExisted();
                }

                if(!player.hasPermission("theSaves.default") && !player.hasPermission("theSaves.admin") && !player.hasPermission("theSaves.list")) {
                    noPermission();
                    return false;
                }

                for(String s : Worlds.load_illegalWorlds) {
                    if(player.getWorld().getName().equals(s)) {
                        player.sendMessage("§4[§c!§4] §c你在世界" + s + "中，不能进行读档");
                        return false;
                    }
                }
                for(String s : Permissions.load_exPermissions) {
                    if(!player.hasPermission(s)) {
                        player.sendMessage("§4[§c!§4] §c你需要" + s + "权限才能进行读档");
                        return false;
                    }
                }
                for(String s : Permissions.load_ilPermissions) {
                    if(player.hasPermission(s)) {
                        player.sendMessage("§4[§c!§4] §c你拥有" + s + "权限，不能进行读档");
                        return false;
                    }
                }

                ConfigurationSection fatherSection = saves.getConfigurationSection("Players." + player.getName());
                List<String> pSaves = new ArrayList<>(fatherSection.getKeys(false));
                MainGUI.initialization(player);
                MainGUI.setGUI(1, player, fatherSection, pSaves);
                MainGUI.openGUI(player);
            } else if("help".equalsIgnoreCase(args[0])) {
                if(!player.hasPermission("theSaves.default") && !player.hasPermission("theSaves.admin") && !player.hasPermission("theSaves.help")) {
                    noPermission();
                    return false;
                }

                helpInfo();
            } else if("reload".equalsIgnoreCase(args[0]) || "rl".equalsIgnoreCase(args[0])) {
                if(!player.hasPermission("theSaves.admin") && !player.hasPermission("theSaves.reload")) {
                    noPermission();
                    return false;
                }

                if(!theSaves.reload()) {
                    player.sendMessage("§4[§c!§4] §c在进行重载操作时出现了未知问题");
                    return false;
                }
                player.sendMessage("§2[§a√§2] §a已重载TheSaves插件");
            } else {
                player.sendMessage("§4[§c!§4] §c指令输入有误");
                helpInfo();
            }
        } else if(args.length == 2) {
            if(!player.hasPermission("theSaves.default") && !player.hasPermission("theSaves.admin") && !player.hasPermission("theSaves.create")) {
                noPermission();
                return false;
            }

            if("create".equalsIgnoreCase(args[0]) || "ct".equalsIgnoreCase(args[0])) {
                if(saves.getConfigurationSection("Players." + player.getName()) == null) {
                    playerNotExisted();
                }

                boolean onGroundLimit = config.getBoolean("Load_OnGround");
                if(onGroundLimit && !player.isOnGround()) {
                    player.sendMessage("§4[§c!§4] §c你需要站好才能存档");
                    return false;
                }

                for(String s : Worlds.save_illegalWorlds) {
                    if(player.getWorld().getName().equals(s)) {
                        player.sendMessage("§4[§c!§4] §c你在世界" + s + "中，不能进行存档");
                        return false;
                    }
                }
                for(String s : Permissions.save_exPermissions) {
                    if(!player.hasPermission(s)) {
                        player.sendMessage("§4[§c!§4] §c你需要" + s + "权限才能进行存档");
                        return false;
                    }
                }
                for(String s : Permissions.save_ilPermissions) {
                    if(player.hasPermission(s)) {
                        player.sendMessage("§4[§c!§4] §c你拥有" + s + "权限，不能进行存档");
                        return false;
                    }
                }

                if(ChatListener.isExisted(player, args[1])) {
                    player.sendMessage("§4[§c!§4] §c该存档名已存在");
                    return false;
                }

                String saveName = args[1];
                CreateGUI.initialization(player, saveName);
                CreateGUI.openGUI(player);
            } else {
                player.sendMessage("§4[§c!§4] §c指令输入有误");
                helpInfo();
            }
        } else {
            player.sendMessage("§4[§c!§4] §c指令输入有误");
            helpInfo();
        }

        return true;
    }

    private void playerNotExisted() {
        ConfigurationSection temp = saves.getConfigurationSection("Players");
        temp.createSection(player.getName());
        save();
    }

    private void noPermission() {
        player.sendMessage("§4[§c!§4] §c你没有权限");
    }

    private void helpInfo() {
        // 对应的顺序是权限节点.docx中的权限顺序！
        boolean[] permissions = new boolean[6];
        List<Integer> temp = new ArrayList<>();
        permissions[0] = player.hasPermission("theSaves.default");
        permissions[4] = player.hasPermission("TheSave.admin");
        if(permissions[0]) {
            permissions[1] = true;
            permissions[2] = true;
            permissions[3] = true;
        } else {
            permissions[1] = player.hasPermission("theSaves.help");
            permissions[2] = player.hasPermission("theSaves.list");
            permissions[3] = player.hasPermission("theSaves.create");
        }
        if(permissions[4]) {
            permissions[5] = true;
        } else {
            permissions[5] = player.hasPermission("theSaves.reload");
        }
        for(int i = 1; i < permissions.length; i++) {
            if(i == 4) {
                continue;
            }
            if(permissions[i]) {
                temp.add(i);
            }
        }
        player.sendMessage("§a=§2=§a=§2=§a=§2=§a=§2= §eTheSaves帮助 §2=§a=§2=§a=§2=§a=§2=§a=");
        player.sendMessage(commandsHelp[0][0]);
        for (Integer integer : temp) {
            for (int j = 0; j < commandsHelp[integer].length; j++) {
                player.sendMessage(commandsHelp[integer][j]);
            }
        }
    }

    private void save() {
        try {
            saves.save(new File(TheSaves.getInstance().getDataFolder(), "playerSaves.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
