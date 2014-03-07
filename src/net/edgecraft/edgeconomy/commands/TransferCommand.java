package net.edgecraft.edgeconomy.commands;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.transactions.TransactionManager;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TransferCommand extends AbstractCommand {

	@Override
	public Level getLevel() {
		return Level.USER;
	}

	@Override
	public String[] getNames() {
		String[] names = { "transfer" };
		return names;
	}

	@Override
	public void sendUsage(CommandSender sender) {
		if (sender instanceof Player) {
			
			User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
			
			if (u != null) {
				
				if (!Level.canUse(u, getLevel())) return;
				
				sender.sendMessage(EdgeCore.usageColor + "/transfer <to> <amount> <description>");
				
				if (!Level.canUse(u, Level.MODERATOR)) return;
				
				sender.sendMessage(EdgeCore.usageColor + "/transfer last [<amount>]");
				
				if (!Level.canUse(u, Level.ADMIN)) return;
				
				sender.sendMessage(EdgeCore.usageColor + "/transfer <from> <to> <amount> <description>");
			}
		}
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLanguage();
		
		if (!Level.canUse(user, getLevel())) {
			player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
			return true;
		}
		
		try {			
			
			// /transfer <to> <amount> <description>
			if (args.length == 4) {
				
				doTransaction(EdgeConomy.getEconomy()
						.getAccount(user.getName()), EdgeConomy.getEconomy().getAccount(Integer.parseInt(args[1])), Double.parseDouble(args[2]), args[3], player, user);	
				
				return true;
				
			}
			
			// /transfer <from> <to> <amount> <description>
			if (args.length == 5) {
			
				doTransaction(EdgeConomy.getEconomy()
						.getAccount(Integer.parseInt(args[1])), EdgeConomy.getEconomy().getAccount(Integer.parseInt(args[2])), Double.parseDouble(args[3]), args[4], player, user);
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("last")) {
				if (args.length == 2) {
					if(!Level.canUse(user, Level.MODERATOR)) {
						player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
						return true;
					}
					
					listLastTransactions(player, 3);
					
					return true;
				}
				
				if (args.length == 3) {
					if (!Level.canUse(user, Level.ADMIN)) {
						player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
						return true;
					}
					
					listLastTransactions(player, Integer.parseInt(args[2]));
					
					return true;
				}
			}

		} catch(NumberFormatException e) {
			player.sendMessage(lang.getColoredMessage(userLang, "numberformatexception"));
		}
		
		return true;
	}

	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		listLastTransactions(sender, 5);
		return true;
	}

	@Override
	public boolean validArgsRange(String[] args) {
		return (args.length > 1 && args.length < 6);
	}
	
	private void listLastTransactions(CommandSender sender, int amount) {
		if (TransactionManager.getInstance().getTransactions().isEmpty()) {
			sender.sendMessage(EdgeCore.errorColor + "No transactions found!");
			return;
		}
		
		for (int i = TransactionManager.getInstance().amountOfTransactions() -1; i < TransactionManager.getInstance().getTransactions().size(); i--) {
			sender.sendMessage(EdgeCore.sysColor + i + ") " + TransactionManager.getInstance().getTransaction(i).getGist());
			if (i <= amount) break;
		}
	}
	
	private void doTransaction(BankAccount from, BankAccount to, double amount, String description, Player sender, User user) {
		if (from == null || to == null) {
			sendUsage(sender);
			return;
		}
		
		if (amount <= 0) {
			sender.sendMessage(lang.getColoredMessage(user.getLanguage(), "amounttoolow"));
			return;
		}
	
		if (amount > from.getBalance()) {
			sender.sendMessage(lang.getColoredMessage(user.getLanguage(), "notenoughmoney"));
			return;
		}
		
		Cuboid cuboid = Cuboid.getCuboid(sender.getLocation());
		
		if (cuboid == null) {
			sender.sendMessage(lang.getColoredMessage(user.getLanguage(), "notinrange_location").replace("[0]", "ATM"));
			return;
		}
		
		if (CuboidType.getType(cuboid.getCuboidType()) == CuboidType.ATM) {
			
			if (amount >= Economy.getMonitoredAmount()) {
				for (User u : EdgeCoreAPI.userAPI().getUsers().values()) {
					if (u == null || !u.getPlayer().isOnline()) continue;
					
					if (Level.canUse(u, Level.MODERATOR)) {
						u.getPlayer().sendMessage(lang.getColoredMessage(user.getLanguage(), "transaction_highamount").replace("[0]", from.getOwner()).replace("[1]", to.getOwner()).replace("[2]", amount + ""));
					}
				}
			}
			
			EdgeConomy.getTransactions().addTransaction(from, to, amount, description);
			sender.sendMessage(lang.getColoredMessage(user.getLanguage(), "transfer_success").replace("[0]", amount + "").replace("[1]", to.getID() + ""));
			
		} else {
			sender.sendMessage(lang.getColoredMessage(user.getLanguage(), "eco_nocuboid"));
			return;
		}
	}
}
