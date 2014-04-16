package net.edgecraft.edgeconomy.commands;

import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.economy.EconomyPlayer;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AccountCommand extends AbstractCommand {
	
	private final Economy economy = Economy.getInstance();
	
	private static final AccountCommand instance = new AccountCommand();
	
	private AccountCommand() { super(); }
	
	public static final AccountCommand getInstance() {
		return instance;
	}
	
	@Override
	public Level getLevel() {
		return Level.USER;
	}
	
	@Override
	public String[] getNames() {
		String[] names = { "account", "acc", "konto" } ;
		return names;
	}
	
	@Override
	public boolean validArgsRange(String[] args) {
		return (args.length > 1 && args.length < 6);
	}
	
	@Override
	public void sendUsageImpl(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/account info");
		sender.sendMessage(EdgeCore.usageColor + "/account apply");
		sender.sendMessage(EdgeCore.usageColor + "/account deactivate");
		sender.sendMessage(EdgeCore.usageColor + "/account deposit <amount>");
		sender.sendMessage(EdgeCore.usageColor + "/account withdraw <amount>");
		
		User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
		
		if (u == null || !Level.canUse(u, Level.MODERATOR)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/account delete <user>");
		sender.sendMessage(EdgeCore.usageColor + "/account updatebalance <user> <amount>");
		sender.sendMessage(EdgeCore.usageColor + "/account lock <user>");
		sender.sendMessage(EdgeCore.usageColor + "/account unlock <user>");
		sender.sendMessage(EdgeCore.usageColor + "/account exists <user>");
		sender.sendMessage(EdgeCore.usageColor + "/account reload [<user>]");
		sender.sendMessage(EdgeCore.usageColor + "/account amount");
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLang();
		
		
		try {
						
			if (args[1].equalsIgnoreCase("delete")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				if (!EdgeCoreAPI.userAPI().exists(args[2])) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (!economy.hasAccount(EdgeCoreAPI.userAPI().getUser(args[2]))) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount_user").replace("[0]", args[2]));
					return true;
				}
				
				economy.deleteAccount(economy.getAccount(EdgeCoreAPI.userAPI().getUser(args[2])).getId());
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_delete_success").replace("[0]", args[2]));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("updatebalance")) {
				if (args.length != 4) {
					sendUsage(player);
					return true;
				}
				
				if (!EdgeCoreAPI.userAPI().exists(args[2])) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				BankAccount acc = getAccount(EdgeCoreAPI.userAPI().getUser(args[2]));
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount_user").replace("[0]", args[2]));
					return true;
				}
				
				if (acc.isClosed()) {
					player.sendMessage(lang.getColoredMessage(userLang, "account_closed_user").replace("[0]", args[2]));
					return true;
				}
				
				double balance = Double.parseDouble(args[3]);
				
				if (balance <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				acc.updateBalance(balance);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_updatebalance_success").replace("[0]", args[2]).replace("[1]", balance + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("lock")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				if (!EdgeCoreAPI.userAPI().exists(args[2])) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				BankAccount acc = getAccount(EdgeCoreAPI.userAPI().getUser(args[2]));
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount_user").replace("[0]", args[2]));
					return true;
				}
				
				acc.setClosed(true);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_status_locked").replace("[0]", args[2]));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("unlock")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				if (!EdgeCoreAPI.userAPI().exists(args[2])) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				BankAccount acc = getAccount(EdgeCoreAPI.userAPI().getUser(args[2]));
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount_user").replace("[0]", args[2]));
					return true;
				}
				
				acc.setClosed(false);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_status_unlocked").replace("[0]", args[2]));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("exists")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				if (!EdgeCoreAPI.userAPI().exists(args[2])) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (getAccount(EdgeCoreAPI.userAPI().getUser(args[2])) == null) {
					
					player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_user_false").replace("[0]", args[2]));
					
				} else {
					
					player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_user_true").replace("[0]", args[2]));
					
				}
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("reload")) {
				if (args.length == 2) {
					
					economy.synchronizeEconomy(true, true);
					player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_reload_all_success"));
					
					return true;
				}
				
				if (args.length == 3) {
					
					if (!EdgeCoreAPI.userAPI().exists(args[2])) {
						player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
						return true;
					}
					
					BankAccount acc = getAccount(EdgeCoreAPI.userAPI().getUser(args[2]));
					
					if (acc == null) {
						player.sendMessage(lang.getColoredMessage(userLang, "noaccount_user").replace("[0]", args[2]));
						return true;
					}
					
					economy.synchronizeAccount(acc.getId());
					
					return true;
				}
			}
			
			if (args[1].equalsIgnoreCase("amount")) {
				if (args.length != 2) {
					sendUsage(player);
					return true;
				}
				
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_amount").replace("[0]", economy.amountOfAccounts() + ""));
				
				return true;
			}
						
			if (args[1].equalsIgnoreCase("info")) {
				
				BankAccount acc = getAccount(user);
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_title").replace("[0]", user.getName()));
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_id").replace("[0]", acc.getId() + ""));
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_owner").replace("[0]", acc.getUser().getName()));
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_balance").replace("[0]", acc.getBalance() + ""));
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_credit").replace("[0]", acc.getRawCredit() + "").replace("[1]", acc.getCredit() + ""));
				
				if (acc.hasWelfare()) 
					player.sendMessage(lang.getColoredMessage(userLang, "acc_info_welfare_true"));
				else
					player.sendMessage(lang.getColoredMessage(userLang, "acc_info_welfare_false"));
				
				if (acc.isClosed())
					player.sendMessage(lang.getColoredMessage(userLang, "acc_info_status_locked"));
				else
					player.sendMessage(lang.getColoredMessage(userLang, "acc_info_status_unlocked"));
				
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_payday").replace("[0]", acc.getPayday() + ""));
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_lowestbalance").replace("[0]", acc.getLowestBalance() + ""));
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_highestbalance").replace("[0]", acc.getHighestBalance() + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("apply")) {
				if (args.length != 2) {
					sendUsage(player);
					return true;
				}
				
				if (economy.hasAccount(user)) {
					player.sendMessage(lang.getColoredMessage(userLang, "acc_apply_alreadyacc"));
					return true;
				}
				
				if (!economy.insideBankCuboid(player)) {
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "Bank"));
					return true;
				}
				
				economy.registerAccount(user.getUUID(), 500.0D, 0);
				player.sendMessage(lang.getColoredMessage(userLang, "acc_apply_success").replace("[0]", getAccount(user).getId() + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("deactivate")) {
				if (args.length != 2) {
					sendUsage(player);
					return true;
				}
				
				if (!economy.hasAccount(user)) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				if (!economy.insideBankCuboid(player)) {
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "Bank"));
					return true;
				}
				
				player.sendMessage(lang.getColoredMessage(userLang, "acc_deactivate_success").replace("[0]", getAccount(user).getId() + ""));
				economy.deleteAccount(getAccount(user).getId());
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("deposit")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				BankAccount acc = getAccount(user);
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				if (!economy.insideATMCuboid(player) || !economy.insideBankCuboid(player)) {
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "ATM / Bank"));
					return true;
				}
				
				if (acc.isClosed()) {
					player.sendMessage(lang.getColoredMessage(userLang, "account_closed"));
					return true;
				}
				
				EconomyPlayer ecoPl = acc.getEconomyPlayer();
				double amount = Double.parseDouble(args[2]);
				
				if (ecoPl == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "globalerror"));
					return true;
				}
				
				if (amount <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				if (amount > ecoPl.getCash()) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoohigh"));
					return true;
				}
				
				ecoPl.updateCash(ecoPl.getCash() - amount);
				acc.updateBalance(acc.getBalance() + amount);
				
				player.sendMessage(lang.getColoredMessage(userLang, "acc_deposit_success").replace("[0]", amount + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("withdraw")) {
				if (args.length != 3) {
					sendUsage(player);
					return true;
				}
				
				BankAccount acc = getAccount(user);
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				if (!economy.insideATMCuboid(player) || !economy.insideBankCuboid(player)) {
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "ATM / Bank"));
					return true;
				}
				
				if (acc.isClosed()) {
					player.sendMessage(lang.getColoredMessage(userLang, "account_closed"));
					return true;
				}
				
				EconomyPlayer ecoPl = acc.getEconomyPlayer();
				double amount = Double.parseDouble(args[2]);
				
				if (ecoPl == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "globalerror"));
					return true;
				}
				
				if (amount <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				if (amount > acc.getBalance()) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoohigh"));
					return true;
				}
				
				if (amount >= Economy.getMaxATMAmount() && !economy.insideBankCuboid(player)) {
					player.sendMessage(lang.getColoredMessage(userLang, "withdraw_needbank").replace("[0]", Economy.getMaxATMAmount() + ""));
					return true;
				}
				
				double fee = acc.getBalance() / 10 * Economy.getWithdrawalFee();
				
				ecoPl.updateCash(ecoPl.getCash() + amount);
				acc.updateBalance(acc.getBalance() - (amount + fee));
				
				player.sendMessage(lang.getColoredMessage(userLang, "acc_withdraw_success").replace("[0]", amount + ""));
				
				return true;				
			}
			
		} catch(NumberFormatException e) {
			player.sendMessage(lang.getColoredMessage(userLang, "numberformatexception"));
		}
		
		return false;
	}
	
	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		return true;
	}
	
	private final BankAccount getAccount(User user) {
		return economy.getAccount(user);
	}
}
