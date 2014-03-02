package net.edgecraft.edgeconomy.other;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgecore.EdgeCoreAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EcoMonitorTask extends BukkitRunnable {
	
	public EcoMonitorTask() { }
	
	@Override
	public void run() {		
		
		EdgeConomy.log.info(EdgeConomy.ecobanner + "Starte PayDay..");
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p == null) continue;
			if (!EdgeCoreAPI.userAPI().exists(p.getName())) continue;
			
			EdgeConomy.getEcoMonitor().updateScoreboard(p.getName());
		}
		
		EdgeConomy.log.info(EdgeConomy.ecobanner + "PayDay erfolgreich abgeschlossen!");
	}
}
