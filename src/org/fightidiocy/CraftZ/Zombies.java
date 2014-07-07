package org.fightidiocy.CraftZ;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.World;

public class Zombies extends org.bukkit.plugin.java.JavaPlugin implements Listener
{
	Regions regions = null;
	ArrayList<Material> noSpawnMaterials = new ArrayList<Material>();
	
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this);
		regions = new Regions(this.getDataFolder().getAbsolutePath() + "\\Zombies.db3", this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new ZombieSpeed(getServer()), 1L, 20L);
		
		//Stairs		
		noSpawnMaterials.add(Material.BIRCH_WOOD_STAIRS);
		noSpawnMaterials.add(Material.BRICK_STAIRS);
		noSpawnMaterials.add(Material.COBBLESTONE_STAIRS);
		noSpawnMaterials.add(Material.JUNGLE_WOOD_STAIRS);
		noSpawnMaterials.add(Material.NETHER_BRICK_STAIRS);
		noSpawnMaterials.add(Material.SANDSTONE_STAIRS);
		noSpawnMaterials.add(Material.SMOOTH_STAIRS);
		noSpawnMaterials.add(Material.SPRUCE_WOOD_STAIRS);
		noSpawnMaterials.add(Material.WOOD_STAIRS);
		
		//Steps
		noSpawnMaterials.add(Material.DOUBLE_STEP);
		noSpawnMaterials.add(Material.STEP);
		noSpawnMaterials.add(Material.WOOD_DOUBLE_STEP);
		noSpawnMaterials.add(Material.WOOD_STEP);
		
		//Fence
		noSpawnMaterials.add(Material.COBBLE_WALL);
		noSpawnMaterials.add(Material.IRON_FENCE);
		noSpawnMaterials.add(Material.FENCE);
		noSpawnMaterials.add(Material.FENCE_GATE);
		noSpawnMaterials.add(Material.NETHER_FENCE);
		
		//Transparent / Liquid
		noSpawnMaterials.add(Material.ICE);
		noSpawnMaterials.add(Material.GLASS);
		noSpawnMaterials.add(Material.GLOWSTONE);
		noSpawnMaterials.add(Material.LAVA);
		noSpawnMaterials.add(Material.LEAVES);
		noSpawnMaterials.add(Material.REDSTONE_LAMP_OFF);
		noSpawnMaterials.add(Material.REDSTONE_LAMP_ON);
		noSpawnMaterials.add(Material.THIN_GLASS);
		noSpawnMaterials.add(Material.WATER);
		noSpawnMaterials.add(Material.STATIONARY_LAVA);
		noSpawnMaterials.add(Material.STATIONARY_WATER);
		
		//Dangerous
		noSpawnMaterials.add(Material.CACTUS);
		noSpawnMaterials.add(Material.FIRE);
		
		//Other
		noSpawnMaterials.add(Material.BREWING_STAND);
		noSpawnMaterials.add(Material.CAULDRON);

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new SpawnMobs(getServer(), noSpawnMaterials, regions), 1L, 40L);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = null;
		
		if (cmd.getName().equalsIgnoreCase("craftz"))	{
			getLogger().info("CraftZ Loaded!");
			
			if (sender instanceof Player && ((Player) sender).isOp()) {
				player = (Player) sender;
				player.sendMessage("CraftZ Loaded!");
			}
		}
		else if (cmd.getName().equalsIgnoreCase("killall"))
		{
			if (args.length > 0)
			{
				if (sender instanceof Player && ((Player) sender).isOp()) {
					player = (Player) sender;
											
					if (args[0].equalsIgnoreCase("zombies"))
						RemoveMobs(player.getWorld(), EntityType.ZOMBIE);
				}
			}
		}
		else if (cmd.getName().equalsIgnoreCase("r"))
		{

			if (sender instanceof Player && ((Player) sender).isOp()) {
				player = (Player) sender;
				
				if (args.length < 1)
				{
					player.sendMessage("Looking for command type: /r [info] [list] [add] [remove] [addmeta] [removemeta]");
				}
				else if (args[0].equalsIgnoreCase("info"))
				{
					if (args.length > 1 && args[1] != null)
					{
						player.sendMessage(regions.getRegionData(args[1]));
					}
					else
					{
						player.sendMessage(regions.listRegionsInside(player.getLocation()));
					}
				}
				else if (args[0].equalsIgnoreCase("list"))
				{
					player.sendMessage(regions.listRegions());
				}
				else if (args[0].equalsIgnoreCase("add"))
				{
					if (args.length > 4 && args[1] != null && args[2] != null && args[3] != null && args[4] != null)
					{
						String RegionName = args[1];
						Integer Radius = Integer.decode(args[2]);
						Integer Height = Integer.decode(args[3]);
						Integer Depth = Integer.decode(args[4]);
						Integer X = player.getLocation().getBlockX();
						Integer Y = player.getLocation().getBlockY();
						Integer Z = player.getLocation().getBlockZ();
						String msg = regions.addRegion(RegionName, X - Radius, Y - Depth, Z - Radius, X + Radius, Y + Height, Z + Radius);
						
						player.sendMessage(msg);
					}
					else
					{
						player.sendMessage("Usage: /r add <RegionName> <radius (around player)> <height (above player)> <depth (below player)>");			
					}
				}
				else if (args[0].equalsIgnoreCase("remove"))
				{
					if (args.length > 1 && args[1] != null)
					{
						String RegionName = args[1];
						
						player.sendMessage(regions.removeRegion(RegionName));
					}
					else
					{
						player.sendMessage("Usage: /r remove <RegionName>");			
					}
				}
				else if (args[0].equalsIgnoreCase("addmeta"))
				{
					if (args.length > 3 && args[1] != null && args[2] != null && args[3] != null)
					{
						String RegionName = args[1];
						String Key = args[2];
						String Value = args[3];
						
						player.sendMessage(regions.addRegionMetaData(RegionName, Key, Value));
					}
					else
					{
						player.sendMessage("Usage: /r addmeta <RegionName> <MetaKey> <MetaValue>");			
					}
				}
				else if (args[0].equalsIgnoreCase("removemeta"))
				{
					if (args.length > 2 && args[1] != null && args[2] != null)
					{
						String RegionName = args[1];
						String Key = args[2];
						
						player.sendMessage(regions.removeRegionMetaData(RegionName, Key));
					}
					else
					{
						player.sendMessage("Usage: /r removemeta <RegionName> <MetaKey>");			
					}
				}
			}
		}
		
		return true;
    }
	
	public void RemoveMobs(World w, EntityType et)
	{
		for (Entity e : w.getEntities())
		{
			if (e.getType() == et)
				e.remove();
		}
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent evt)
	{
		//Snow balls should do 2.5 hearts of damage
		if (evt.getCause() == DamageCause.PROJECTILE)
		{
			if (evt.getDamager() instanceof Snowball)
			{
				Snowball s = (Snowball)evt.getDamager();
				
				if  (s.getShooter() instanceof Snowman && evt.getEntityType() == EntityType.ZOMBIE)
					evt.setDamage(15);
				else	
					evt.setDamage(5);
			}
		}	
		else if (evt.getCause() == DamageCause.ENTITY_ATTACK && evt.getDamager() instanceof Player && evt.getEntity() instanceof Player)
		{
			Player p = (Player)evt.getDamager();
			
			if (p.getItemInHand() != null)
			{
				if (p.getItemInHand().getType() == Material.PAPER)
				{
					evt.setCancelled(true);
					Integer itemCount = p.getItemInHand().getAmount() - 1;
					Integer health = p.getHealth() + 2;
					
					if (health == 22)
					{
						p.sendMessage("They don't have any wounds to mend.");
					}
					else
					{
						p.sendMessage("You mend their wounds with the bandage.");
						
						health = health > 20 ? 20 : health;
						
						if (itemCount < 1)
							p.setItemInHand(null);
						else
							p.getItemInHand().setAmount(itemCount);
						p.setHealth(health);
					}
				}
				else if (p.getItemInHand().getTypeId() == 351 && p.getItemInHand().getData().getData() == 15)
				{
					evt.setCancelled(true);
					Integer itemCount = p.getItemInHand().getAmount() - 1;
					Integer health = p.getHealth() + 2;
					
					if (health == 22)
					{
						p.sendMessage("They don't have any wounds to mend.");
					}
					else
					{
						p.sendMessage("You mend their wounds with the bandage.");
						
						health = health > 20 ? 20 : health;
						
						if (itemCount < 1)
							p.setItemInHand(null);
						else
							p.getItemInHand().setAmount(itemCount);
						p.setHealth(health);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent evt)
	{
		if (evt.getPlayer().getGameMode() == GameMode.CREATIVE)
		{ 	//builder events
			if (evt.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				if (evt.getPlayer().getItemInHand().getType() == Material.STRING)
				{ //Probe Spawn-ability
					evt.setCancelled(true);
					
					evt.getPlayer().sendMessage("This block CAN" + (noSpawnMaterials.contains(evt.getClickedBlock().getType()) ? "'T" : "") + " be spawned upon by mobs.");
				}
				if (evt.getPlayer().getItemInHand().getType() == Material.REDSTONE_TORCH_ON)
				{ //Probe Regions
					evt.setCancelled(true);
					
					evt.getPlayer().sendMessage(regions.listRegionsInside(evt.getClickedBlock().getLocation()));
				}
			}
		}
		else
		{	//player events
			Player p = evt.getPlayer();
			if (p.getItemInHand().getType() == Material.PAPER)
			{
				Integer itemCount = p.getItemInHand().getAmount() - 1;
				Integer health = p.getHealth() + 2;
				
				if (health > 21)
				{
					p.sendMessage("You don't have any wounds to mend.");
				}
				else
				{
					p.sendMessage("You mend your wounds with the bandage.");
					
					health = health > 20 ? 20 : health;
					
					if (itemCount < 1)
						p.setItemInHand(null);
					else
						p.getItemInHand().setAmount(itemCount);
					p.setHealth(health);
				}
			}
			else if (p.getItemInHand().getTypeId() == 351 && p.getItemInHand().getData().getData() == 15)
			{
				evt.setCancelled(true);
				Integer itemCount = p.getItemInHand().getAmount() - 1;
				Integer health = p.getHealth() + 6;
				
				if (health > 25)
				{
					p.sendMessage("You don't have any wounds to mend.");
				}
				else
				{
					p.sendMessage("You mend your wounds with the gauss.");
					
					health = health > 20 ? 20 : health;
					
					if (itemCount < 1)
						p.setItemInHand(null);
					else
						p.getItemInHand().setAmount(itemCount);
					p.setHealth(health);
				}
			}
			else
			{
				evt.setCancelled(true)
			}
		}
	}
	
	@EventHandler
	public void onCreatureSpawnEvent(CreatureSpawnEvent evt)
	{		
		if (!evt.isCancelled() && evt.getSpawnReason() != SpawnReason.CUSTOM)//evt.getEntityType() != EntityType.ZOMBIE)
		{
			switch (evt.getSpawnReason())
			{
				case BUILD_IRONGOLEM:
					break;
				case BUILD_SNOWMAN:					
					break;
				case SPAWNER_EGG:
					break;
				default:
					evt.setCancelled(true);
					break;
			}			 
		}
	}
	
	@EventHandler
	public void onEntityCombustEvent(EntityCombustEvent evt)
	{
		if (evt.getEntityType() == EntityType.ZOMBIE)
			evt.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent evt)
	{
		//snowmen and iron golems are invincible unless hit by creative-mode players
		if (evt.getEntityType() == EntityType.SNOWMAN || evt.getEntityType() == EntityType.IRON_GOLEM)
		{
			if (evt instanceof EntityDamageByEntityEvent)
			{
				Entity e = ((EntityDamageByEntityEvent)evt).getDamager();
				
				if (e instanceof Player && ((Player)e).getGameMode() == GameMode.CREATIVE)
				{
					//1 hit kill
					evt.setDamage(1000);
				}
				else
					evt.setCancelled(true);
			}
			else
			{
				evt.setCancelled(true);
			}
		}	
	}
		
	//Make sure ice/snow/farms don't change state
	@EventHandler
	public void onBlockFadeEvent (BlockFadeEvent evt)
	{
		if (evt.getBlock().getType() != Material.GRASS)
			evt.setCancelled(true);
	}
	
	//Disable the destruction of a block due to fire
	@EventHandler
	public void onBlockBurnEvent(BlockBurnEvent evt)
	{
		evt.setCancelled(true);
	}
	
	//disable fire spreading/lava/lightning/etc
	@EventHandler
	public void onBlockIgniteEvent(BlockIgniteEvent evt)
	{
		if (evt.getCause() != IgniteCause.FLINT_AND_STEEL || evt.getPlayer().getGameMode() != GameMode.CREATIVE)
			evt.setCancelled(true);
	}
	
	//disable health regeneration (from food saturation)
	@EventHandler
	public void onEntityRegainHealthEvent(EntityRegainHealthEvent evt)
	{
		if (evt.getRegainReason() == RegainReason.SATIATED)
			evt.setCancelled(true);
	}
	
	//disable block explosions
	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent evt)
	{
		evt.blockList().clear();
	}
	
	//mob drops
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent evt)
	{
		//no one should drop XP
		evt.setDroppedExp(0);
		
		if (evt.getEntityType() == EntityType.ZOMBIE)
		{
			evt.getDrops().clear();
			//drop 1 zombie flesh
			evt.getDrops().add(new ItemStack(367, 1));
			
			Zombie z = (Zombie)evt.getEntity();
			if (z.getKiller() != null)
			{
				z.getKiller().setLevel(z.getKiller().getLevel() + 1);
			}
		}
	}
}