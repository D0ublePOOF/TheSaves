package doublepoof.thesaves.UtilClasses;

import org.bukkit.inventory.ItemStack;

public class GUIItems {
    private GUIItems() {
    }

    public static ItemStack noPrev = BuildItem.makeItem("MINECART", "§c没有上一页了", new String[]{}, 1, false);
    public static ItemStack prev = BuildItem.makeItem("COMMAND_MINECART", "§a上一页", new String[]{}, 1, false);
    public static ItemStack search = BuildItem.makeItem("HOPPER", "§f搜索存档", new String[]{
            "§e[§6!§e]",
            "§f如果存档名带有英文字符，那么搜索时§a不严格限制大小写",
            "§7(存档名：\"Paper\"，可以搜索：\"paper\"或\"PAPER\")",
            "§e[§6!§e]",
            "§f如果你不记得存档的全名，可以尝试§a搜索部分字符",
            "§7(存档名：\"Paper\"，可以搜索：\"pa\")"
    }, 1, false);
    public static ItemStack noNext = BuildItem.makeItem("MINECART", "§c没有下一页了", new String[]{}, 1, false);
    public static ItemStack next = BuildItem.makeItem("COMMAND_MINECART", "§a下一页", new String[]{}, 1, false);

    public static ItemStack createGUItips = BuildItem.makeItem("SIGN", "§2[§aTIPS§2]", new String[]{"§f左键上方图标添加一行存档简介", "§f右键上方图标减少一行存档简介"}, 1, false);
    public static ItemStack rename = BuildItem.makeItem("ANVIL", "§e重命名",
            new String[]{"§e[§6*§e]", "§f命名时§c不允许携带空格§f，§c不允许与已有存档重名"}, 1, false);
    public static ItemStack check = BuildItem.makeItem("NETHER_STAR", "§b确认", new String[]{}, 1, true);
    public static ItemStack notEnchanted = BuildItem.makeItem("BOOK", "§f图标无附魔光效", new String[]{}, 1, false);
    public static ItemStack enchanted = BuildItem.makeItem("ENCHANTED_BOOK", "§d图标有附魔光效", new String[]{}, 1, false);
    public static ItemStack randomNewIcon = BuildItem.makeItem("MAP", "§a随机获取一个新的图标", new String[]{}, 1, false);
}
