package dev.piyush.shyamduels.kit;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class Kit {

    private final String name;
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private ItemStack offhand;
    private List<PotionEffect> effects;
    private ItemStack icon;

    public Kit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public void setEffects(List<PotionEffect> effects) {
        this.effects = effects;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    private List<org.bukkit.Material> buildWhitelist = new java.util.ArrayList<>();

    public List<org.bukkit.Material> getBuildWhitelist() {
        return buildWhitelist;
    }

    public void setBuildWhitelist(List<org.bukkit.Material> buildWhitelist) {
        this.buildWhitelist = buildWhitelist;
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

        if (effects != null) {
            for (PotionEffect effect : effects) {
                player.addPotionEffect(effect);
            }
        }

        if (offhand != null) {
            player.getInventory().setItemInOffHand(offhand.clone());
        }

        player.updateInventory();
    }
}
