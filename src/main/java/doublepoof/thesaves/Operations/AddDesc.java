package doublepoof.thesaves.Operations;

import doublepoof.thesaves.UtilClasses.PlayerStates;
import org.bukkit.entity.Player;

public class AddDesc {
    private static Player player;

    public static void initialization(Player player) {
        AddDesc.player = player;

        player.sendMessage("§e[§6*§e] §f请在聊天框输入你想要添加的简介§7(输入cancel取消，输入&+数字/字母修改字体颜色/样式)，§c不允许超过64个字符§7：");

        PlayerStates.isAddingDesc.add(player.getName());
    }
}
