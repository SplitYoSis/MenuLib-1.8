package dev.splityosis.menulib;

import org.bukkit.inventory.ItemStack;

public class MenuItem implements Cloneable{

    private ItemStack displayItem;
    private MenuItemExecuter menuItemExecuter;

    public MenuItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public MenuItem executes(MenuItemExecuter menuItemExecute){
        this.menuItemExecuter = menuItemExecute;
        return this;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public MenuItem clone(){
        try {
            return (MenuItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public MenuItemExecuter getMenuItemExecuter() {
        return menuItemExecuter;
    }
}
