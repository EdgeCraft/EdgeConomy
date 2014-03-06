package net.edgecraft.edgeconomy.other;

import net.edgecraft.edgeconomy.commands.AccountCommand;
import net.edgecraft.edgeconomy.commands.CashCommand;
import net.edgecraft.edgeconomy.commands.CreditCommand;
import net.edgecraft.edgeconomy.commands.TransferCommand;
import net.edgecraft.edgeconomy.commands.WelfareCommand;
import net.edgecraft.edgecore.command.CommandHandler;

public class EconomyCommands extends CommandHandler {

	private static final EconomyCommands instance = new EconomyCommands();
	
	private EconomyCommands() {
		
		super.registerCommand( new AccountCommand() );
		super.registerCommand( new CashCommand() );
		super.registerCommand( new CreditCommand() );
		super.registerCommand( new TransferCommand() );
		super.registerCommand( new WelfareCommand() );
	}
	
	public static final EconomyCommands getInstance() {
		return instance;
	}
	
}
