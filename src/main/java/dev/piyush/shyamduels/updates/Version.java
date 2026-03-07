package dev.piyush.shyamduels.updates;

public class Version implements Comparable<Version> {
    private final int major;
    private final int minor;
    private final int patch;

    public Version(String version) {
        String cleanVersion = version.replaceAll("[^0-9.]", "");
        String[] parts = cleanVersion.split("\\.");
        this.major = parts.length > 0 ? parseIntSafe(parts[0]) : 0;
        this.minor = parts.length > 1 ? parseIntSafe(parts[1]) : 0;
        this.patch = parts.length > 2 ? parseIntSafe(parts[2]) : 0;
    }

    private int parseIntSafe(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int compareTo(Version other) {
        if (this.major != other.major) {
            return Integer.compare(this.major, other.major);
        }
        if (this.minor != other.minor) {
            return Integer.compare(this.minor, other.minor);
        }
        return Integer.compare(this.patch, other.patch);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}
