package dev.piyush.shyamduels.gui;

import dev.piyush.shyamduels.ShyamDuels;
import dev.piyush.shyamduels.config.GuiConfigLoader;
import dev.piyush.shyamduels.kit.Kit;
import dev.piyush.shyamduels.kit.PlayerKit;
import dev.piyush.shyamduels.util.ItemBuilder;
import dev.piyush.shyamduels.util.MessageUtils;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class KitEditorGui extends FastInv {

    private static final String GUI_KEY = "kit-editor";

    private final ShyamDuels plugin;
    private final Player player;
    private final Kit kit;
    private final PlayerKit playerKit;
    private final ItemStack[] backupInventory;
    private final ItemStack[] backupArmor;
    private final ItemStack backupOffhand;
    private boolean skipCloseTitle = false;

    private int slotInfo;
    private int slotSave;
    private int slotReset;
    private int slotExit;
    private int slotOffhand;
    private int[] slotArmor;

    public KitEditorGui(ShyamDuels plugin, Player player, Kit kit) {
        super(getGuiLoader().getSize(GUI_KEY),
                getGuiLoader().getTitle(GUI_KEY, Map.of("kit", kit.getName())));
        this.plugin = plugin;
        this.player = player;
        this.kit = kit;
        this.playerKit = plugin.getKitManager().getPlayerKit(player.getUniqueId(), kit.getName());
        this.backupInventory = player.getInventory().getContents();
        this.backupArmor = player.getInventory().getArmorContents();
        this.backupOffhand = player.getInventory().getItemInOffHand();

        loadSlots();
        initializeItems();

        addCloseHandler(e -> {
            if (!skipCloseTitle) {
                MessageUtils.sendTitle(player, "gui.kit-editor.title.exit");
            }
        });
    }

    private static GuiConfigLoader getGuiLoader() {
        return ShyamDuels.getInstance().getGuiConfigLoader();
    }

    private void loadSlots() {
        GuiConfigLoader loader = getGuiLoader();
        slotInfo = loader.getSlot(GUI_KEY, "info");
        slotSave = loader.getSlot(GUI_KEY, "save");
        slotReset = loader.getSlot(GUI_KEY, "reset");
        slotExit = loader.getSlot(GUI_KEY, "exit");
        slotOffhand = loader.getSlot(GUI_KEY, "offhand");
        slotArmor = loader.getSlots(GUI_KEY, "slots.armor");
        if (slotArmor.length < 4) {
            slotArmor = new int[] { 0, 1, 2, 3 };
        }
    }

    private void initializeItems() {
        GuiConfigLoader loader = getGuiLoader();

        ItemStack[] invContent = kit.getInventory();
        ItemStack[] armorContent = kit.getArmor();
        ItemStack offhandContent = kit.getOffhand();

        if (playerKit != null) {
            invContent = playerKit.getInventory();
            armorContent = playerKit.getArmor();
            offhandContent = playerKit.getOffhand();
        }

        if (armorContent != null && armorContent.length >= 4) {
            if (armorContent[0] != null && armorContent[0].getType() != Material.AIR) {
                setItem(slotArmor[0], armorContent[0].clone());
            }
            if (armorContent[1] != null && armorContent[1].getType() != Material.AIR) {
                setItem(slotArmor[1], armorContent[1].clone());
            }
            if (armorContent[2] != null && armorContent[2].getType() != Material.AIR) {
                setItem(slotArmor[2], armorContent[2].clone());
            }
            if (armorContent[3] != null && armorContent[3].getType() != Material.AIR) {
                setItem(slotArmor[3], armorContent[3].clone());
            }
        }

        setItem(slotInfo, loader.buildItemFromSection(GUI_KEY, "info", Material.BOOK,
                Map.of("kit", kit.getName())));
        setItem(slotSave, loader.buildItemFromSection(GUI_KEY, "save", Material.LIME_DYE, Map.of()));
        setItem(slotReset, loader.buildItemFromSection(GUI_KEY, "reset", Material.ORANGE_DYE, Map.of()));
        setItem(slotExit, loader.buildItemFromSection(GUI_KEY, "exit", Material.BARRIER, Map.of()));

        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 45; i < 54; i++) {
            if (i != slotSave && i != slotReset && i != slotExit) {
                setItem(i, glass);
            }
        }

        if (offhandContent != null && offhandContent.getType() != Material.AIR) {
            setItem(slotOffhand, offhandContent.clone());
        } else {
            ItemStack placeholder = loader.buildItemFromSection(GUI_KEY, "offhand", Material.SHIELD, Map.of());
            if (placeholder != null) {
                placeholder = new ItemBuilder(placeholder).tag("offhand_placeholder", "true").build();
            }
            setItem(slotOffhand, placeholder);
        }
        
        if (invContent != null) {
            for (int i = 0; i < Math.min(invContent.length, 36); i++) {
                if (invContent[i] != null && invContent[i].getType() != Material.AIR) {
                    setItem(9 + i, invContent[i].clone());
                }
            }
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();
        int rawSlot = event.getRawSlot();

        if (rawSlot >= getInventory().getSize()) {
            event.setCancelled(true);
            return;
        }

        boolean isArmorSlot = false;
        int armorSlotIndex = -1;
        for (int i = 0; i < slotArmor.length; i++) {
            if (slot == slotArmor[i]) {
                isArmorSlot = true;
                armorSlotIndex = i;
                break;
            }
        }

        if (slot == slotSave) {
            event.setCancelled(true);
            saveKit();
            return;
        }
        if (slot == slotReset) {
            event.setCancelled(true);
            plugin.getKitManager().resetPlayerKit(player.getUniqueId(), kit.getName());
            MessageUtils.sendMessage(player, "gui.kit-editor.messages.reset");
            player.closeInventory();
            return;
        }
        if (slot == slotExit) {
            event.setCancelled(true);
            restoreInventory();
            player.closeInventory();
            return;
        }
        if (slot == slotInfo) {
            event.setCancelled(true);
            return;
        }

        if (slot >= 45 && slot < 54 && slot != slotSave && slot != slotReset && slot != slotExit) {
            event.setCancelled(true);
            return;
        }

        if (isArmorSlot) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }
            if (event.isRightClick() && event.getCursor().getType() == Material.AIR) {
                event.setCancelled(true);
                if (player.hasPermission("shyamduels.vip")) {
                    ItemStack item = getInventory().getItem(slot);
                    if (item != null && item.getType() != Material.AIR) {
                        if (item.getItemMeta() instanceof org.bukkit.inventory.meta.ArmorMeta) {
                            skipCloseTitle = true;
                            new ArmorTrimGui(plugin, player, item, (trimmedItem) -> {
                                skipCloseTitle = false;
                                if (trimmedItem != null) {
                                    setItem(slot, trimmedItem);
                                }
                                this.open(player);
                            }).open(player);
                        } else {
                            MessageUtils.sendMessage(player, "gui.kit-editor.messages.hold-armor");
                        }
                    } else {
                        MessageUtils.sendMessage(player, "gui.kit-editor.messages.hold-armor");
                    }
                } else {
                    MessageUtils.sendMessage(player, "gui.kit-editor.messages.no-permission");
                }
                return;
            }
            if (!isValidArmorForSlot(event.getCursor(), armorSlotIndex)) {
                event.setCancelled(true);
                MessageUtils.sendMessage(player, "gui.kit-editor.messages.invalid-armor-slot");
                return;
            }
            event.setCancelled(false);
        } else if (slot == slotOffhand) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }
            ItemStack currentItem = getInventory().getItem(slot);
            if (currentItem != null && currentItem.hasItemMeta()) {
                org.bukkit.persistence.PersistentDataContainer pdc = currentItem.getItemMeta().getPersistentDataContainer();
                org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(plugin, "offhand_placeholder");
                if (pdc.has(key, org.bukkit.persistence.PersistentDataType.STRING)) {
                    if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                        setItem(slot, event.getCursor().clone());
                        event.getCursor().setAmount(0);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            event.setCancelled(false);
        } else if (slot >= 9 && slot <= 44) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }
            event.setCancelled(false);
        } else {
            event.setCancelled(true);
        }
    }
    
    private boolean isValidArmorForSlot(ItemStack item, int armorSlotIndex) {
        if (item == null || item.getType() == Material.AIR) {
            return true;
        }
        
        String itemName = item.getType().name();
        
        switch (armorSlotIndex) {
            case 0:
                return itemName.endsWith("_BOOTS");
            case 1:
                return itemName.endsWith("_LEGGINGS");
            case 2:
                return itemName.endsWith("_CHESTPLATE") || itemName.equals("ELYTRA");
            case 3:
                return itemName.endsWith("_HELMET") || itemName.equals("TURTLE_HELMET") || 
                       itemName.equals("CARVED_PUMPKIN") || itemName.equals("PLAYER_HEAD") ||
                       itemName.equals("ZOMBIE_HEAD") || itemName.equals("SKELETON_SKULL") ||
                       itemName.equals("WITHER_SKELETON_SKULL") || itemName.equals("CREEPER_HEAD") ||
                       itemName.equals("DRAGON_HEAD") || itemName.equals("PIGLIN_HEAD");
            default:
                return false;
        }
    }

    private void saveKit() {
        ItemStack[] armor = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            ItemStack armorPiece = getInventory().getItem(slotArmor[i]);
            if (armorPiece != null && armorPiece.getType() != Material.AIR) {
                armor[i] = armorPiece.clone();
            } else {
                armor[i] = null;
            }
        }

        ItemStack offhand = getInventory().getItem(slotOffhand);
        
        if (offhand != null && offhand.hasItemMeta()) {
            org.bukkit.persistence.PersistentDataContainer pdc = offhand.getItemMeta().getPersistentDataContainer();
            org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(plugin, "offhand_placeholder");
            if (pdc.has(key, org.bukkit.persistence.PersistentDataType.STRING)) {
                offhand = null;
            } else if (offhand.getType() == Material.AIR) {
                offhand = null;
            } else {
                offhand = offhand.clone();
            }
        } else if (offhand == null || offhand.getType() == Material.AIR) {
            offhand = null;
        } else {
            offhand = offhand.clone();
        }

        ItemStack[] inv = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            ItemStack item = getInventory().getItem(9 + i);
            inv[i] = (item != null && item.getType() != Material.AIR) ? item.clone() : null;
        }

        PlayerKit newPk = new PlayerKit(player.getUniqueId(), kit.getName());
        newPk.setInventory(inv);
        newPk.setArmor(armor);
        newPk.setOffhand(offhand);

        plugin.getKitManager().savePlayerKit(newPk);
        MessageUtils.sendMessage(player, "gui.kit-editor.messages.saved");
        restoreInventory();
        player.closeInventory();
    }

    private void restoreInventory() {
        player.getInventory().clear();
        player.getInventory().setContents(backupInventory);
        player.getInventory().setArmorContents(backupArmor);
        player.getInventory().setItemInOffHand(backupOffhand);
        player.updateInventory();
    }
}
