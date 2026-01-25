package dev.piyush.shyamduels.util;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.arena.Arena;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FaweUtils {

    public static void saveSchematic(Arena arena) {
        if (arena.getCorner1() == null || arena.getCorner2() == null)
            return;

        File file = new File(ShyamDuels.getInstance().getDataFolder(), "schematics/" + arena.getName() + ".schem");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        BlockVector3 p1 = BlockVector3.at(arena.getCorner1().getBlockX(), arena.getCorner1().getBlockY(),
                arena.getCorner1().getBlockZ());
        BlockVector3 p2 = BlockVector3.at(arena.getCorner2().getBlockX(), arena.getCorner2().getBlockY(),
                arena.getCorner2().getBlockZ());

        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(arena.getCorner1().getWorld());
        Region region = new CuboidRegion(world, p1, p2);

        TaskManager.taskManager().async(() -> {
            try (EditSession session = WorldEdit.getInstance().newEditSession(world)) {
                Clipboard clipboard = new com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard(region);

                com.sk89q.worldedit.function.operation.ForwardExtentCopy copy = new com.sk89q.worldedit.function.operation.ForwardExtentCopy(
                        world, region, clipboard, region.getMinimumPoint());
                Operations.complete(copy);

                ClipboardFormat format = ClipboardFormats.findByAlias("schem");
                if (format != null) {
                    try (ClipboardWriter writer = format.getWriter(new FileOutputStream(file))) {
                        writer.write(clipboard);
                    }
                } else {
                    ShyamDuels.getInstance().getLogger().warning("Could not find 'schem' clipboard format.");
                }

                Bukkit.getScheduler().runTask(ShyamDuels.getInstance(),
                        () -> ShyamDuels.getInstance().getLogger().info("Saved schematic for " + arena.getName()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void pasteSchematic(Arena arena) {
        File file = new File(ShyamDuels.getInstance().getDataFolder(), "schematics/" + arena.getName() + ".schem");
        if (!file.exists())
            return;

        TaskManager.taskManager().async(() -> {
            ClipboardFormat format = ClipboardFormats.findByAlias("schem");
            if (format == null)
                return;

            try (FileInputStream fis = new FileInputStream(file);
                    ClipboardReader reader = format.getReader(fis)) {
                Clipboard clipboard = reader.read();

                com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(arena.getCorner1().getWorld());

                try (EditSession session = WorldEdit.getInstance().newEditSession(world);
                        ClipboardHolder holder = new ClipboardHolder(clipboard)) {

                    int minX = Math.min(arena.getCorner1().getBlockX(), arena.getCorner2().getBlockX());
                    int minY = Math.min(arena.getCorner1().getBlockY(), arena.getCorner2().getBlockY());
                    int minZ = Math.min(arena.getCorner1().getBlockZ(), arena.getCorner2().getBlockZ());

                    Operation operation = holder
                            .createPaste(session)
                            .to(BlockVector3.at(minX, minY, minZ))
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                }

                arena.setStatus(Arena.ArenaStatus.AVAILABLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
