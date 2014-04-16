package net.edgecraft.edgeconomy.economy;

import java.util.LinkedHashMap;
import java.util.Map;

import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class EcoMonitor {
	
	private final Map<User, Scoreboard> monitors = new LinkedHashMap<>();
	private final LanguageHandler lang = EdgeCoreAPI.languageAPI();
	
	private static final EcoMonitor instance = new EcoMonitor();
	
	private EcoMonitor() { /* ... */ }
	
	public static final EcoMonitor getInstance() {
		return instance;
	}
	
	public Map<User, Scoreboard> getMonitors() {
		return monitors;
	}
	
	public int amountOfMonitors() {
		return monitors.size();
	}
	
	/**
	 * Shows the economy monitor
	 * @param user
	 */
	@SuppressWarnings("deprecation")
	public void showMonitor(final User target, final User receiver) {
		if (target == null)
			return;
		
		try {
			
			final BankAccount acc = Economy.getInstance().getAccount(target);
			final Player player = receiver.getPlayer();
			
			if (acc == null) {
				player.sendMessage(lang.getColoredMessage(target.getLang(), "global_payday_noaccount"));
				return;
			}
			
			// Create the monitor heart: a scoreboard and an objective
			Scoreboard monitor = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective obj = monitor.registerNewObjective("Eco Monitor", "dummy");
			
			// Set position and title
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			obj.setDisplayName(lang.getColoredMessage(target.getLang(), "private_ecomonitor_title"));
			
			// Check for account welfare
			double welfare = acc.hasWelfare() ? Economy.getWelfareAmount() : 0.0D;
			
			// Set PayDay Score
			final Score paydayTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLang(), "private_ecomonitor_payday_title")));
			final Score payday;
			
			if (acc.isClosed())
				payday = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLanguage(), "acc_closed")));
			else
				payday = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLang(), "private_ecomonitor_payday_value").replace("[0]", (acc.getPayday() + welfare) + "")));
			
			paydayTitle.setScore(10);
			payday.setScore(9);
			
			// Set Credit Score
			final Score creditTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLang(), "private_ecomonitor_credit_title")));
			final Score credit = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLang(), "private_ecomonitor_credit_value").replace("[0]", acc.getCreditTax() + "")));
			creditTitle.setScore(8);
			credit.setScore(7);
			
			// Set Vehicle Score
			final Score vehicleTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLang(), "private_ecomonitor_vehicle_title")));
			final Score vehicle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLang(), "private_ecomonitor_vehicle_value").replace("[0]", acc.getVehicleTax() + "")));
			vehicleTitle.setScore(6);
			vehicle.setScore(5);
			
			// Set Property Score
			final Score propertyTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLang(), "private_ecomonitor_property_title")));
			final Score property = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLang(), "private_ecomonitor_property_value").replace("[0]", acc.getPropertyTax() + "")));
			propertyTitle.setScore(4);
			property.setScore(3);
			
			// Set State Score
			final Score stateTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLang(), "private_ecomonitor_state_title")));
			final Score state = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(target.getLang(), "private_ecomonitor_state_value").replace("[0]", acc.getStateTax() + "")));
			stateTitle.setScore(2);
			state.setScore(1);
			
			// Add / Overwrite the scoreboard in the Map
			getMonitors().put(target, monitor);
			
			// Show scoreboard
			player.setScoreboard(monitor);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(EdgeCore.getInstance(), new Runnable() {
				
				public void run() {
					// Reset scoreboard after 10 seconds
					player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
				
			}, 20L * 10);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Manages the account internal economy for the payday (including taxes and payday (+ welfare))
	 * @param user
	 */
	public void manageEconomy(final User user) {
		if (user == null)
			return;
		
		try {
			
			// Manage account
			final BankAccount acc = Economy.getInstance().getAccount(user);
			
			if (acc == null)
				return;
			
			// Check for welfare
			double welfare = acc.hasWelfare() ? Economy.getWelfareAmount() : 0.0D;
			
			// Calculate everything together
			double payday = acc.getPayday() + welfare;
			double taxes = acc.getCreditTax() + acc.getVehicleTax() + acc.getPropertyTax() + acc.getStateTax();
			
			acc.updateBalance(acc.getBalance() + payday);
			acc.updateBalance(acc.getBalance() - taxes);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
