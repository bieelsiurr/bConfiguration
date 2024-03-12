package me.biiee3l.bconfig.config.utilities;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemStackUtils {

    public static String setItemStack(ItemStack itemStack){
        Material material = itemStack.getType();
        return material.name();
    }

    public static ItemStack getItemStack(String material){
        return new ItemStack(Material.valueOf(material));
    }
}
