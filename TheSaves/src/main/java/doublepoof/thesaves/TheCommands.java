package doublepoof.thesaves;

import doublepoof.thesaves.GUI.CreateGUI;
import doublepoof.thesaves.GUI.MainGUI;
import doublepoof.thesaves.Listeners.ChatListener;
import doublepoof.thesaves.UtilClasses.Permissions;
import doublepoof.thesaves.UtilClasses.Worlds;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static doublepoof.thesaves.TheSaves.*;

/**
 * @author DoublePOOF
 */
public class TheCommands implements CommandExecutor {
    private static final String[][] COMMANDS_HELP = {
            {"§f/ts help", "§f指令说明：展示§n你可用的§f所有指令及对应的示例，简单来说就是帮助页面", "§7例子：无"},
            {"§f/ts list", "§f指令说明：展示你的存档，点击图标读取存档", "§7例子：无"},
            {"§f/ts create|ct <存档名>", "§f指令说明：创建一个指定存档名的存档，§n存档名必须唯一", "§7例子：/ts ct TEST", "§7例子说明：创建一个存档名为TEST的存档"},
            {"§f/ts reload|rl", "§f指令说明：重载该插件的所有配置文件", "§7例子：无"},
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§4[§c!§4] §c无法在控制台使用TheSaves插件的命令！");
            return false;
        }

        Player player = (Player) sender;
        boolean hasPlayerPermission = player.hasPermission("TheSaves.default");
        boolean hasAdminPermission = player.hasPermission("TheSaves.admin");

        if(SAVES.getConfigurationSection("Players." + player.getName()) == null) {
            handleNonExistentPlayer(player);
        }
        ConfigurationSection fatherSection = SAVES.getConfigurationSection("Players." + player.getName());
        List<String> playerSaves = new ArrayList<>(fatherSection.getKeys(false));

        switch(args.length) {
            case 1: {
                if("list".equalsIgnoreCase(args[0])) {
                    if(!hasPlayerPermission && !hasAdminPermission && !player.hasPermission("TheSaves.list")) {
                        sendNoPermissionMessage(player);
                        return false;
                    }

                    boolean onGroundLimit = CONFIG.getBoolean("Load_OnGround");
                    if(onGroundLimit && !player.isOnGround()) {
                        player.sendMessage("§4[§c!§4] §c你需要站好才能读档");
                        return false;
                    }

                    if(!validatePlayer(player, "读档")) {
                        return false;
                    }

                    MainGUI.initialization(player);
                    MainGUI.setGUI(1, player, fatherSection, playerSaves);
                    MainGUI.openGUI(player);
                } else if("help".equalsIgnoreCase(args[0])) {
                    if(!hasPlayerPermission && !hasAdminPermission && !player.hasPermission("TheSaves.help")) {
                        sendNoPermissionMessage(player);
                        return false;
                    }

                    sendHelpInfo(player);
                } else if("reload".equalsIgnoreCase(args[0]) || "rl".equalsIgnoreCase(args[0])) {
                    if(!hasAdminPermission && !player.hasPermission("TheSaves.reload")) {
                        sendNoPermissionMessage(player);
                        return false;
                    }

                    getInstance().reload();
                    player.sendMessage("§2[§a√§2] §a已重载TheSaves插件");
                } else {
                    handleWrongCommand(player);
                    return false;
                }
                break;
            }
            case 2: {
                if("create".equalsIgnoreCase(args[0]) || "ct".equalsIgnoreCase(args[0])) {
                    if(!hasPlayerPermission && !hasAdminPermission && !player.hasPermission("TheSaves.create")) {
                        sendNoPermissionMessage(player);
                        return false;
                    }

                    boolean onGroundLimit = CONFIG.getBoolean("Save_OnGround");
                    if(onGroundLimit && !player.isOnGround()) {
                        player.sendMessage("§4[§c!§4] §c你需要站好才能存档");
                        return false;
                    }

                    if(!validatePlayer(player, "存档")) {
                        return false;
                    }

                    if(ChatListener.isExisted(player, args[1])) {
                        player.sendMessage("§4[§c!§4] §c该存档名已存在");
                        return false;
                    }

                    String saveName = args[1];
                    CreateGUI.initialization(player, saveName);
                    CreateGUI.openGUI(player);
                } else {
                    handleWrongCommand(player);
                    return false;
                }
                break;
            }
            default: {
                handleWrongCommand(player);
                return false;
            }
        }

        return true;
    }

    /**
     * 检测玩家是否在非法世界/没有所需权限/有非法权限的操作
     * @param player 检测玩家
     * @param action 玩家要进行的操作
     * @return 检测不通过返回false，否则返回true
     */
    private boolean validatePlayer(Player player, String action) {
        World playerCurrentWorld = player.getWorld();

        switch (action) {
            case "存档": {
                if(Worlds.save_illegalWorlds.contains(playerCurrentWorld.getName())) {
                    player.sendMessage("§4[§c!§4] §c你在世界" + playerCurrentWorld.getName() + "中，不能进行" + action);
                    return false;
                }

                for(String s : Permissions.save_ilPermissions) {
                    if(!player.hasPermission(s)) {
                        player.sendMessage("§4[§c!§4] §c你需要" + s + "权限才能进行" + action);
                        return false;
                    }
                }
                for(String s : Permissions.save_exPermissions) {
                    if(player.hasPermission(s)) {
                        player.sendMessage("§4[§c!§4] §c你拥有" + s + "权限，不能进行" + action);
                        return false;
                    }
                }
                break;
            }
            case "读档": {
                if(Worlds.load_illegalWorlds.contains(playerCurrentWorld.getName())) {
                    player.sendMessage("§4[§c!§4] §c你在世界" + playerCurrentWorld.getName() + "中，不能进行" + action);
                    return false;
                }

                for(String s : Permissions.load_exPermissions) {
                    if(!player.hasPermission(s)) {
                        player.sendMessage("§4[§c!§4] §c你需要" + s + "权限才能进行" + action);
                        return false;
                    }
                }
                for(String s : Permissions.load_ilPermissions) {
                    if(player.hasPermission(s)) {
                        player.sendMessage("§4[§c!§4] §c你拥有" + s + "权限，不能进行" + action);
                        return false;
                    }
                }
                break;
            }
            default: {}
        }

        return true;
    }

    private void handleNonExistentPlayer(Player player) {
        ConfigurationSection temp = SAVES.getConfigurationSection("Players");
        temp.createSection(player.getName());
    }

    private void handleWrongCommand(Player player) {
        sendWrongCommandMessage(player);
        sendHelpInfo(player);
    }

    private void sendWrongCommandMessage(Player player) {
        player.sendMessage("§4[§c!§4] §c指令输入有误");
    }

    private void sendNoPermissionMessage(Player player) {
        player.sendMessage("§4[§c!§4] §c你没有权限");
    }

    private void sendHelpInfo(Player player) {
        // 对应的顺序是权限节点.docx中的权限顺序！
        boolean hasPlayerPermission = player.hasPermission("TheSaves.default");
        boolean hasAdminPermission = player.hasPermission("TheSaves.admin");
        boolean[] permissions = new boolean[4];

        // =========== 玩家 ===========
        permissions[0] = hasPlayerPermission || player.hasPermission("TheSaves.help");
        permissions[1] = hasPlayerPermission || player.hasPermission("TheSaves.list");
        permissions[2] = hasPlayerPermission || player.hasPermission("TheSaves.create");

        // =========== 管理员指令 ===========
        permissions[3] = hasAdminPermission || player.hasPermission("TheSaves.reload");

        player.sendMessage("§a=§2=§a=§2=§a=§2=§a=§2= §eTheSaves帮助 §2=§a=§2=§a=§2=§a=§2=§a=");
        player.sendMessage("§e注：A|B代表选择A或者B输入，<...>代表必填项，[<...>]代表选填项，(...)代表默认值，{<...>}代表特殊情况下不填");
        for(int i = 0; i < permissions.length; i++) {
            if(permissions[i]) {
                for(int j = 0; j < COMMANDS_HELP[i].length; j++) {
                    player.sendMessage(COMMANDS_HELP[i][j]);
                }
            }
        }
    }
}
