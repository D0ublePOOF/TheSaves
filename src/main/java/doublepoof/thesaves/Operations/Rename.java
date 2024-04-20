package doublepoof.thesaves.Operations;

import doublepoof.thesaves.UtilClasses.PlayerStates;
import org.bukkit.entity.Player;

public class Rename {
    private static Player player;

    public static void initialization(Player player) {
        Rename.player = player;

        player.sendMessage("§e[§6*§e] §f请在聊天框输入你想要的存档名§7(输入cancel取消，§c不允许携带空格§7，输入&+数字/字母修改字体颜色/样式)：");

        PlayerStates.isRenaming.add(player.getName());
    }
}
