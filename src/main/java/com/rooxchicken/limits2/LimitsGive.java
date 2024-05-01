package com.rooxchicken.limits2;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class LimitsGive implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!sender.isOp())
        {
            sender.sendMessage("You need to be OP silly!");
            return false;
        }

        Bukkit.getServer().dispatchCommand(sender, "give @s netherite_boots{display:{Name:'{\"text\":\"Puff Boots\",\"color\":\"white\",\"bold\":true,\"italic\":true}',Lore:['{\"text\":\"Allows you to double jump (double tab space)\",\"color\":\"gray\",\"bold\":true}']},Unbreakable:1b,Enchantments:[{id:\"minecraft:feather_falling\",lvl:4s},{id:\"minecraft:protection\",lvl:4s},{id:\"minecraft:depth_strider\",lvl:3s},{id:\"minecraft:soul_speed\",lvl:3s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:unbreaking\",lvl:3s}]} 1");
        Bukkit.getServer().dispatchCommand(sender, "give @s netherite_sword{display:{Name:'{\"text\":\"Ice Sword\",\"color\":\"aqua\",\"bold\":true,\"italic\":true}',Lore:['{\"text\":\"Allows you to freeze your\",\"color\":\"aqua\",\"bold\":true}','{\"text\":\"enemy for 5 seconds (right click)\",\"color\":\"aqua\",\"bold\":true}']},Unbreakable:1b,Enchantments:[{id:\"minecraft:looting\",lvl:3s},{id:\"minecraft:sharpness\",lvl:5s},{id:\"minecraft:sweeping\",lvl:3s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:unbreaking\",lvl:3s}]} 1");
        Bukkit.getServer().dispatchCommand(sender, "give @p netherite_axe{display:{Name:'{\"text\":\"Molten Axe\",\"color\":\"#FF7700\",\"bold\":true,\"italic\":true}',Lore:['{\"text\":\"Allows you to overcharge (right click)\",\"color\":\"#FF7700\",\"bold\":true}']},Unbreakable:1b,Enchantments:[{id:\"minecraft:sharpness\",lvl:5s},{id:\"minecraft:efficiency\",lvl:5s},{id:\"minecraft:silk_touch\",lvl:1s},{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:unbreaking\",lvl:3s}]} 1");
        Bukkit.getServer().dispatchCommand(sender, "give @p shield{display:{Name:'{\"text\":\"Skulk Shield\",\"color\":\"dark_blue\",\"bold\":true,\"italic\":true}',Lore:['{\"text\":\"Allows you to sonic blast (Shift+Right click)\",\"color\":\"dark_blue\",\"bold\":true}']},Unbreakable:1b,Enchantments:[{id:\"minecraft:mending\",lvl:1s},{id:\"minecraft:unbreaking\",lvl:3s}]} 1");

        return true;
    }

}
