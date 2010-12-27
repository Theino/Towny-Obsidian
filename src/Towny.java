

import java.util.logging.Logger;

public class Towny extends Plugin {
    protected static final Logger log = Logger.getLogger("Minecraft");
    private TownyThread towny;
	private TownyTickerThread tickerThread;
    private CommandQueue<Object> commandQueue;
    public TownyListener listener;
    
    public Towny() {
		listener = new TownyListener();
	}
    
    public void enable() throws NullPointerException {
		commandQueue = new CommandQueue<Object>();
        towny = new TownyThread(commandQueue);
		tickerThread = new TownyTickerThread(towny);
		
        listener.towny = towny;
		listener.commandQueue = commandQueue;
		
        if (towny.load() && towny.loadData()) {
			towny.updateAllPlayerZones();
            towny.start();
			tickerThread.start();
            towny.world.updatePopulationCount();
            log.info("[Towny] Beta 1.9 - Mod Enabled.");
        } else {
            log.info("[Towny] Mod failed to load.");
            //Disable this plugin
        }
		
		for (Player player : etc.getServer().getPlayerList())
			listener.onLogin(player);
    }
    
    public void disable() {
		commandQueue.addWork(TownyThread.NO_MORE_WORK);
		tickerThread.setRunning(false);
		BlockQueue.getInstance().getThread().setRunning(false);
		log.info("[Towny] Mod Disabled.");
    }

    public void initialize() {
        etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED, listener, this, PluginListener.Priority.MEDIUM);
        etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ARM_SWING, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.DAMAGE, listener, this, PluginListener.Priority.MEDIUM);
    }
}