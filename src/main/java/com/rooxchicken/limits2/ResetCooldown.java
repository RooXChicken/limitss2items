package com.rooxchicken.limits2;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ResetCooldown implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(!sender.isOp())
        {
            sender.sendMessage("You need to be OP silly!");
            return false;
        }

        if(sender instanceof Player)
        {
            Player player = (Player) sender;
            
            PersistentDataContainer container = player.getPersistentDataContainer();
            container.set(LimitsPlugin.puffBootsCooldownKey, PersistentDataType.INTEGER, 0);
            container.set(LimitsPlugin.iceSwordCooldownKey, PersistentDataType.INTEGER, 0);
            container.set(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER, 0);
            container.set(LimitsPlugin.moltenAxeCooldownKey, PersistentDataType.INTEGER, 0);
          }

        // ItemStack puffBoots = new ItemStack(Material.NETHERITE_BOOTS);
        // puffBoots.addEnchantment(Enchantment.DURABILITY, 3);
        // puffBoots.addEnchantment(Enchantment.MENDING, 1);
        // puffBoots.addEnchantment(Enchantment.PROTECTION_FALL, 4);
        // puffBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        // puffBoots.addEnchantment(Enchantment.SOUL_SPEED, 3);
        // puffBoots.addEnchantment(Enchantment.DEPTH_STRIDER, 3);

        // puffBoots.

        // ItemStack totem = new ItemStack(Material.TOTEM_OF_UNDYING);
        // totem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        // Bukkit.getServer().getPlayer(sender.getName()).getInventory().addItem(totem);
        return true;
    }

}
