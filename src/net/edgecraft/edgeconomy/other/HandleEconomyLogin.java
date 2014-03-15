package net.edgecraft.edgeconomy.other;

import net.edgecraft.edgeconomy.EdgeConomyAPI;
import net.edgecraft.edgeconomy.economy.Economy;
import net.edgecraft.edgecore.EdgeCoreAPI;
import net.edgecraft.edgecore.lang.LanguageHandler;
import net.edgecraft.edgecore.user.User;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HandleEconomyLogin implements Listener {
	
	private final LanguageHandler lang = EdgeCoreAPI.languageAPI();
	private final Economy economy = EdgeConomyAPI.economyAPI();
	
	@EventHandler(priority = EventPriority.LOW)
	public void registerEconomyPlayer(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		User user = EdgeCoreAPI.userAPI().getUser(player.getName());
		
		if (user == null)
			return;
		
		try {
			
			if (economy.existsEconomyPlayer(player.getName()))
				return;
			
			economy.registerEconomyPlayer(player.getName());
			player.sendMessage(lang.getColoredMessage(user.getLanguage(), "registration_eco_success"));
			
		} catch(Exception e) {
			e.printStackTrace();
			player.sendMessage(lang.getColoredMessage(user.getLanguage(), "globalerror"));
		}
	}
	
	@EventHandler
	public void registerAccount(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		User user = EdgeCoreAPI.userAPI().getUser(player.getName());
		
		if (user == null)
			return;
		
		if (!Economy.isAllowAccounts())
			return;
		
		try {
			
			if (Economy.isAutoCreateAccounts()) {
				
				economy.registerAccount(user.getID(), 500D, 0);
				player.sendMessage(lang.getColoredMessage(user.getLanguage(), "acc_apply_success").replace("[0]", economy.getAccount(player.getName()).getID() + ""));
				
			} else {
				
				player.sendMessage(lang.getColoredMessage(user.getLanguage(), "registration_eco_accinfo"));
				
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			player.sendMessage(lang.getColoredMessage(user.getLanguage(), "globalerror"));
		}
	}
}
