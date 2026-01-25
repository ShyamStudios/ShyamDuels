package dev.piyush.shyamduels.arena;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class Arena {

    private final String name;
    private final String worldName;
    private Location corner1;
    private Location corner2;
    private Location spawn1;
    private Location spawn2;
    private Location center;
    private ArenaStatus status;
    private ArenaType type = ArenaType.DUEL;
    private final Set<String> allowedKits;

    private boolean buildEnabled = false;

    public Arena(String name, String worldName, Location corner1, Location corner2) {
        this.name = name;
        this.worldName = worldName;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.status = ArenaStatus.AVAILABLE;
        this.allowedKits = new HashSet<>();
    }

    public enum ArenaType {
        DUEL, FFA
    }

    public ArenaType getType() {
        return type;
    }

    public void setType(ArenaType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }

    public Location getCorner1() {
        return corner1;
    }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }

    public Location getSpawn1() {
        return spawn1;
    }

    public void setSpawn1(Location spawn1) {
        this.spawn1 = spawn1;
    }

    public Location getSpawn2() {
        return spawn2;
    }

    public void setSpawn2(Location spawn2) {
        this.spawn2 = spawn2;
    }

    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public ArenaStatus getStatus() {
        return status;
    }

    public void setStatus(ArenaStatus status) {
        this.status = status;
    }

    public Set<String> getAllowedKits() {
        return allowedKits;
    }

    public void addKit(String kitName) {
        allowedKits.add(kitName);
    }

    public void removeKit(String kitName) {
        allowedKits.remove(kitName);
    }

    public boolean isBuildEnabled() {
        return buildEnabled;
    }

    public void setBuildEnabled(boolean buildEnabled) {
        this.buildEnabled = buildEnabled;
    }

    public enum ArenaStatus {
        AVAILABLE,
        IN_USE,
        REGENERATING,
        DISABLED
    }
}
