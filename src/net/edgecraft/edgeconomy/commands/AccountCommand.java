package net.edgecraft.edgeconomy.commands;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgeconomy.economy.BankAccount;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.economy.EconomyPlayer;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AccountCommand extends AbstractCommand {
	
	private final LanguageHandler lang = EdgeCore.getLang();
	
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
		String[] names = { "account", "acc" } ;
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
		
		sender.sendMessage(EdgeCore.usageColor + "/account create <user> <balance> <credit>");
		sender.sendMessage(EdgeCore.usageColor + "/account delete <id>");
		sender.sendMessage(EdgeCore.usageColor + "/account updatebalance <id> <amount>");
		sender.sendMessage(EdgeCore.usageColor + "/account lock <id>");
		sender.sendMessage(EdgeCore.usageColor + "/account unlock <id>");
		sender.sendMessage(EdgeCore.usageColor + "/account exists <id>");
		sender.sendMessage(EdgeCore.usageColor + "/account reload [<id>]");
		sender.sendMessage(EdgeCore.usageColor + "/account amount");
	}
	
	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLanguage();
		
		try {
			
			if (args[1].equalsIgnoreCase("info")) {
				
				BankAccount acc = EdgeConomy.getEconomy().getAccount(user.getName());
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_title").replace("[0]", acc.getID() + ""));
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_id").replace("[0]", acc.getID() + ""));
				player.sendMessage(lang.getColoredMessage(userLang, "acc_info_owner").replace("[0]", acc.getOwner()));
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
				
				if (EdgeConomy.getEconomy().hasAccount(user.getID())) {
					player.sendMessage(lang.getColoredMessage(userLang, "acc_apply_alreadyacc"));
					return true;
				}
				
				Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "Bank"));
					return true;
				}
				
				if (CuboidType.getType(cuboid.getCuboidType()) != CuboidType.Bank) {
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "Bank"));
					return true;
				}
				
				EdgeConomy.getEconomy().registerAccount(user.getID(), 500D, 0);
				player.sendMessage(lang.getColoredMessage(userLang, "acc_apply_success").replace("[0]", EdgeConomy.getEconomy().getAccount(player.getName()).getID() + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("deactivate")) {
				if (args.length != 2) {
					sendUsage(player);
					return true;
				}
				
				if (!EdgeConomy.getEconomy().hasAccount(user.getID())) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "Bank"));
					return true;
				}
				
				if (CuboidType.getType(cuboid.getCuboidType()) != CuboidType.Bank) {
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "Bank"));
					return true;
				}
				
				EdgeConomy.getEconomy().deleteAccount(EdgeConomy.getEconomy().getAccount(player.getName()).getID());
				player.sendMessage(lang.getColoredMessage(userLang, "acc_deactivate_success").replace("[0]", EdgeConomy.getEconomy().getAccount(player.getName()).getID() + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("deposit")) {
				if (args.length != 3) {
					sendUsage(player);
					return false;
				}
				
				double amount = Double.parseDouble(args[2]);
				BankAccount acc = EdgeConomy.getEconomy().getAccount(user.getName());
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
				EconomyPlayer ep = acc.getEconomyPlayer();
				
				if (cuboid == null || CuboidType.getType(cuboid.getCuboidType()) != CuboidType.ATM || CuboidType.getType(cuboid.getCuboidType()) != CuboidType.Bank) {
					player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "ATM / Bank"));
					return true;
				}
				
				if (ep == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				if (amount <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				if (amount > ep.getCash()) {
					player.sendMessage(lang.getColoredMessage(userLang, "notenoughmoney"));
					return true;
				}
				
				if (acc.isClosed()) {
					player.sendMessage(lang.getColoredMessage(userLang, "account_closed"));
					return true;
				}
				
				ep.updateCash(ep.getCash() - amount);
				acc.updateBalance(acc.getBalance() + amount);
				
				player.sendMessage(lang.getColoredMessage(userLang, "acc_deposit_success").replace("[0]", amount + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("withdraw")) {
				if (args.length != 3) {
					sendUsage(player);
					return false;
				}
				
				double amount = Double.parseDouble(args[2]);
				BankAccount acc = EdgeConomy.getEconomy().getAccount(user.getName());
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
					return true;
				}
				
				EconomyPlayer ep = acc.getEconomyPlayer();
				Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
				
				if (cuboid == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "eco_nocuboid"));
					return true;
				}
				
				if (ep == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
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
				
				if (acc.isClosed()) {
					player.sendMessage(lang.getColoredMessage(userLang, "account_closed"));
					return true;
				}
				
				if (CuboidType.getType(cuboid.getCuboidType()) != CuboidType.ATM || CuboidType.getType(cuboid.getCuboidType()) != CuboidType.Bank) {
					player.sendMessage(lang.getColoredMessage(userLang, "eco_nocuboid"));
					return true;
				}
				
				if (CuboidType.getType(cuboid.getCuboidType()) == CuboidType.ATM) {
					
					if (amount > Economy.getMaxATMAmount() && CuboidType.getType(cuboid.getCuboidType()) != CuboidType.Bank) {
						player.sendMessage(lang.getColoredMessage(userLang, "withdraw_needbank").replace("[0]", Economy.getMaxATMAmount() + ""));
						return true;
					}
					
					if (amount > Economy.getMonitoredAmount()) {
						player.sendMessage(lang.getColoredMessage(userLang, "amounttoohigh"));
						return true;
					}
					
					double withdrawalFee = (int) (acc.getBalance() / 100 * Economy.getWithdrawalFee());
										
					ep.updateCash(ep.getCash() + amount);
					acc.updateBalance(acc.getBalance() - (amount + withdrawalFee));
					
					player.sendMessage(lang.getColoredMessage(userLang, "acc_withdraw_success").replace("[0]", amount + ""));
					
					return true;
				}
			}
			
			if (args[1].equalsIgnoreCase("create")) {
				if (args.length != 5) {
					sendUsage(player);
					return false;
				}
				
				User accUser = EdgeCoreAPI.userAPI().getUser(args[2]);
				
				if (accUser == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
					return true;
				}
				
				double balance = Double.parseDouble(args[3]);
				double credit = Double.parseDouble(args[4]);
				
				if (balance < 0 || credit < 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
				}
				
				EdgeConomy.getEconomy().registerAccount(accUser.getID(), balance, credit);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_create_success").replace("[0]", args[2]));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("delete")) {
				if (args.length != 3) {
					sendUsage(player);
					return false;
				}
				
				int id = Integer.parseInt(args[2]);
				
				if (!EdgeConomy.getEconomy().existsAccount(id)) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownaccount").replace("[0]", id + ""));
					return true;
				}
				
				EdgeConomy.getEconomy().deleteAccount(id);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_delete_success").replace("[0]", id + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("updatebalance")) {
				if (args.length != 4) {
					sendUsage(player);
					return false;
				}
				
				double balance = Double.parseDouble(args[3]);
				BankAccount acc = EdgeConomy.getEconomy().getAccountByOwnerID(Integer.parseInt(args[2]));
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownaccount").replace("[0]", args[2]));
					return true;
				}
				
				if (acc.isClosed()) {
					player.sendMessage(lang.getColoredMessage(userLang, "account_closed_id").replace("[0]", acc.getID() + ""));
					return true;
				}
				
				acc.updateBalance(balance);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_updatebalance_success").replace("[0]", acc.getID() + "").replace("[1]", acc.getBalance() + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("lock")) {
				if (args.length != 3) {
					sendUsage(player);
					return false;
				}
				
				int id = Integer.parseInt(args[2]);
				
				if (id <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				BankAccount acc = EdgeConomy.getEconomy().getAccount(id);
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownaccount").replace("[0]", id + ""));
					return true;
				}
				
				if (acc.isClosed()) {
					player.sendMessage(lang.getColoredMessage(userLang, "account_closed_id").replace("[0]", id + ""));
					return true;
				}
				
				acc.setClosed(true);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_status_locked").replace("[0]", id + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("unlock")) {
				if (args.length != 3) {
					sendUsage(player);
					return false;
				}
				
				int id = Integer.parseInt(args[2]);
				
				if (id <= 0) {
					player.sendMessage(lang.getColoredMessage(userLang, "amounttoolow"));
					return true;
				}
				
				BankAccount acc = EdgeConomy.getEconomy().getAccount(id);
				
				if (acc == null) {
					player.sendMessage(lang.getColoredMessage(userLang, "unknownaccount").replace("[0]", id + ""));
					return true;
				}
				
				acc.setClosed(false);
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_status_unlocked").replace("[0]", id + ""));
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("exists")) {
				if (args.length != 3) {
					sendUsage(player);
					return false;
				}
				
				int id = Integer.parseInt(args[2]);
				
				if (EdgeConomy.getEconomy().existsAccount(id)) {
					
					player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_exists_id_true").replace("[0]", id + ""));
					
				} else {
					
					player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_exists_id_false").replace("[0]", id + ""));
					
				}
				
				return true;
			}
			
			if (args[1].equalsIgnoreCase("reload")) {
				if (args.length > 3) {
					sendUsage(player);
					return false;
				}
				
				if (args.length == 2) {
					
					EdgeConomy.getEconomy().synchronizeEconomy(true, false);
					player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_reload_all_success"));
					
					return true;
				}
				
				if (args.length == 3) {
					
					int id = Integer.parseInt(args[2]);
					
					if (!EdgeConomy.getEconomy().existsAccount(id)) {
						player.sendMessage(lang.getColoredMessage(userLang, "unknownaccount").replace("[0]", id + ""));
						return true;
					}
					
					EdgeConomy.getEconomy().synchronizeAccount(id);
					player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_reload_success").replace("[0]", id + ""));
					
					return true;
				}
			}
			
			if (args[1].equalsIgnoreCase("amount")) {
				if (args.length != 2) {
					sendUsage(player);
					return false;
				}
				
				player.sendMessage(lang.getColoredMessage(userLang, "admin_acc_amount").replace("[0]", EdgeConomy.getEconomy().amountOfAccounts() + ""));
				
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
