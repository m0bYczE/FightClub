package pw.kimbo.fightclub.lib;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;

public class Arena implements ConfigurationSerializable {

    private final String name;
    private Location locationOne;
    private Location locationTwo;

    private boolean inUse;


    private static final Map<String, Arena> arenas;

    static {
        arenas = new HashMap<String, Arena>();
    }

    public Arena(String name, Location one, Location two) {
        this.name = name;
        locationOne = one;
        locationTwo = two;
        inUse = false;
    }

    public Arena(String name) {
        this(name,null, null);
    }

    public Location getLocationOne() {
        return locationOne;
    }

    public Location getLocationTwo() {
        return locationTwo;
    }

    public boolean isValid() {
        return locationOne != null && locationTwo != null;
    }

    public String getName() {
        return name;
    }

    public void setLocationOne(Location locationOne) {
        this.locationOne = locationOne;
    }

    public void setLocationTwo(Location locationTwo) {
        this.locationTwo = locationTwo;
    }
    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("name", name);
        result.put("locationOne", locationOne == null ? null : new SerializableLocation(locationOne));
        result.put("locationTwo", locationTwo == null ? null : new SerializableLocation(locationTwo));
        return result;
    }

    public static Arena deserialize(Map<String, Object> map) {
        String name = (String)map.get("name");
        Location one = (Location)map.get("locationOne");
        Location two = (Location)map.get("locationTwo");
        return new Arena(name, one, two);
    }

    public static Map<String, Arena> getArenas() {
        return arenas;
    }
}
