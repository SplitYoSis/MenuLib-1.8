package com.octanepvp.splityosis.menulib;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class MenuLib implements Listener {

    private static boolean isSetup = false;

    public static void setup(JavaPlugin plugin){
        if (isSetup) return;
        plugin.getServer().getPluginManager().registerEvents(new MenuLib(), plugin);
        isSetup = true;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory().getHolder() == null) return;
        if (!(e.getInventory().getHolder() instanceof Menu)) return;
        e.setCancelled(true);
        if (!(e.getClickedInventory().getHolder() instanceof Menu)) return;
        Menu menu = (Menu) e.getClickedInventory().getHolder();
        MenuItem menuItem = menu.getItem(e.getSlot());
        if (menuItem == null) return;
        menuItem.menuItemExecute.onClick(e, menu);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e){
        if (e.getInventory().getHolder() instanceof Menu)
            e.setCancelled(true);
    }

    public static boolean isIsSetup() {
        return isSetup;
    }
}