package com.rooxchicken.limits2.Items;

import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.rooxchicken.limits2.LimitsPlugin;

public class PuffBoots implements Listener
{
    private Plugin plugin;

    public PuffBoots(Plugin _plugin)
    {
        plugin = _plugin;
    }

    private void checkForPuffBoots(Player player)
    {
        boolean allowFlight = false;
        ItemStack boots = player.getInventory().getBoots();
        if(boots != null && boots.hasItemMeta())
        {
            if(boots.getItemMeta().getDisplayName().equals("§f§l§oPuff Boots"))
                allowFlight = true;
        }
        
        player.setAllowFlight(allowFlight);
    }

    @EventHandler
    public void cancelFallIfBoots(EntityDamageEvent event)
    {
        if(event.getCause() != DamageCause.FALL || event.getEntityType() != EntityType.PLAYER || ((Player)event.getEntity()).getInventory().getBoots() == null)
            return;

        event.setCancelled(((Player)event.getEntity()).getInventory().getBoots().getItemMeta().getDisplayName().equals("§f§l§oPuff Boots"));
    }

    @EventHandler
    public void activateDoubleJump(PlayerToggleFlightEvent event)
    {
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        player.setFlying(false);
        ItemStack boots = player.getInventory().getBoots();
        if(boots == null || !boots.hasItemMeta())
            return;

        if(!boots.getItemMeta().getDisplayName().equals("§f§l§oPuff Boots"))
            return;

        PersistentDataContainer container = player.getPersistentDataContainer();
        if(!container.has(LimitsPlugin.puffBootsCooldownKey, PersistentDataType.INTEGER))
            container.set(LimitsPlugin.puffBootsCooldownKey, PersistentDataType.INTEGER, 0);

        int cooldown = container.get(LimitsPlugin.puffBootsCooldownKey, PersistentDataType.INTEGER);

        if(cooldown > 0)
            return;

        container.set(LimitsPlugin.puffBootsCooldownKey, PersistentDataType.INTEGER, 60*LimitsPlugin.scheduleScale);

        Vector direction = player.getLocation().getDirection();
        player.setVelocity(player.getVelocity().add(direction));

        player.getWorld().spawnParticle(Particle.SPELL, player.getLocation(), 100, 0.5f, 0.2f, 0.5f);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BIG_DRIPLEAF_FALL, 1, 1);

       // getServer().dispatchCommand(getServer().getConsoleSender(), "execute at " + player.getName() + " run particle minecraft:effect ~ ~ ~ 0.5 0.1 0.5 1 100");
        //getServer().dispatchCommand(getServer().getConsoleSender(), "execute at " + player.getName() + " run playsound minecraft:block.big_dripleaf.fall master @a");
        
    }

    public String handlePuffBoots(Player player, PersistentDataContainer container)
    {
        String message = "";
        checkForPuffBoots(player);
   
        if(!container.has(LimitsPlugin.puffBootsCooldownKey, PersistentDataType.INTEGER))
            container.set(LimitsPlugin.puffBootsCooldownKey, PersistentDataType.INTEGER, 0);

        int puffBootsCooldown = container.get(LimitsPlugin.puffBootsCooldownKey, PersistentDataType.INTEGER);

        if(puffBootsCooldown > 0)
        {
            puffBootsCooldown--;
            player.setAllowFlight(false);
        }

        container.set(LimitsPlugin.puffBootsCooldownKey, PersistentDataType.INTEGER, puffBootsCooldown);
        
        ItemStack boots = player.getInventory().getBoots();
        if(boots != null && boots.hasItemMeta())
        {
            if(boots.getItemMeta().getDisplayName().equals("§f§l§oPuff Boots"))
            {
                message = "☁ ";
                if(puffBootsCooldown <= 0)
                    message += "READY";
                else
                    message += (puffBootsCooldown/LimitsPlugin.scheduleScale+1) + "s";
            }
        }

        return message;
    }
}
