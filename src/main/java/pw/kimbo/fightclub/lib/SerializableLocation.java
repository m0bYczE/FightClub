package pw.kimbo.fightclub.lib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;
import java.util.Map;


public class SerializableLocation extends Location implements ConfigurationSerializable {

    public SerializableLocation(Location location) {
        this(location.getWorld(), location.getX(), location.getY(), location.getZ());
    }

    public SerializableLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public Map<String,Object> serialize() {
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("world", getWorld().getName());
        m.put("x", getX());
        m.put("y", getY());
        m.put("z", getZ());
        return m;
    }

    public static Location deserialize(Map<String,Object> m) {
        World w = Bukkit.getServer().getWorld((String) m.get("world"));
        if (w == null) {
            throw new IllegalArgumentException("Non-existent world.");
        }
        return new Location(w, (Double) m.get("x"), (Double) m.get("y"), (Double) m.get("z"));
    }
}
