package org.fightidiocy.CraftZ;

import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ZombieSpeed implements Runnable {

	Server s;
	
	public ZombieSpeed(Server server)
	{
		s = server;
	}
	
	public void run()
	{
		for (Player p : s.getOnlinePlayers())
		{
			for (Entity e : p.getNearbyEntities(64, 64, 64))
			{
				if (e.getType() == EntityType.ZOMBIE)
				{
					Zombie z = (Zombie)e;
					
					if (!z.isBaby())
					{ //zombies and non-baby villagers
						PotionEffect pe = new PotionEffect(PotionEffectType.SPEED, 60, 2);
						//PotionEffect pe2 = new PotionEffect(PotionEffectType.SLOW, 60, 1);
						
						z.addPotionEffect(pe);
						//z.addPotionEffect(pe2);
					}
				}
			}
		}
	}
}
