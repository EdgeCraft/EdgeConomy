package net.edgecraft.edgeconomy.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.edgecraft.edgeconomy.economy.EcoMonitor;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;

public class PaydayCommand extends AbstractCommand {
	
	private static final PaydayCommand instance = new PaydayCommand();
	
	private PaydayCommand() { super(); }
	
	public static final PaydayCommand getInstance() {
		return instance;
	}

	@Override
	public Level getLevel() {
		return Level.USER;
	}

	@Override
	public String[] getNames() {
		return new String[] { "payday" };
	}

	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLang();
		
		if (args.length == 1) {
			
			if (!Economy.getInstance().hasAccount(user)) {
				player.sendMessage(lang.getColoredMessage(userLang, "noaccount"));
				return true;
			}
			
			EcoMonitor.getInstance().showMonitor(user, user);
			
			return true;
		}
		
		if (!Level.canUse(user, Level.MODERATOR)) {
			player.sendMessage(lang.getColoredMessage(userLang, "nopermission"));
			return true;
		}
		
		if (args.length == 2) {
			
			User target = EdgeCoreAPI.userAPI().getUser(args[1]);
			
			if (target == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "notfound"));
				return true;
			}
			
			if (!Economy.getInstance().hasAccount(target)) {
				player.sendMessage(lang.getColoredMessage(userLang, "noaccount_user").replace("[0]", args[1]));
				return true;
			}
			
			EcoMonitor.getInstance().showMonitor(target, user);
			
			return true;
		}
		
		return false;
	}

	@Override
	public void sendUsageImpl(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/payday");
		
		User u = users.getUser(sender.getName());
		
		if (!Level.canUse(u, Level.MODERATOR))
			return;
		
		sender.sendMessage(EdgeCore.usageColor + "/payday <user>");
	}

	@Override
	public boolean validArgsRange(String[] args) {
		return args.length == 1 || args.length == 2;
	}
}
