package net.edgecraft.edgeconomy.commands;

import net.edgecraft.edgeconomy.EdgeConomy;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgeconomy.economy.EconomyPlayer;
import net.edgecraft.edgecore.EdgeCore;
import net.edgecraft.edgecore.command.AbstractCommand;
import net.edgecraft.edgecore.command.Level;
import net.edgecraft.edgecore.user.User;
import net.edgecraft.edgecuboid.cuboid.Cuboid;
import net.edgecraft.edgecuboid.cuboid.types.CuboidType;

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
			
			EconomyPlayer ep = Economy.getInstance().getEconomyPlayer(player.getName());
			Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
			if (ep == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "globalerror"));
				return true;
			}
			
			if (cuboid == null || cuboid.getCuboidType() != CuboidType.Bank.getTypeID()) {
				player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "Bank"));
				return true;
			}
			
			if (ep.hasWelfare()) {
				player.sendMessage(lang.getColoredMessage(userLang, "welfare_apply_alreadywelfare"));
				return true;
			}
			
			if (!ep.checkWelfare()) {
				player.sendMessage(lang.getColoredMessage(userLang, "welfare_apply_warning"));
				return true;
			}
			
			ep.setWelfare(true);
			player.sendMessage(lang.getColoredMessage(userLang, "welfare_apply_success"));
			
			return true;
		}
		
		if (args[1].equalsIgnoreCase("cancel")) {
			
			EconomyPlayer ep = Economy.getInstance().getEconomyPlayer(player.getName());
			Cuboid cuboid = Cuboid.getCuboid(player.getLocation());
			
			if (ep == null) {
				player.sendMessage(lang.getColoredMessage(userLang, "globalerror"));
				return true;
			}
			
			if (cuboid == null || cuboid.getCuboidType() != CuboidType.Bank.getTypeID()) {
				player.sendMessage(lang.getColoredMessage(userLang, "notinrange_location").replace("[0]", "Bank"));
				return true;
			}
			
			if (!ep.hasWelfare()) {
				player.sendMessage(lang.getColoredMessage(userLang, "welfare_cancel_nowelfare"));
				return true;
			}
			
			if (ep.checkWelfare()) {
				player.sendMessage(lang.getColoredMessage(userLang, "welfare_cancel_warning"));
			}
			
			ep.setWelfare(false);
			player.sendMessage(lang.getColoredMessage(userLang, "welfare_cancel_success"));
			
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
	public void sendUsageImpl(CommandSender sender) {
		if (!(sender instanceof Player)) return;
		
		sender.sendMessage(EdgeCore.usageColor + "/welfare apply");
		sender.sendMessage(EdgeCore.usageColor + "/welfare cancel");
		sender.sendMessage(EdgeCore.usageColor + "/welfare info");
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
