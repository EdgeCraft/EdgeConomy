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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TransferCommand extends AbstractCommand {
	
	private static final TransferCommand instance = new TransferCommand();
	
	private TransferCommand() { super(); }
	
	public static final TransferCommand getInstance() {
		return instance;
	}
	
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
	public void sendUsageImpl(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/transfer <to> <amount> <description>");
		
		User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
		
		if (u == null || !Level.canUse(u, Level.SUPPORTER)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/transfer last [<amount>]");
		
		if (u == null || !Level.canUse(u, Level.MODERATOR)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/transfer <from> <to> <amount> <description>");
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLanguage();
		
		try {			
			
			// /transfer <to> <amount> <description>
			if (args.length == 4) {
				
				if (!users.exists(args[1])) {
					player.sendMessage(lang.getColoredMessage(user.getLang(), "notfound"));
					return true;
				}
				
				doTransaction(EdgeConomy.getEconomy()
						.getAccount(user.getName()), EdgeConomy.getEconomy().getAccount(args[1]), Double.parseDouble(args[2]), args[3], player, user, false);	
				
				return true;
				
			}
			
			// /transfer <from> <to> <amount> <description>
			if (args.length == 5) {
			
				if (!users.exists(args[1])) {
					player.sendMessage(lang.getColoredMessage(user.getLang(), "notfound"));
					return true;
				}
				
				doTransaction(EdgeConomy.getEconomy()
						.getAccount(args[1]), EdgeConomy.getEconomy().getAccount(args[2]), Double.parseDouble(args[3]), args[4], player, user, true);
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("last")) {
				if (args.length == 2) {
					if(!Level.canUse(user, Level.SUPPORTER)) {
						player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
						return true;
					}
					
					listLastTransactions(player, 3);
					
					return true;
				}
				
				if (args.length == 3) {
					if (!Level.canUse(user, Level.MODERATOR)) {
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
	
	private void doTransaction(BankAccount from, BankAccount to, double amount, String description, Player sender, User user, boolean admin) {
		if (from == null) {
			sender.sendMessage(lang.getColoredMessage(user.getLang(), "noaccount"));
			return;
		}
		
		if (to == null) {
			sender.sendMessage(lang.getColoredMessage(user.getLang(), "noaccount_anonym"));
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
		
		if (!Economy.getInstance().insideBankCuboid(sender)) {
			sender.sendMessage(lang.getColoredMessage(user.getLanguage(), "notinrange_location").replace("[0]", "Bank"));
			return;
		}
		
		if (Economy.getInstance().insideATMCuboid(sender)) {
			
			if (amount >= Economy.getMonitoredAmount()) {
				for (User u : EdgeCoreAPI.userAPI().getUsers().values()) {
					if (u == null || !u.getPlayer().isOnline()) continue;
					
					if (Level.canUse(u, Level.SUPPORTER)) {
						u.getPlayer().sendMessage(lang.getColoredMessage(user.getLanguage(), "transaction_highamount")
								.replace("[0]", from.getUser().getName())
								.replace("[1]", to.getUser().getName())
								.replace("[2]", amount + ""));
					}
				}
			}
			
			EdgeConomy.getTransactions().addTransaction(from, to, amount, description);
			
			if (!admin)
				sender.sendMessage(lang.getColoredMessage(user.getLanguage(), "transfer_success").replace("[0]", amount + "").replace("[1]", to.getUser().getName()));
			else
				sender.sendMessage(lang.getColoredMessage(user.getLanguage(), "admin_transfer_success").replace("[0]", from.getId() + "").replace("[1]", to.getId() + "").replace("[2]", amount + ""));
			
		} else {
			sender.sendMessage(lang.getColoredMessage(user.getLanguage(), "eco_nocuboid"));
			return;
		}
	}
}
