package com.rooxchicken.limits2.Items;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.mol;

import com.rooxchicken.limits2.LimitsPlugin;

import org.bukkit.entity.Player;

public class MoltenAxe implements Listener
{
    private Plugin plugin;

    public MoltenAxe(Plugin _plugin)
    {
        plugin = _plugin;
    }

    @EventHandler
    public void addFireAspect(EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Player)
        {
            Player source = (Player)event.getDamager();
            ItemStack axe = source.getInventory().getItemInMainHand();
            if(axe != null && axe.hasItemMeta())
            {
                if(axe.getItemMeta().getDisplayName().equals("§x§F§F§7§7§0§0§l§oMolten Axe"))
                {
                    if(Math.random() < 0.25)
                        event.getEntity().setFireTicks(160);
                }
            }
        }
    }

    @EventHandler
    public void activateDoubleJump(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(event.getItem() == null || !event.getItem().hasItemMeta())
            return;

        if(player.isSneaking() && player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getItemMeta().getDisplayName().equals("§1§l§oSkulk Shield"))
            return;

        if(event.getItem().getItemMeta().getDisplayName().equals("§x§F§F§7§7§0§0§l§oMolten Axe"))
        {
            PersistentDataContainer container = player.getPersistentDataContainer();
            if(!container.has(LimitsPlugin.moltenAxeCooldownKey, PersistentDataType.INTEGER))
                container.set(LimitsPlugin.moltenAxeCooldownKey, PersistentDataType.INTEGER, 0);
    
            int cooldown = container.get(LimitsPlugin.moltenAxeCooldownKey, PersistentDataType.INTEGER);
    
            if(cooldown > 0)
                return;
    
            container.set(LimitsPlugin.moltenAxeCooldownKey, PersistentDataType.INTEGER, 180*LimitsPlugin.scheduleScale);

            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15*20, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15*20, 1));

            player.getWorld().strikeLightningEffect(player.getLocation());
            player.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 100, 0.1f, 0.1f, 0.1f, 0.2);
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1, 1);
        }

    }

    public String handleMoltenAxe(Player player, PersistentDataContainer container)
    {
        String message = "";
   
        if(!container.has(LimitsPlugin.moltenAxeCooldownKey, PersistentDataType.INTEGER))
            container.set(LimitsPlugin.moltenAxeCooldownKey, PersistentDataType.INTEGER, 0);

        int moltenAxeCooldown = container.get(LimitsPlugin.moltenAxeCooldownKey, PersistentDataType.INTEGER);

        if(moltenAxeCooldown > 0)
        {
            moltenAxeCooldown--;
        }

        container.set(LimitsPlugin.moltenAxeCooldownKey, PersistentDataType.INTEGER, moltenAxeCooldown);
        
        ItemStack axe = player.getInventory().getItemInMainHand();
        if(axe != null && axe.hasItemMeta())
        {
            if(axe.getItemMeta().getDisplayName().equals("§x§F§F§7§7§0§0§l§oMolten Axe"))
            {
                message = "⚡ ";
                if(moltenAxeCooldown <= 0)
                    message += "READY";
                else
                    message += (moltenAxeCooldown/LimitsPlugin.scheduleScale+1) + "s";
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1, 0, false));
        }

        return message;
    }
}
