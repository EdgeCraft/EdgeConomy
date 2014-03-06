package net.edgecraft.edgeconomy;

import java.util.logging.Logger;

import net.edgecraft.edgeconomy.commands.AccountCommand;
import net.edgecraft.edgeconomy.commands.CashCommand;
import net.edgecraft.edgeconomy.commands.CreditCommand;
import net.edgecraft.edgeconomy.commands.TransferCommand;
import net.edgecraft.edgeconomy.commands.WelfareCommand;
import net.edgecraft.edgeconomy.economy.EcoMonitor;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.other.ConfigHandler;
import net.edgecraft.edgeconomy.other.EcoMonitorTask;
import net.edgecraft.edgeconomy.other.EconomyCommands;
import net.edgecraft.edgeconomy.other.EconomySynchronizationTask;
import net.edgecraft.edgeconomy.transactions.TransactionManager;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.CommandHandler;
import net.edgecraft.edgecore.mod.ModCommand;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class EdgeConomy extends JavaPlugin {
	
	public static final String ecobanner = "[EdgeConomy] ";
	
	public static final Logger log = EdgeCore.log;
	private static EdgeConomy instance;
	
	protected static final Economy economy = Economy.getInstance();
	protected static final TransactionManager transaction = TransactionManager.getInstance();
	protected static final EcoMonitor ecoMonitor = EcoMonitor.getInstance();
	
	private final CommandHandler commands = EdgeCoreAPI.commandsAPI();
	private final ConfigHandler config = ConfigHandler.getInstance( this );
	
	/**
	 * Is used when the plugin is going to shut down
	 */
	public void onDisable() {
		economy.synchronizeEconomy(true, true);
		log.info(ecobanner + "Plugin wurde erfolgreich beendet!");
	}
	
	/**
	 * Is used when the plugin starts up
	 */
	public void onEnable() {
		registerData();
		log.info(ecobanner + "Plugin wurde erfolgreich gestartet!");
	}
	
	/**
	 * Is used before onEnable(), e.g. to pre-load needed functions
	 */
	public void onLoad() {
		instance = this;
		
		this.config.loadConfig();
		this.config.update();
		
		economy.synchronizeEconomy(true, true);
	}
	
	/**
	 * Registers data the plugin will use
	 */
	private void registerData() {
		
		commands.registerCommand( new CommandCollection( EconomyCommands.getInstance() ) );
		
		@SuppressWarnings("unused") BukkitTask ecoTask = new EconomySynchronizationTask().runTaskTimer(this, 0, 20L * 60 * 10);
		@SuppressWarnings("unused") BukkitTask monitorTask = new EcoMonitorTask().runTaskTimer(this, 0, 20L * 60 * Economy.getPaydayInterval());
	}
	
	/**
	 * Returns an instance of this class
	 * @return EdgeConomy
	 */
	public static EdgeConomy getInstance() {
		return instance;
	}
	
	/**
	 * Returns the EconomyAPI which is instantiated in this class
	 * @return Economy
	 */
	public static Economy getEconomy() {
		return EdgeConomy.economy;
	}
	
	/**
	 * Returns the TransactionAPI which is instantiated in this class
	 * @return TransactionManager
	 */
	public static TransactionManager getTransactions() {
		return EdgeConomy.transaction;
	}
	
	/**
	 * Returns the EcoMonitor Instance which is instantiated in this class
	 * @return EcoMonitor
	 */
	public static EcoMonitor getEcoMonitor() {
		return EdgeConomy.ecoMonitor;
	}
}
