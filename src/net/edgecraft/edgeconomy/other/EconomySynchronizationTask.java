package net.edgecraft.edgeconomy.other;

import net.edgecraft.edgeconomy.EdgeConomy;

import org.bukkit.scheduler.BukkitRunnable;

public class EconomySynchronizationTask extends BukkitRunnable {
	
	public EconomySynchronizationTask() { }
	
	@Override
	public void run() {
		
		EdgeConomy.log.info(EdgeConomy.ecobanner + "Starte Economy-Synchronisation..");
		EdgeConomy.getEconomy().synchronizeEconomy(true, true);
		EdgeConomy.getTransactions().syncTransactions();
		EdgeConomy.log.info(EdgeConomy.ecobanner + "Automatische Economy-Synchronisation abgeschlossen!");
		
	}
}
