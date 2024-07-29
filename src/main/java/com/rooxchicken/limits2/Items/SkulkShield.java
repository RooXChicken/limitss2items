package com.rooxchicken.limits2.Items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.entity.ExperienceOrb;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
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
            boolean check = false;
            ItemStack shield = ((Player)event.getDamager()).getInventory().getItemInOffHand();
            if(shield != null && shield.hasItemMeta())
                if(shield.getItemMeta().getDisplayName().equals("§1§l§oSkulk Shield"))
                    check = true;

            ItemStack sword = ((Player)event.getDamager()).getInventory().getItemInMainHand();
            if(sword != null && sword.hasItemMeta() && sword.getItemMeta().getDisplayName().equals("§4§l§oLimiter Sword"))
                check = true;


            if(check)
            {
                if(((Player)event.getDamager()).getPersistentDataContainer().has(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER))
                    if(((Player)event.getDamager()).getPersistentDataContainer().get(LimitsPlugin.skulkShieldCooldownKey, PersistentDataType.INTEGER) > 120*LimitsPlugin.scheduleScale-2)
                        event.setDamage(DamageModifier.ARMOR, 0);
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

        boolean cancel = true;
        
        ItemStack shield = player.getInventory().getItemInOffHand();
        if(shield != null && shield.hasItemMeta() && shield.getItemMeta().getDisplayName().equals("§1§l§oSkulk Shield"))
            cancel = false;

        if(cancel)
        {
            ItemStack sword = player.getInventory().getItemInMainHand();
            if(sword != null && sword.hasItemMeta() && sword.getItemMeta().getDisplayName().equals("§4§l§oLimiter Sword"))
                cancel = false;
        }

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
            Object[] nearbyE = getNearbyEntities(boom, 2);

            for (Object e : nearbyE)
            {
                if (e instanceof LivingEntity)
                {
                    if(e != player)
                        ((LivingEntity)e).damage(7, player);
                }
            }
            player.getWorld().spawnParticle(Particle.SONIC_BOOM, boom, 1, 0.05, 0.05, 0.05, 0);
        }

        Entity xp = player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
        ((ExperienceOrb)xp).setExperience(3);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1, 1);

       // getServer().dispatchCommand(getServer().getConsoleSender(), "execute at " + player.getName() + " run particle minecraft:effect ~ ~ ~ 0.5 0.1 0.5 1 100");
        //getServer().dispatchCommand(getServer().getConsoleSender(), "execute at " + player.getName() + " run playsound minecraft:block.big_dripleaf.fall master @a");
        
    }

    public static Object[] getNearbyEntities(Location where, int range)
    {
        return where.getWorld().getNearbyEntities(where, range, range, range).toArray();
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
        
        boolean check = false;
        boolean giveHealth = false;

        ItemStack shield = player.getInventory().getItemInOffHand();
        if(shield != null && shield.hasItemMeta())
            if(shield.getItemMeta().getDisplayName().equals("§1§l§oSkulk Shield"))
                check = true;

        for(ItemStack item : player.getInventory().getContents())
        {
            if(item != null && item.hasItemMeta() && (item.getItemMeta().getDisplayName().equals("§4§l§oLimiter Sword")))
                check = true;
        }

        if(check)
        {
            giveHealth = true;
            message = ChatColor.DARK_BLUE + "🛡 ";
            if(skulkShieldCooldown <= 0)
                message += "READY";
            else
                message += (skulkShieldCooldown/LimitsPlugin.scheduleScale+1) + "s";
        }

        if(giveHealth)
        {
            if(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() != 24.0)
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24.0);
        }
        else if(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() != 20.0)
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);

        return message;
    }
}
