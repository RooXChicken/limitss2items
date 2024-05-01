package com.rooxchicken.limits2.Items;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.rooxchicken.limits2.LimitsPlugin;

public class IceSword implements Listener
{
    private NamespacedKey freezeTimeKey;
    private NamespacedKey freezePosXKey;
    private NamespacedKey freezePosYKey;
    private NamespacedKey freezePosZKey;
    private NamespacedKey freezePosYawKey;
    private NamespacedKey freezePosPitchKey;

    private int freezeTaskID = -1;

    Plugin plugin;

    public IceSword(Plugin _plugin)
    {
        plugin = _plugin;

        freezeTimeKey = new NamespacedKey(plugin, "limits_freezeTime");
        freezePosXKey = new NamespacedKey(plugin, "limits_freezePosX");
        freezePosYKey = new NamespacedKey(plugin, "limits_freezePosY");
        freezePosZKey = new NamespacedKey(plugin, "limits_freezePosZ");
        freezePosYawKey = new NamespacedKey(plugin, "limits_freezePosYaw");
        freezePosPitchKey = new NamespacedKey(plugin, "limits_freezePosPitch");
    }

    private void handlePlayerFreeze(Player player, PersistentDataContainer container)
    {
        if(container.has(freezeTimeKey, PersistentDataType.INTEGER))
        {
            int time = container.get(freezeTimeKey, PersistentDataType.INTEGER);
            if(time > 0)
            {
                double x = container.get(freezePosXKey, PersistentDataType.DOUBLE);
                double y = container.get(freezePosYKey, PersistentDataType.DOUBLE);
                double z = container.get(freezePosZKey, PersistentDataType.DOUBLE);
                float yaw = container.get(freezePosYawKey, PersistentDataType.FLOAT);
                float pitch = container.get(freezePosPitchKey, PersistentDataType.FLOAT);

                player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
            }
            time--;
            container.set(freezeTimeKey, PersistentDataType.INTEGER, time);
        }
    }

    @EventHandler
    public void onUseSword(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR)
            return;
            
        if(event.getItem() == null || !event.getItem().hasItemMeta())
            return;

        if(event.getItem().getItemMeta().getDisplayName().equals("§b§l§oIce Sword"))
        {
            Player player = event.getPlayer();
            if(player.getPersistentDataContainer().has(LimitsPlugin.iceSwordCooldownKey, PersistentDataType.INTEGER))
                if(player.getPersistentDataContainer().get(LimitsPlugin.iceSwordCooldownKey, PersistentDataType.INTEGER) > 0)
                    return;

            Entity target = LimitsPlugin.getTarget(player, 6);
            if(target != null && target.getType() == EntityType.PLAYER)
            {
                player.getPersistentDataContainer().set(LimitsPlugin.iceSwordCooldownKey, PersistentDataType.INTEGER, 60*LimitsPlugin.scheduleScale);
                PersistentDataContainer container = target.getPersistentDataContainer();
                container.set(freezeTimeKey, PersistentDataType.INTEGER, 5*20);
                container.set(freezePosXKey, PersistentDataType.DOUBLE, target.getLocation().getX());
                container.set(freezePosYKey, PersistentDataType.DOUBLE, target.getLocation().getY());
                container.set(freezePosZKey, PersistentDataType.DOUBLE, target.getLocation().getZ());

                container.set(freezePosYawKey, PersistentDataType.FLOAT, target.getLocation().getYaw());
                container.set(freezePosPitchKey, PersistentDataType.FLOAT, target.getLocation().getPitch());

                player.getWorld().spawnParticle(Particle.SNOW_SHOVEL, target.getLocation().add(new Vector(0, 1, 0)), 75, 0.2f, 0.2f, 0.2f);
                player.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 1, 1);

                if(freezeTaskID == -1)
                {
                    freezeTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
                    {
                        public void run()
                        {
                            boolean cancel = true;
                            for(Player player : Bukkit.getServer().getOnlinePlayers())
                            {
                                PersistentDataContainer container = player.getPersistentDataContainer();
                                if(container.has(freezeTimeKey, PersistentDataType.INTEGER))
                                {
                                    if(container.get(freezeTimeKey, PersistentDataType.INTEGER) > 0)
                                    {
                                        cancel = false;
                                        handlePlayerFreeze(player, container);
                                    }
                                }
                            }

                            if(cancel)
                            {
                                Bukkit.getScheduler().cancelTask(freezeTaskID);
                                freezeTaskID = -1;
                            }

                        }
                    }, 0, 1);
                }
            }
        }
    }

    @EventHandler
    public void resetFreezeTimerOnDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();

        player.getPersistentDataContainer().set(freezeTimeKey, PersistentDataType.INTEGER, 0);
    }

    public String handleFreezeTime(Player player, PersistentDataContainer container)
    {
        String message = "";
        if(!container.has(LimitsPlugin.iceSwordCooldownKey, PersistentDataType.INTEGER))
            container.set(LimitsPlugin.iceSwordCooldownKey, PersistentDataType.INTEGER, 0);

        int iceSwordCooldown = container.get(LimitsPlugin.iceSwordCooldownKey, PersistentDataType.INTEGER);

        if(iceSwordCooldown > 0)
            iceSwordCooldown--;

        container.set(LimitsPlugin.iceSwordCooldownKey, PersistentDataType.INTEGER, iceSwordCooldown);
        
        ItemStack hand = player.getInventory().getItemInMainHand();
        if(hand != null && hand.hasItemMeta())
        {
            if(hand.getItemMeta().getDisplayName().equals("§b§l§oIce Sword"))
            {
                message = ChatColor.AQUA + "❄ ";
                if(iceSwordCooldown <= 0)
                    message += "READY";
                else
                    message += (iceSwordCooldown/LimitsPlugin.scheduleScale+1) + "s";
            }
        }

        return message;
    }
}
