package doublepoof.thesaves.UtilClasses;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class BuildItem {
    public ItemStack makeItem(String material, String name, String[] desc, int amount, boolean enchanted) {
        ItemStack item;
        Pattern pattern = Pattern.compile("[0-9]*");
        if(pattern.matcher(material).matches()) {
            item = new ItemStack(Integer.parseInt(material), amount);
        } else {
            item = new ItemStack(Material.valueOf(material), amount);
        }

        ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);

        im.setLore(Arrays.asList(desc));

        if(enchanted) {
            im.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        }

        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(im);

        return item;
    }
}
