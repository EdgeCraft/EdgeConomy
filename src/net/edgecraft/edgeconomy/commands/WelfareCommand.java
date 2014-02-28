package net.edgecraft.edgeconomy.commands;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.economy.EconomyPlayer;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WelfareCommand extends AbstractCommand {

	@Override
	public Level getLevel() {
		return Level.USER;
	}

	@Override
	public String[] getNames() {
		String[] names = { "welfare" };
		return names;
	}

	@Override
	public boolean runImpl(Player player, User user, String[] args) throws Exception {
		
		String userLang = user.getLanguage();
		
		if (args[1].equalsIgnoreCase("apply")) {
			
			player.sendMessage(lang.getColoredMessage(userLang, "pluginexception").replace("[0]", "EdgeCuboid"));
			
			return true;
		}
		
		if (args[1].equalsIgnoreCase("cancel")) {
			
			player.sendMessage(lang.getColoredMessage(userLang, "pluginexception").replace("[0]", "EdgeCuboid"));
			
			return true;
		}
		
		if (args[1].equalsIgnoreCase("info")) {
			
			EconomyPlayer ep = EdgeConomy.getEconomy().getEconomyPlayer(user.getName());
			
			if (ep == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "globalerror"));
				return true;
			}
			
			player.sendMessage(lang.getColoredMessage(userLang, "welfare_info_title"));
			
			if (ep.hasWelfare())
				player.sendMessage(lang.getColoredMessage(userLang, "welfare_info_status_true"));
			else
				player.sendMessage(lang.getColoredMessage(userLang, "welfare_info_status_false"));
			
			EdgeConomy.getEconomy();
			player.sendMessage(lang.getColoredMessage(userLang, "welfare_info_independentafter")
					.replace("[0]", Economy.getMaxWelfareBalance() - (ep.getCash() + ep.getAccount().getBalance()) + ""));
			
			return true;
		}
		
		return true;
	}

	@Override
	public void sendUsage(CommandSender sender) {
		if (sender instanceof Player) {
			
			User u = EdgeCoreAPI.userAPI().getUser(sender.getName());
			
			if (u != null) {
				
				if (!Level.canUse(u, getLevel())) return;
				
				sender.sendMessage(EdgeCore.usageColor + "/welfare apply");
				sender.sendMessage(EdgeCore.usageColor + "/welfare cancel");
				sender.sendMessage(EdgeCore.usageColor + "/welfare info");
				
			}
		}
	}

	@Override
	public boolean sysAccess(CommandSender sender, String[] args) {
		return true;
	}

	@Override
	public boolean validArgsRange(String[] args) {
		return (args.length > 1 && args.length < 3);
	}

}
