package net.edgecraft.edgeconomy.other;

import java.io.File;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgecore.EdgeCore;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigHandler {
	
	private EdgeConomy plugin;
	private FileConfiguration config;
	
	protected static final ConfigHandler instance = new ConfigHandler();
	
	protected ConfigHandler() { /* ... */ }
	
	public static final ConfigHandler getInstance(EdgeConomy plugin) {
		instance.setPlugin(plugin);
		return instance;
	}
	
	/**
	 * Loads the config of the EdgeConomy-Instance
	 */
	public void loadConfig() {
		
		// Config itself
		setConfig(getPlugin().getConfig());
		
		// General
		getConfig().addDefault("General.IntervalInMinutes", 25);
		getConfig().addDefault("General.AllowAccounts", true);
		getConfig().addDefault("General.AutoCreateAccounts", true);	
		getConfig().addDefault("General.MaxCashDistance", 5);
		getConfig().addDefault("General.MaxATMAmount", 2000.0D);
		getConfig().addDefault("General.MonitoredAmount", 3500.0D);
		
		
		// Economy
		getConfig().addDefault("Economy.State", "EdgeCraft");		
		getConfig().addDefault("Economy.DefaultCash", 0.0D);
		getConfig().addDefault("Economy.DefaultWelfare", 400.0D);
		getConfig().addDefault("Economy.MaxWelfareBalance", 3500.0D);	
		
		// Economy Fees
		getConfig().addDefault("Economy.TransferFee", 0.1D);
		getConfig().addDefault("Economy.WithdrawalFee", 0.1D);
		getConfig().addDefault("Economy.CreditFee", 0.4D);
		getConfig().addDefault("Economy.PaydayBonus", 0.2D);
		getConfig().addDefault("Economy.StateTax", 0.6D);
		
		getConfig().options().copyDefaults(true);
		getPlugin().saveConfig();
		
		if (!new File(getPlugin().getDataFolder() + "src" + File.pathSeparator + "config.yml").exists());
			getPlugin().saveDefaultConfig();
	}
	
	/**
	 * Updates all local settings using the configuration
	 * @param instance
	 */
	public final void update() {
		
		Economy.setAllowAccounts(getConfig().getBoolean("General.AllowAccounts"));
		Economy.setAutoCreateAccounts(getConfig().getBoolean("General.AutoCreateAccounts"));
		
		Economy.setPaydayInterval(getConfig().getInt("General.IntervalInMinutes"));
		Economy.setMaxCashDistance(getConfig().getInt("General.MaxCashDistance"));
		Economy.setMaxATMAmount(getConfig().getDouble("General.MaxATMAmount"));
		Economy.setMonitoredAmount(getConfig().getDouble("General.MonitoredAmount"));
		
		Economy.setState(getConfig().getString("Economy.State"));
		Economy.setCurrency(EdgeCore.getCurrency());
		Economy.setDefaultCash(getConfig().getDouble("Economy.DefaultCash"));
		Economy.setDefaultWelfare(getConfig().getDouble("Economy.DefaultWelfare"));
		Economy.setMaxWelfareBalance(getConfig().getDouble("Economy.MaxWelfareBalance"));
		
		Economy.setTransferFee(getConfig().getDouble("Economy.TransferFee"));
		Economy.setWithdrawalFee(getConfig().getDouble("Economy.WithdrawalFee"));
		Economy.setCreditFee(getConfig().getDouble("Economy.CreditFee"));
		Economy.setPaydayBonus(getConfig().getDouble("Economy.PaydayBonus"));
		Economy.setStateTax(getConfig().getDouble("Economy.StateTax"));
		
	}
	
	/**
	 * Returns the used EdgeConomy-Instance
	 * @return EdgeConomy
	 */
	private EdgeConomy getPlugin() {
		return plugin;
	}
	
	/**
	 * Returns the used FileConfiguration
	 * @return FileConfiguration
	 */
	public FileConfiguration getConfig() {
		return config;
	}
	
	/**
	 * Sets the used EdgeConomy-Instance
	 * @param instance
	 */
	protected void setPlugin(EdgeConomy instance) {
		if (instance != null)
			plugin = instance;
	}
	
	/**
	 * Sets the used FileConfiguration
	 * @param config
	 */
	protected void setConfig(FileConfiguration config) {
		if (config != null)
			this.config = config;
	}
}
