import java.util.*;

public class TownyTickerThread extends Thread {
	private boolean running;
	TownyThread towny;

	public TownyTickerThread(TownyThread towny) {
		super();
		this.towny = towny;
		setRunning(true);
	}
	
	public synchronized void setRunning(boolean running) {
		this.running = running;
	}
	
	public void run() {
		try {
			while (running) {
				/*if (TownyProperties.townRegen > 0) {
					for (Player player : etc.getServer().getPlayerList()) {
						Resident resident = towny.world.residents.get(player.getName());
						if (resident == null) continue;
						if (resident.town == null) continue;
						long[] posTownBlock = TownyUtil.getTownBlock((long)player.getX(), (long)player.getZ());
						String key = posTownBlock[0]+","+posTownBlock[1];
						TownBlock townblock = towny.world.townblocks.get(key);
						if (townblock == null) continue;
						if (townblock.town == null) continue;
						if (townblock.town == resident.town) {
							player.setHealth(player.getHealth()+TownyProperties.townRegen);
							continue;
						}
						if (resident.town.nation == null || townblock.town.nation == null) continue;
						if (resident.town.nation == townblock.town.nation ||
							resident.town.nation.friends.contains(townblock.town.nation)) {
							player.setHealth(player.getHealth()+TownyProperties.townRegen);
							continue;
						}
					}
				}*/
				
				if (TownyProperties.noMobsInTown) {
					try {
						List<Mob> mobs = etc.getServer().getMobList();
						for (int i = 0; i < mobs.size(); i++) {
							Mob mob = mobs.get(i);
							long[] posTownBlock = TownyUtil.getTownBlock((long)mob.getX(), (long)mob.getZ());
							String key = posTownBlock[0]+","+posTownBlock[1];
							TownBlock townblock = towny.world.townblocks.get(key);
							if (townblock == null) continue;
							if (townblock.town == null) continue;
							
							//delete mob
							//Workaround
							mob.teleportTo(mob.getX(), -50, mob.getZ(), 0, 0);
						}
					} catch(Exception cme) {} //You don't need to hold up the rest of the server for this.
				}
				
				sleep(1000);
			}
		} catch (InterruptedException e) {}
	}
}