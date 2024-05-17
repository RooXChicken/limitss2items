package com.rooxchicken.limits2;

import java.util.ArrayList;
import java.util.List;
import com.google.common.base.Predicate;

import org.apache.commons.io.filefilter.CanExecuteFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
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
import org.bukkit.util.RayTraceResult;
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

                        
                    if(finalMessage != "" && moltenAxeMessage != "")
                        finalMessage += " | " + moltenAxeMessage;
                    else
                        finalMessage += moltenAxeMessage;
                        
                    if(finalMessage != "" && skulkShieldMessage != "")
                        finalMessage += " | " + skulkShieldMessage;
                    else
                        finalMessage += skulkShieldMessage;
                    
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

    public static Entity getTarget(final Player player, int range)
    {
        Predicate<Entity> p = new Predicate<Entity>()
        {
            @Override
            public boolean apply(Entity input)
            {
                return(input != player);
            }
            
        };
        RayTraceResult ray = player.getWorld().rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, p);
        
        if(ray != null)
            return ray.getHitEntity();
        else
            return null;
    }
}