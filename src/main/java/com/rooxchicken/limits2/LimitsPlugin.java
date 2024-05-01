package com.rooxchicken.limits2;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.CanExecuteFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.rooxchicken.limits2.Commands.LimitsGive;
import com.rooxchicken.limits2.Commands.ResetCooldown;
import com.rooxchicken.limits2.Items.IceSword;
import com.rooxchicken.limits2.Items.MoltenAxe;
import com.rooxchicken.limits2.Items.PuffBoots;
import com.rooxchicken.limits2.Items.SkulkShield;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

// import com.comphenix.protocol.PacketType;
// import com.comphenix.protocol.ProtocolLibrary;
// import com.comphenix.protocol.ProtocolManager;
// import com.comphenix.protocol.PacketType.Protocol;
// import com.comphenix.protocol.PacketType.Sender;
// import com.comphenix.protocol.events.ListenerPriority;
// import com.comphenix.protocol.events.PacketAdapter;
// import com.comphenix.protocol.events.PacketEvent;

// import io.netty.channel.Channel;


public class LimitsPlugin extends JavaPlugin implements Listener
{
    public static NamespacedKey puffBootsCooldownKey;
    public static NamespacedKey iceSwordCooldownKey;
    public static NamespacedKey skulkShieldCooldownKey;
    public static NamespacedKey moltenAxeCooldownKey;
    
    public static int scheduleScale = 4;

    private PuffBoots puffBootsHandler;
    private IceSword iceSwordHandler;
    private SkulkShield skulkShieldHandler;
    private MoltenAxe moltenAxeHandler;
    

    @Override
    public void onEnable()
    {
        puffBootsCooldownKey = new NamespacedKey(this, "limits_puffBootsCooldown");
        iceSwordCooldownKey = new NamespacedKey(this, "limits_iceSwordCooldown");
        skulkShieldCooldownKey = new NamespacedKey(this, "limits_skulkShieldCooldown");
        moltenAxeCooldownKey = new NamespacedKey(this, "limits_moltenAxeCooldown");
        
        puffBootsHandler = new PuffBoots(this);
        iceSwordHandler = new IceSword(this);
        skulkShieldHandler = new SkulkShield(this);
        moltenAxeHandler = new MoltenAxe(this);

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(puffBootsHandler, this);
        getServer().getPluginManager().registerEvents(iceSwordHandler, this);
        getServer().getPluginManager().registerEvents(skulkShieldHandler, this);
        getServer().getPluginManager().registerEvents(moltenAxeHandler, this);
        
        this.getCommand("limitsgive").setExecutor(new LimitsGive());
        this.getCommand("resetcooldown").setExecutor(new ResetCooldown());

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() // for item cooldown
        {
            public void run()
            {
                for(Player player : getServer().getOnlinePlayers())
                {
                    PersistentDataContainer container = player.getPersistentDataContainer();

                    String finalMessage = "";

                    String puffBootsMessage = puffBootsHandler.handlePuffBoots(player, container);
                    String iceSwordMessage = iceSwordHandler.handleFreezeTime(player, container);
                    String skulkShieldMessage = skulkShieldHandler.handleSkulkShield(player, container);
                    String moltenAxeMessage = moltenAxeHandler.handleMoltenAxe(player, container);

                    finalMessage = puffBootsMessage;
                    if(finalMessage != "" && iceSwordMessage != "")
                        finalMessage += " | " + iceSwordMessage;
                    else
                        finalMessage += iceSwordMessage;

                    if(finalMessage != "" && skulkShieldMessage != "")
                        finalMessage += " | " + skulkShieldMessage;
                    else
                        finalMessage += skulkShieldMessage;

                    if(finalMessage != "" && moltenAxeMessage != "")
                        finalMessage += " | " + moltenAxeMessage;
                    else
                        finalMessage += moltenAxeMessage;

                    if(finalMessage != "")
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(finalMessage));
                }
            }
        }, 0, 4);
        getLogger().info("Limits Season 2. In development since 1987 (made by roo)");
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event)
    {
        if(event.getItem() == null || !event.getItem().hasItemMeta())
            return;
    }

    public static Entity getTarget(Player player, int range)
    {
        List<Entity> nearbyE = player.getNearbyEntities(range, range, range);
        ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();

        for (Entity e : nearbyE) {
            if (e instanceof LivingEntity) {
                livingE.add((LivingEntity) e);
            }
        }

        Entity target = null;
        BlockIterator bItr = new BlockIterator(player, range);
        Block block;
        Location loc;
        int bx, by, bz;
        double ex, ey, ez;
        // loop through player's line of sight
        while (bItr.hasNext())
        {
            block = bItr.next();
            bx = block.getX();
            by = block.getY();
            bz = block.getZ();

            for (LivingEntity e : livingE)
            {
                loc = e.getLocation();
                ex = loc.getX();
                ey = loc.getY();
                ez = loc.getZ();
                if ((bx-.75 <= ex && ex <= bx+1.75) && (bz-.75 <= ez && ez <= bz+1.75) && (by-1 <= ey && ey <= by+2.5))
                {
                    // entity is close enough, set target and stop
                    target = e;
                    break;
                }
            }
        }

        return target;
    }
}