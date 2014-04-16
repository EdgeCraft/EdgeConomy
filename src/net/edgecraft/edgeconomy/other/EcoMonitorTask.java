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
		
		if (Bukkit.getOnlinePlayers().length <= 0)
			return;
		
		EdgeConomy.log.info(EdgeConomy.ecobanner + "Starte PayDay..");
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p == null) continue;
			if (!EdgeCoreAPI.userAPI().exists(p.getName())) continue;
			
			EdgeConomy.getEcoMonitor().manageEconomy(EdgeCoreAPI.userAPI().getUser(p.getUniqueId()));
			EdgeConomy.getEcoMonitor().showMonitor(EdgeCoreAPI.userAPI().getUser(p.getUniqueId()), EdgeCoreAPI.userAPI().getUser(p.getUniqueId()));
		}
		
		EdgeConomy.log.info(EdgeConomy.ecobanner + "PayDay erfolgreich abgeschlossen!");
	}
}
