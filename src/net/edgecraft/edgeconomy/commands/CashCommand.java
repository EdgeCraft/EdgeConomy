package net.edgecraft.edgeconomy.commands;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.economy.EconomyPlayer;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CashCommand extends AbstractCommand {

	private final LanguageHandler lang = EdgeCore.getLang();
	
	@Override
	public Level getLevel() {
		return Level.USER;
	}

	@Override
	public String[] getNames() {
		String[] names = { "cash" };
		return names;
	}
	
	@Override
	public boolean validArgsRange(String[] args) {
		return (args.length > 1 && args.length < 6);
	}
	
	@Override
	public void sendUsage(CommandSender sender) {
		if (sender instanceof Player) {
			
			User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
			
			if (u != null) {
				
				if (!Level.canUse(u, getLevel())) return;
				
				sender.sendMessage(EdgeCore.usageColor + "/cash info");
				sender.sendMessage(EdgeCore.usageColor + "/cash give <user> <amount>");
				sender.sendMessage(EdgeCore.usageColor + "/cash donate <amount>");
				
				if (!Level.canUse(u, Level.ADMIN)) return;
				
				sender.sendMessage(EdgeCore.usageColor + "/cash update <user> <amount>");
				sender.sendMessage(EdgeCore.usageColor + "/cash transfer <from> <to> <amount>");
				sender.sendMessage(EdgeCore.usageColor + "/cash reload [<user>]");
				
			}				
		}
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLanguage();
		
		if (args[1].equalsIgnoreCase("info")) {
			
			if (!Level.canUse(user, Level.USER)) {
				player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
				return true;
			}
			
			EconomyPlayer ep = EdgeConomy.getEconomy().getEconomyPlayer(user.getName());
			
			if (ep == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "globalerror"));
				return true;
			}
			
			player.sendMessage(lang.getColoredMessage(userLang, "cash_info_title").replace("[0]", ep.getID() + ""));
			player.sendMessage(lang.getColoredMessage(userLang, "cash_info_amount").replace("[0]", ep.getCash() + ""));
			player.sendMessage(lang.getColoredMessage(userLang, "cash_info_given").replace("[0]", ep.getTotalGiven() + ""));
			player.sendMessage(lang.getColoredMessage(userLang, "cash_info_received").replace("[0]", ep.getTotalReceived() + ""));
			player.sendMessage(lang.getColoredMessage(userLang, "cash_info_donated").replace("[0]", ep.getTotalDonated() + ""));
			
			return true;
		}
		
		if (args[1].equalsIgnoreCase("give")) {
			if (args.length != 4) {
				sendUsage(player);
				return false;
			}
			
			try {
				
				User u = EdgeCoreAPI.userAPI().getUser(args[2]);
				double amount = Double.parseDouble(args[3]);
				
				if (u == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				EconomyPlayer from = EdgeConomy.getEconomy().getEconomyPlayer(user.getName());
				EconomyPlayer to = EdgeConomy.getEconomy().getEconomyPlayer(u.getName());
				
				if (from == null || to == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (amount <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				if (from.getCash() < amount) {
					player.sendMessage(lang.getColoredMessage(userLang, "notenoughmoney"));
					return true;
				}
				
				if (amount > Economy.getMonitoredAmount()) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoohigh"));
					return true;
				}
				
				if (player.getLocation().distanceSquared(to.getUser().getPlayer().getLocation()) > Economy.getMaxCashDistance()) { 
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_player").replace("[0]", to.getName()));
					return true;
				}
				
				from.updateCash(from.getCash() - amount);
				to.updateCash(to.getCash() + amount);
				
				player.sendMessage(lang.getColoredMessage(userLang, "cash_give_success").replace("[0]", to.getName()).replace("[1]", amount + ""));
				
				return true;
			
			} catch(NumberFormatException e) {
				player.sendMessage(lang.getColoredMessage(userLang, "numberformatexception"));
			}
		}
		
		if (args[1].equalsIgnoreCase("donate")) {
			if (args.length != 3) {
				sendUsage(player);
				return false;
			}
			
			try {
				
				double donation = Double.parseDouble(args[2]);
				EconomyPlayer donator = EdgeConomy.getEconomy().getEconomyPlayer(user.getName());
				
				if (donator == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (donation <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				if (donator.getCash() < donation) {
					player.sendMessage(lang.getColoredMessage(userLang, "notenoughmoney"));
					return true;
				}
				
				donator.updateCash(donator.getCash() - donation);
				/** TODO Add state instance which receives the money */
				
				player.sendMessage(lang.getColoredMessage(userLang, "cash_donate_success").replace("[0]", donation + ""));
				
				return true;
				
			} catch(NumberFormatException e) {
				player.sendMessage(lang.getColoredMessage(userLang, "numbeformatException"));
			}
		}
		
		if ( !Level.canUse( user, Level.ADMIN ) ) {
			player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
			return true;
		}
		
		if (args[1].equalsIgnoreCase("update")) {
			if (args.length != 4) {
				sendUsage(player);
				return false;
			}
			
			try {
				
				EconomyPlayer ep = EdgeConomy.getEconomy().getEconomyPlayer(args[2]);
				double amount = Double.parseDouble(args[3]);
				
				if (ep == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (amount < 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				ep.updateCash(amount);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_cash_update_success").replace("[0]", ep.getName()).replace("[1]", amount + ""));
				
				return true;
				
			} catch(NumberFormatException e) {
				player.sendMessage(lang.getColoredMessage(userLang, "numberformatexception"));
			}
		}
		
		if (args[1].equalsIgnoreCase("transfer")) {
			if (args.length != 5) {
				sendUsage(player);
				return false;
			}
			
			try {
				
				EconomyPlayer from = EdgeConomy.getEconomy().getEconomyPlayer(args[2]);
				EconomyPlayer to = EdgeConomy.getEconomy().getEconomyPlayer(args[3]);
				double amount = Double.parseDouble(args[4]);
				
				if (from == null || to == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (amount <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				if (from.getCash() < amount) {
					player.sendMessage(lang.getColoredMessage(userLang, "notenoughmoney"));
					return true;
				}
				
				from.updateCash(from.getCash() - amount);
				to.updateCash(to.getCash() + amount);
				
				player.sendMessage(lang.getColoredMessage(userLang, "admin_cash_transfer_success").replace("[0]", from.getName()).replace("[1]", to.getName()).replace("[2]", amount + ""));
				
				return true;
				
			} catch(NumberFormatException e) {
				player.sendMessage(lang.getColoredMessage(userLang, "numberformatexception"));
			}
		}
		
		return true;
	}

	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		return true;
	}
	
}
