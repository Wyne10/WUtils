package me.wyne.wutils.common.serialization;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

public class Base64InventorySerializer implements JsonSerializer<Inventory>, JsonDeserializer<Inventory> {

    @Override
    public Inventory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(json.getAsString()));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            Inventory inventory = Bukkit.createInventory(null, dataInput.readInt());
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            return inventory;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("An exception occurred trying to deserialize inventory from base64", e);
        }
    }

    @Override
    public JsonElement serialize(Inventory src, Type typeOfSrc, JsonSerializationContext context) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeInt(src.getSize());
            for (int i = 0; i < src.getSize(); i++) {
                dataOutput.writeObject(src.getItem(i));
            }
            return new JsonPrimitive(Base64Coder.encodeLines(outputStream.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException("An exception occurred trying to serialize inventory to base64", e);
        }
    }

}
