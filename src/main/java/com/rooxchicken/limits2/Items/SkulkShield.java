package com.rooxchicken.limits2.Items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Vibration;
import org.bukkit.Vibration.Destination;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.rooxchicken.limits2.LimitsPlugin;

import org.bukkit.entity.Player;

public class SkulkShield implements Listener
{
    private Plugin plugin;

    public SkulkShield(Plugin _plugin)
    {
        plugin = _plugin;
    }

    @EventHandler
    public void bypassArmorForBlast(EntityDamageByEntityEvent event)
    {
        if(event.getDamager() instanceof Player)
        {
            ItemStack shield = ((Player)event.getDamager()).getInventory().getItemInOffHand();
            if(shield != null && shield.hasItemMeta())
            {
                if(shield.getItemMeta().getDisplayName().equals("§1§l§oSkulk Shield"))
                {
                    if(((Player)event.getDamager()).getPersistentDataContainer().has(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER))
                        if(((Player)event.getDamager()).getPersistentDataContainer().get(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER) > 120*LimitsPlugin.scheduleScale-2)
                            event.setDamage(DamageModifier.ARMOR, 0);
                }
            }
        }
    }

    @EventHandler
    public void activateSkulkShield(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();

        if(!player.isSneaking())
            return;
        
        ItemStack shield = player.getInventory().getItemInOffHand();
        if(shield == null || !shield.hasItemMeta())
            return;

        if(!shield.getItemMeta().getDisplayName().equals("§1§l§oSkulk Shield"))
            return;

        PersistentDataContainer container = player.getPersistentDataContainer();
        if(!container.has(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER))
            container.set(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER, 0);

        int cooldown = container.get(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER);

        if(cooldown > 0)
            return;

        container.set(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER, 120*LimitsPlugin.scheduleScale);

        player.getWorld().spawnParticle(Particle.SCULK_SOUL, player.getLocation(), 50, 0.5f, 0.2f, 0.5f, 0.1);

        for(int i = 0; i < 20; i++)
        {
            Location boom = player.getLocation().add(player.getLocation().getDirection().multiply(i/2.0)).add(0, 1, 0);
            List<Entity> nearbyE = getNearbyEntities(boom, 1);
            ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();

            for (Entity e : nearbyE)
            {
                if (e instanceof LivingEntity)
                {
                    if(e != player)
                        ((LivingEntity)e).damage(7, player);
                }
            }
            player.getWorld().spawnParticle(Particle.SONIC_BOOM, boom, 1, 0.05, 0.05, 0.05, 0);
        }

        player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
        player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1, 1);

       // getServer().dispatchCommand(getServer().getConsoleSender(), "execute at " + player.getName() + " run particle minecraft:effect ~ ~ ~ 0.5 0.1 0.5 1 100");
        //getServer().dispatchCommand(getServer().getConsoleSender(), "execute at " + player.getName() + " run playsound minecraft:block.big_dripleaf.fall master @a");
        
    }

    public static List<Entity> getNearbyEntities(Location where, int range)
    {
        List<Entity> found = new ArrayList<Entity>();
         
        for (Entity entity : where.getWorld().getEntities())
        {
            if (isInBorder(where, entity.getLocation(), range))
                found.add(entity);
        }

        return found;
    }

    public static boolean isInBorder(Location center, Location notCenter, int range)
    {
        int x = center.getBlockX(), z = center.getBlockZ();
        int x1 = notCenter.getBlockX(), z1 = notCenter.getBlockZ();
            
        if (x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range))
        {
            return false;
        }

        return true;
    }

    public String handleSkulkShield(Player player, PersistentDataContainer container)
    {
        String message = "";
   
        if(!container.has(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER))
            container.set(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER, 0);

        int skulkShieldCooldown = container.get(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER);

        if(skulkShieldCooldown > 0)
            skulkShieldCooldown--;

        container.set(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER, skulkShieldCooldown);
        
        ItemStack shield = player.getInventory().getItemInOffHand();
        if(shield != null && shield.hasItemMeta())
        {
            if(shield.getItemMeta().getDisplayName().equals("§1§l§oSkulk Shield"))
            {
                message = "🛡 ";
                if(skulkShieldCooldown <= 0)
                    message += "READY";
                else
                    message += (skulkShieldCooldown/LimitsPlugin.scheduleScale+1) + "s";

                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24.0);
            }
            else
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        }
        else
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);

        return message;
    }
}
