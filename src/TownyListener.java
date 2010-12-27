import java.util.*;
import java.util.logging.Logger;

public class TownyListener extends PluginListener {
	protected static final Logger log = Logger.getLogger("Minecraft");
    public TownyThread towny;
    public CommandQueue<Object> commandQueue;
    private ArrayList<String> commands;

    public TownyListener() {
        commands = new ArrayList<String>();
        commands.add("/resident");commands.add("/player");
        commands.add("/town");
        commands.add("/nation");
		commands.add("/towny");
        commands.add("/claim");
        commands.add("/ally");
		commands.add("/townyadmin");
		
        //Debug Commands
        commands.add("/wait");
		commands.add("/loc");
    }

    public void onLogin(Player player) {
        towny.onLogin(player);
    }
    
    public void onDisconnect(Player player) {
        towny.playerZone.remove(player.getName());
		
		// End claim mode for player
		if (towny.playerClaim.contains(player.getName())) {
			for (int i = 0; i < towny.playerClaim.size(); i++) {
				if (towny.playerClaim.get(i) == player.getName()) {
					towny.playerClaim.remove(i);
					break;
				}
			}
		}
		// End claim mode for player
		if (towny.playerMap.contains(player.getName())) {
			for (int i = 0; i < towny.playerMap.size(); i++) {
				if (towny.playerMap.get(i) == player.getName()) {
					towny.playerMap.remove(i);
					break;
				}
			}
		}
    }

    public boolean onCommand(Player player, String[] split) {
        if (!player.canUseCommand(split[0]))
            return false;
        if (commands.contains(split[0])) {
            Object[] objs = {player, split};
            commandQueue.addWork(objs);
            return true;
        }
		
		return false;
    }
    
    public void onPlayerMove(Player player, Location from, Location to) {
        Object[] objs = {player, from, to};
		commandQueue.addWork(objs);
    }
    
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker,BaseEntity defender, int amount){
		if (type == PluginLoader.DamageType.ENTITY && attacker.isPlayer() && defender.isPlayer()) {
			Player a = attacker.getPlayer();
			
			// Check Town PvP status
			long[] posTownBlock = TownyUtil.getTownBlock((long)a.getX(), (long)a.getZ());
			String key = posTownBlock[0]+","+posTownBlock[1];
			TownBlock townblock = towny.world.townblocks.get(key);
			if (townblock == null) return false;
			log.info("Has townblock");
			if (townblock.town == null) return false;
			log.info("Has town");
			if (!townblock.town.pvp) return true;
			log.info("Has PVP Zone!");
			
			// Check Allies
			if (!TownyProperties.friendlyfire) {
				Resident residenA = towny.world.residents.get(a.getName());
				if (residenA == null) return false;
				if (residenA.town == null) return false;
				Player b = defender.getPlayer();
				Resident residenB = towny.world.residents.get(b.getName());
				if (residenB == null) return false;
				if (residenB.town == null) return false;
				if (residenA.town == residenB.town) return true;
				if (residenA.town.nation == null || residenB.town.nation == null) return false;
				if (residenA.town.nation == residenB.town.nation) return true;
				if (residenA.town.nation.friends.contains(residenB.town.nation)) return true;
			}
		}
		
		return false;
	}
	
    public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
		if (player.canUseCommand("/townyadmin")) // Ignore PlayerZones when admin.
			return false;
		
		int zone;
		if (towny == null || towny.playerZone == null)
			zone = -1;
		else
			zone = (towny.playerZone.get(player.getName()) == null) ? -1 : towny.playerZone.get(player.getName());
		
		if (zone == -1) {
			player.sendMessage(Colors.Rose + "Error: You don't have a playerZone.");
			player.sendMessage(Colors.Rose + "Notify admin. Shade (coder) needs to know what");
			player.sendMessage(Colors.Rose + "happened. Tell admin what you interactions with");
			player.sendMessage(Colors.Rose + "Towny this login. Thank you. -Shade");
            return true;
		}
		
		// Stop creation if player doesn't belong to town or if he's an enemy
        // ToDo: Let enemies place TNT and/or fire.
        if (zone == 3 || zone == 2) {
			player.sendMessage(Colors.Rose + "The nearby town's barrier prevents you from creation.");
            return true;
		}
		
		// Stop creation outside towns if configured so.
		if (zone == 0 && !TownyProperties.unclaimedZoneBuildRights && !player.canUseCommand("/towny-worldbuilder")) {
			player.sendMessage(Colors.Rose + "You should start a town first before building.");
			return true;
		}
        
        // Otherwise let the block be placed.
        return false;
    }
    
    public boolean onBlockDestroy(Player player, Block block) {
		if (player.canUseCommand("/townyadmin")) // Ignore PlayerZones when admin.
			return false;
		
        int zone = (towny.playerZone.get(player.getName()) == null) ? -1 : towny.playerZone.get(player.getName());
		if (zone == -1) {
			player.sendMessage(Colors.Rose + "Error: You don't have a playerZone.");
			player.sendMessage(Colors.Rose + "Notify admin. Shade (coder) needs to know what");
			player.sendMessage(Colors.Rose + "happened. Tell admin what you interactions with");
			player.sendMessage(Colors.Rose + "Towny this login. Thank you. -Shade");
            return true;
		}	
        
		// Stop destruction only if player doesn't belong to town.
        if (zone == 3 || zone == 5) {
			if (block.getStatus() == 3)
				player.sendMessage(Colors.Rose + "The nearby town's barrier prevents you from destruction.");
			return true;
		}
        
        // Otherwise, destroy it.
        return false;
    }
	
	public void onArmSwing(Player player) {
		if (towny.playerClaim.contains(player.getName())) {
			towny.claimSingleTownBlock(player);
		}
	}
	
	public boolean onTeleport(Player player, Location from, Location to) { 
		Object[] objs = {player, from, to};
		commandQueue.addWork(objs);
		return false;
	}
}