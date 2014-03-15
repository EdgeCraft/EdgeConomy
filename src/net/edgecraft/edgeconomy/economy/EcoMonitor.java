package net.edgecraft.edgeconomy.economy;

import java.util.LinkedHashMap;
import java.util.Map;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.EdgeCuboidAPI;
import net.edgecraft.edgecuboid.cuboid.Habitat;
import net.edgecraft.edgecuboid.shop.ShopHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class EcoMonitor {
	
	public static Map<String, Scoreboard> ecoMonitor = new LinkedHashMap<>();	
	private static final EcoMonitor instance = new EcoMonitor();
	private final LanguageHandler lang = EdgeCoreAPI.languageAPI();
	
	private EcoMonitor() { /* ... */ }
	
	public static final EcoMonitor getInstance() {
		return instance;
	}
	
	public Map<String, Scoreboard> getEcoMonitors() {
		return EcoMonitor.ecoMonitor;
	}
	
	public int amountOfEcoMonitors() {
		return EcoMonitor.ecoMonitor.size();
	}
	
	/**
	 * Sets the eco monitor scoreboard for the given player
	 * @param player
	 * @return Scoreboard
	 */
	public void setScoreboard(final String player) {
		
		try {
			
			if (player == null) return;
			if (ecoMonitor.containsKey(player)) {
				updateScoreboard(player);
				return;
			}
			
			// Create need payday instances
			final BankAccount acc = Economy.getInstance().getAccount(player);
			final User user = EdgeCoreAPI.userAPI().getUser(player);
					
			// Create player
			final Player p = Bukkit.getPlayerExact(player);
			
			// Do not go further if user doesn't exist
			if (user == null) {
				return;
			}
			
			// Do not go further if account doesn't exist
			if (acc == null) {
				p.sendMessage(lang.getColoredMessage(user.getLanguage(), "global_payday_noaccount"));
				return;
			}
			
			// Create optional habitat
			final Habitat habitat = EdgeCuboidAPI.cuboidAPI().getHabitatByOwner(player);
			
			// Create method heart, the scoreboard
			Scoreboard monitor = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective obj = monitor.registerNewObjective("EcoMonitor", "dummy");
			
			// Set position and title of the scoreboard
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			obj.setDisplayName(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_title"));
			
			// Set payday score
			final Score paydayTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_payday_title")));
			final Score payday;
			if (!acc.isClosed())
				payday = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_payday_value").replace("[0]", acc.getPayday() + "")));
			else
				payday = obj.getScore(Bukkit.getOfflinePlayer("§cKonto geschlossen!"));
			
			paydayTitle.setScore(10);
			payday.setScore(9);
			acc.updateBalance(acc.getBalance() + acc.getPayday());
			
			// Set credit score
			final Score creditTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_credit_title")));
			final Score credit = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_credit_value").replace("[0]", acc.getCredit() + "")));
			creditTitle.setScore(8);
			credit.setScore(7);
			acc.updateBalance(acc.getBalance() - (acc.getCredit() / 100 * 0.2));
			
			// Set vehicle score
			final Score vehicleTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_vehicle_title")));
			final Score vehicle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_vehicle_value").replace("[0]", "##")));
			vehicleTitle.setScore(6);
			vehicle.setScore(5);
			
			// Set property score
			final double propertyTaxes = (habitat == null ? 0 : habitat.getTaxes()) + (ShopHandler.getInstance().getShop(p.getName()) == null ? 0 :  ShopHandler.getInstance().getShop(p.getName()).getTaxes());
			
			final Score propertyTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_property_title")));
			final Score property = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_property_value").replace("[0]", propertyTaxes + "")));
			propertyTitle.setScore(4);
			property.setScore(3);
			acc.updateBalance(acc.getBalance() - propertyTaxes);
			
			// Set state score
			final Score stateTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_state_title")));
			final Score state = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_state_value").replace("[0]", acc.getStateTaxes() + "")));
			stateTitle.setScore(2);
			state.setScore(1);
			acc.updateBalance(acc.getBalance() - acc.getStateTaxes());
			
			// Put/Overwrite the scoreboard into the map
			getEcoMonitors().put(player, monitor);	
			
			// Start payday
			p.sendMessage(lang.getColoredMessage(user.getLanguage(), "global_payday_start").replace("[0]", Economy.getState()));
			p.setScoreboard(monitor);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(EdgeConomy.getInstance(), new Runnable() {
				
				public void run() {
					// Reset scoreboard after 5 sec
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
				
			}, 20L * 10);
			
			p.sendMessage(lang.getColoredMessage(user.getLanguage(), "global_payday_success").replace("[0]", Economy.getState()).replace("[1]", Economy.getPaydayInterval() + ""));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the eco monitor scoreboard for the given player
	 * @param player
	 */
	public void updateScoreboard(String player) {
		try {
			
			if (player == null) return;
			if (!ecoMonitor.containsKey(player)) {
				setScoreboard(player);
				return;
			}
			
			// Create need payday instances
			final BankAccount acc = Economy.getInstance().getAccount(player);
			final User user = EdgeCoreAPI.userAPI().getUser(player);
					
			// Create player
			final Player p = Bukkit.getPlayerExact(player);
			
			// Do not go further if user doesn't exist
			if (user == null) {
				return;
			}
			
			// Do not go further if account doesn't exist
			if (acc == null) {
				p.sendMessage(lang.getColoredMessage(user.getLanguage(), "global_payday_noaccount"));
				return;
			}
			
			// Create optional habitat
			final Habitat habitat = EdgeCuboidAPI.cuboidAPI().getHabitatByOwner(player);
			
			// Create method heart, the scoreboard
			Scoreboard monitor = Bukkit.getScoreboardManager().getNewScoreboard();
			Objective obj = monitor.registerNewObjective("EcoMonitor", "dummy");
			
			// Set position and title of the scoreboard
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
			obj.setDisplayName(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_title"));
			
			// Set payday score
			final Score paydayTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_payday_title")));
			final Score payday;
			if (!acc.isClosed())
				payday = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_payday_value").replace("[0]", acc.getPayday() + "")));
			else
				payday = obj.getScore(Bukkit.getOfflinePlayer("§cKonto geschlossen!"));
			
			paydayTitle.setScore(10);
			payday.setScore(9);
			acc.updateBalance(acc.getBalance() + acc.getPayday());
			
			// Set credit score
			final Score creditTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_credit_title")));
			final Score credit = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_credit_value").replace("[0]", acc.getCredit() + "")));
			creditTitle.setScore(8);
			credit.setScore(7);
			acc.updateBalance(acc.getBalance() - (acc.getCredit() / 100 * 0.2));
			
			// Set vehicle score
			final Score vehicleTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_vehicle_title")));
			final Score vehicle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_vehicle_value").replace("[0]", "##")));
			vehicleTitle.setScore(6);
			vehicle.setScore(5);
			
			// Set property score
			final Score propertyTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_property_title")));
			final Score property = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_property_value").replace("[0]", habitat == null ? "0" : habitat.getTaxes() + "")));
			propertyTitle.setScore(4);
			property.setScore(3);
			acc.updateBalance(acc.getBalance() - (habitat == null ? 0 : habitat.getTaxes()));
			
			// Set state score
			final Score stateTitle = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_state_title")));
			final Score state = obj.getScore(Bukkit.getOfflinePlayer(lang.getColoredMessage(user.getLanguage(), "private_ecomonitor_state_value").replace("[0]", acc.getStateTaxes() + "")));
			stateTitle.setScore(2);
			state.setScore(1);
			acc.updateBalance(acc.getBalance() - acc.getStateTaxes());
			
			// Put/Overwrite the scoreboard into the map
			getEcoMonitors().put(player, monitor);	
			
			// Start payday
			p.sendMessage(lang.getColoredMessage(user.getLanguage(), "global_payday_start").replace("[0]", Economy.getState()));
			p.setScoreboard(monitor);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(EdgeConomy.getInstance(), new Runnable() {
				
				public void run() {
					// Reset scoreboard after 5 sec
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
				
			}, 20L * 10);
			
			p.sendMessage(lang.getColoredMessage(user.getLanguage(), "global_payday_success").replace("[0]", Economy.getState()).replace("[1]", Economy.getPaydayInterval() + ""));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
