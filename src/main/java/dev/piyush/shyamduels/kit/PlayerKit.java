package dev.piyush.shyamduels.kit;

import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class PlayerKit {

    private final UUID playerUuid;
    private final String kitName;
    private ItemStack[] inventory;

    private ItemStack[] armor;
    private ItemStack offhand;
    private long lastEdited;

    public PlayerKit(UUID playerUuid, String kitName) {
        this.playerUuid = playerUuid;
        this.kitName = kitName;
        this.lastEdited = System.currentTimeMillis();
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getKitName() {
        return kitName;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public ItemStack getOffhand() {
        return offhand;
    }

    public void setOffhand(ItemStack offhand) {
        this.offhand = offhand;
    }

    public long getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(long lastEdited) {
        this.lastEdited = lastEdited;
    }

    public void apply(org.bukkit.entity.Player player) {
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
        if (inventory != null) {
            ItemStack[] contents = new ItemStack[inventory.length];
            for (int i = 0; i < inventory.length; i++) {
                if (inventory[i] != null)
                    contents[i] = inventory[i].clone();
            }
            player.getInventory().setStorageContents(contents);
        }
        if (armor != null) {
            ItemStack[] armorCopy = new ItemStack[armor.length];
            for (int i = 0; i < armor.length; i++) {
                if (armor[i] != null)
                    armorCopy[i] = armor[i].clone();
            }
            player.getInventory().setArmorContents(armorCopy);
        }

        if (offhand != null) {
            player.getInventory().setItemInOffHand(offhand.clone());
        }

        player.updateInventory();
    }
}
