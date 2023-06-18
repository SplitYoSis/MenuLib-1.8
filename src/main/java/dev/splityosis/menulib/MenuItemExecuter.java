package dev.splityosis.menulib;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface MenuItemExecuter {

    void onClick(InventoryClickEvent event, Menu menu);
}
