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
		
		super.registerCommand( AccountCommand.getInstance() );
		super.registerCommand( CashCommand.getInstance() );
		super.registerCommand( CreditCommand.getInstance() );
		super.registerCommand( TransferCommand.getInstance() );
		super.registerCommand( WelfareCommand.getInstance() );
		
	}
	
	public static final EconomyCommands getInstance() {
		return instance;
	}
	
}
