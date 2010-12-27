import java.util.*;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

public class Resident {
    protected static final Logger log = Logger.getLogger("Minecraft");
   // private final String newLine = System.getProperty("line.separator");
    public String name;
    public long lastLogin;
    public Town town;
    public boolean isMayor;
	public boolean isKing;
	public boolean isActive;
	public ArrayList<Resident> friends;

    public Resident(String name) {
        this(name, false);
    }
    
    public Resident(String name, boolean login) {
        this.name = name;
        if (login)
            lastLogin = System.currentTimeMillis();
        else
            lastLogin = 0;
        isMayor = false;
		isKing = false;
		isActive = false;
		friends = new ArrayList<Resident>();
    }
	
	public boolean addFriend(Resident resident) {
        if (!friends.contains(resident)) {
            friends.add(resident);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean remFriend(Resident resident) {
        if (friends.contains(resident)) {
            friends.remove(resident);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean remAllFriends() {
        friends.clear(); 
        if (friends.size() == 0)
            return true;
        else
            return false;
    }
    
    public String getLastLogin() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMMM dd");
        return sdf.format(lastLogin);
    }
    
    public String toString() {
        if (town != null && this == town.mayor) {
            if (town.nation != null && town == town.nation.capital)
                return TownyProperties.getKingPrefix(town.nation.activeResidents) + name;
            else
                return TownyProperties.getMayorPrefix(town.activeResidents) + name;
        } else {
            return name;
        }
    }
    
    public ArrayList<String> getStatus() {
        ArrayList<String> out = new ArrayList<String>();
        
        // ___[ King Harlus ]___
        out.add(ChatTools.formatTitle(toString()));
        
        // Last Online: March 7
        out.add(Colors.Green + "Last Online: " + Colors.LightGreen + getLastLogin());
        
        // Town: Camelot
        String line = Colors.Green + "Town: " + Colors.LightGreen;
        if (town == null) {
            line += "None";
        } else {
            line += town;
        }
        out.add(line);
		
		// Friends [12]:
        // James, Carry, Mason
        out.add(Colors.Green + "Friends " + Colors.LightGreen + "[" + friends.size() + "]" + Colors.Green + ":");
        out.addAll(ChatTools.list(friends.toArray()));
        
        return out;
    }
    
    public void leaveTown() {
        if (town != null)
             town.remResident(this); 
    }
}