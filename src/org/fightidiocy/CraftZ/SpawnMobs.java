package org.fightidiocy.CraftZ;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;


public class SpawnMobs implements Runnable {
	Server server;
	ArrayList<Material> noSpawnMaterials;
	Regions regions;
	
	public SpawnMobs(Server s, ArrayList<Material> noSpawn, Regions r)
	{
		server = s;
		noSpawnMaterials = noSpawn;
		regions = r;
	}
	
	public void run()
	{
		for (Player p : server.getOnlinePlayers())
		{
			for (Integer i = 0; i < 24; i++)
			{
				double angle = Math.random()*Math.PI*2;
				double radius = 24 + Math.floor(Math.random()*48);
				double x = p.getLocation().getX() + Math.floor(Math.cos(angle)*radius);
				double z = p.getLocation().getZ() + Math.floor(Math.sin(angle)*radius);
				World w = p.getWorld();
				Integer maxY = w.getHighestBlockYAt((int)x, (int)z);
				Boolean wasAir = true;
				Boolean was2Air = true;
				ArrayList<Integer> heights = new ArrayList<Integer>();

				//this.getServer().broadcastMessage(String.valueOf(maxY));
				
				for (Integer y = maxY; y > 3; y--)
				{
					if (w.getBlockAt((int)x, y, (int)z).isEmpty())
					{
						was2Air = wasAir;
						wasAir = true;
					}
					else
					{
						if (was2Air)
						{
							Block b = w.getBlockAt((int)x, y, (int)z);
							
							if (!noSpawnMaterials.contains(b.getType()))
							{
								heights.add(y);
								//this.getServer().broadcastMessage(String.valueOf(y) + ": " + b.getType().toString());
								
							}
						}
						
						wasAir = false;
						was2Air = false;
					}
				}	
				
				//this.getServer().broadcastMessage(String.valueOf(heights.size()));
				
				if (heights.size() > 0)
				{
					double y = heights.get((int)Math.floor(Math.random() * (heights.size())));
					//this.getServer().broadcastMessage(String.valueOf(y));
					
					Location l = new Location(w, x + 0.5, 0.0, z + 0.5);
					l.setY(y);
					
					Egg probe = w.spawn(l, Egg.class);

					y += 1.0;
					l.setY(y);
					
					Integer multiplier = 2;
					Integer distance = 24;
					
					ArrayList<String> values = regions.listMetaValues(l, "spawn-density");
					
					if (values.contains("none")){
						multiplier = 0;
					}
					else if (values.contains("low")){
						multiplier = 4;
						distance = 24;
					}
					else if (values.contains("medium")){
						multiplier = 6;
						distance = 24;
					}
					else if (values.contains("high")){
						multiplier = 8;
						distance = 24;
					}
					else if (values.contains("highest")){
						multiplier = 10;
						distance = 24;
					}
					
					if (!(probe.getNearbyEntities(distance, distance, distance).size() > 1 * multiplier))
					{
						Zombie zombie = w.spawn(l, Zombie.class);
						zombie.setVillager(false);
					}
					
					probe.remove();
				}
			}
		}
	}
}
