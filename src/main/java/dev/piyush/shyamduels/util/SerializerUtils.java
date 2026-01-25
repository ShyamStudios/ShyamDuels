package dev.piyush.shyamduels.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SerializerUtils {

    public static String itemStackArrayToBase64(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) {
        if (data == null || data.isEmpty())
            return new ItemStack[0];
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack[] items = new ItemStack[dataInput.readInt()];
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack[0];
        }
    }

    public static String potionEffectsToString(List<PotionEffect> effects) {
        StringBuilder sb = new StringBuilder();
        for (PotionEffect effect : effects) {
            sb.append(effect.getType().getKey().getKey()).append(":")
                    .append(effect.getDuration()).append(":")
                    .append(effect.getAmplifier()).append(";");
        }
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    public static List<PotionEffect> potionEffectsFromString(String data) {
        List<PotionEffect> effects = new ArrayList<>();
        if (data == null || data.isEmpty())
            return effects;

        String[] split = data.split(";");
        for (String s : split) {
            if (s.isEmpty())
                continue;
            String[] parts = s.split(":");
            if (parts.length == 3) {
                try {
                    org.bukkit.potion.PotionEffectType type = org.bukkit.Registry.POTION_EFFECT_TYPE
                            .get(org.bukkit.NamespacedKey.minecraft(parts[0].toLowerCase()));
                    if (type == null) {
                        type = org.bukkit.potion.PotionEffectType.getByName(parts[0]);
                    }

                    int duration = Integer.parseInt(parts[1]);
                    int amplifier = Integer.parseInt(parts[2]);
                    if (type != null) {
                        effects.add(new PotionEffect(type, duration, amplifier));
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return effects;
    }
}
