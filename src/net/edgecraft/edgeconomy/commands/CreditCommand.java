package net.edgecraft.edgeconomy.commands;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreditCommand extends AbstractCommand {
	
	private final LanguageHandler lang = EdgeCore.getLang();
	
	private static final CreditCommand instance = new CreditCommand();
	
	private CreditCommand() { super(); }
	
	public static final CreditCommand getInstance() {
		return instance;
	}
	
	@Override
	public Level getLevel() {
		return Level.USER;
	}
	
	@Override
	public String[] getNames() {
		String[] names = { "credit" };
		return names;
	}
	
	@Override
	public boolean validArgsRange(String[] args) {
		return (args.length > 1 && args.length < 5);
	}
	
	@Override
	public void sendUsageImpl(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/credit apply <amount>");
		sender.sendMessage(EdgeCore.usageColor + "/credit pay <amount>");
		sender.sendMessage(EdgeCore.usageColor + "/credit info");
		
		User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
		
		if (u == null || !Level.canUse(u, Level.MODERATOR)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/credit set <user> <amount>");
		sender.sendMessage(EdgeCore.usageColor + "/credit remove <user>");
		sender.sendMessage(EdgeCore.usageColor + "/credit pay <user> <amount>");
		sender.sendMessage(EdgeCore.usageColor + "/credit info <user>");
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {	
		
		String userLang = user.getLanguage();
		
		try {
			
			if (args[1].equalsIgnoreCase("apply")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				BankAccount acc = EdgeConomy.getEconomy().getAccount(user);
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				if (!Economy.getInstance().insideBankCuboid(player)) {
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "Bank"));
					return true;
				}
				
				if (acc.getRawCredit() > 0.0D) {
					player.sendMessage(lang.getColoredMessage(userLang, "credit_apply_alreadycredit"));
					return true;
				}
				
				acc.updateCredit(Double.parseDouble(args[2]));
				player.sendMessage(lang.getColoredMessage(userLang, "credit_apply_success").replace("[0]", args[2]));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("info")) {				
				if (args.length == 2) {
					
					BankAccount acc = EdgeConomy.getEconomy().getAccount(user);
					
					if (acc == null) {
						player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
						return true;
					}
					
					player.sendMessage(lang.getColoredMessage(userLang, "credit_info_rawcredit").replace("[0]", acc.getRawCredit() + ""));
					player.sendMessage(lang.getColoredMessage(userLang, "credit_info_credit").replace("[0]", acc.getCredit() + ""));
					player.sendMessage(lang.getColoredMessage(userLang, "credit_info_remaining").replace("[0]", acc.getCredit() - acc.getPaidCredit() + ""));
					
					return true;
					
				}
				
				if (args.length == 3) {
					
					if (!Level.canUse(user, Level.MODERATOR)) {
						player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
						return true;
					}
					
					BankAccount acc = EdgeConomy.getEconomy().getAccount(args[2]);
					
					if (acc == null) {
						player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
						return true;
					}
					
					player.sendMessage(lang.getColoredMessage(userLang, "admin_credit_info_title"));
					player.sendMessage(lang.getColoredMessage(userLang, "credit_info_rawcredit").replace("[0]", acc.getRawCredit() + ""));
					player.sendMessage(lang.getColoredMessage(userLang, "credit_info_credit").replace("[0]", acc.getCredit() + ""));
					player.sendMessage(lang.getColoredMessage(userLang, "credit_info_remaining").replace("[0]", acc.getCredit() - acc.getPaidCredit() + ""));
					
					return true;
				}
			}
			
			if (args[1].equalsIgnoreCase("pay")) {
				if (args.length == 3) {
					
					double amount = Double.parseDouble(args[2]);
					BankAccount acc = EdgeConomy.getEconomy().getAccount(user.getName());
					
					if (acc == null) {
						player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
						return true;
					}
					
					if (amount <= 0) {
						player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
						return true;
					}
					
					if (amount > acc.getBalance()) {
						player.sendMessage(lang.getColoredMessage(userLang, "notenoughmoney"));
						return true;
					}
					
					if (!(acc.getCredit() > 0)) {
						player.sendMessage(lang.getColoredMessage(userLang, "nocredit"));
						return true;
					}
					
					if (!Economy.getInstance().insideBankCuboid(player)) {
						player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "Bank"));
						return true;
					}
					
					if (amount > acc.getCredit()) {
						double payback = amount - acc.getCredit();
						
						acc.updateCredit(acc.getCredit() - amount);
						acc.updatePaidCredit(0);
						acc.updateBalance(acc.getBalance() - amount + payback);
						
						player.sendMessage(lang.getColoredMessage(userLang, "credit_pay_complete"));
						
						return true;
					}
					
					acc.updateCredit(acc.getCredit() - amount);
					acc.updatePaidCredit(acc.getPaidCredit() + amount);
					acc.updateBalance(acc.getBalance() - amount);
					
					player.sendMessage(lang.getColoredMessage(userLang, "credit_pay_success").replace("[0]", amount + "").replace("[1]", acc.getCredit() - acc.getPaidCredit() + ""));
					
					return true;
				}
				
				if (args.length == 4) {
					if (!Level.canUse(user, Level.MODERATOR)) {
						player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
						return true;
					}
					
					BankAccount acc = EdgeConomy.getEconomy().getAccount(args[2]);
					double amount = Double.parseDouble(args[3]);
					
					if (acc == null) {
						player.sendMessage(lang.getColoredMessage(userLang, "noaccount_user").replace("[0]", args[2]));
						return true;
					}
					
					if (amount <= 0) {
						player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
						return true;
					}
					
					if (amount > acc.getCredit()) {
						
						acc.updateCredit(0);
						player.sendMessage(lang.getColoredMessage(userLang, "admin_credit_remove_success").replace("[0]", args[2]));
						
						return true;
					}
					
					acc.updateCredit(acc.getCredit() - amount);
					player.sendMessage(lang.getColoredMessage(userLang, "admin_credit_pay_success").replace("[0]", amount + "").replace("[1]", args[2]).replace("[2]", acc.getCredit() + ""));
					
					return true;
				}
			}
			
			if (!Level.canUse(user, Level.MODERATOR)) {
				player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
				return true;
			}
			
			if (args[1].equalsIgnoreCase("set")) {
				if (args.length != 4) {
					sendUsage(player);
					return false;
				}
				
				BankAccount acc = EdgeConomy.getEconomy().getAccount(args[2]);
				double amount = Double.parseDouble(args[3]);
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount_user").replace("[0]", args[2]));
					return true;
				}
				
				if (amount <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				acc.updateCredit(amount);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_credit_set_success").replace("[0]", args[2]).replace("[1]", amount + ""));
								
				return true;
			}
			
			if (args[1].equalsIgnoreCase("remove")) {
				if (args.length != 3) {
					sendUsage(player);
					return false;
				}
				
				BankAccount acc = EdgeConomy.getEconomy().getAccount(args[2]);
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount_user").replace("[0]", args[2]));
					return true;
				}
				
				if (!(acc.getCredit() > 0)) {
					player.sendMessage(lang.getColoredMessage(userLang, "nocredit_user").replace("[0]", args[2]));
					return true;
				}
				
				acc.updateCredit(0);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_credit_remove_success").replace("[0]", args[2]));
								
				return true;
			}
			
		} catch(NumberFormatException e) {
			player.sendMessage(lang.getColoredMessage(userLang, "numberformatexception"));
		}
		
		return true;
	}
	
	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		return true;
	}
}
