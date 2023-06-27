package dev.splityosis.menulib;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class MenuItem implements Cloneable{

    private ItemStack displayItem;
    private MenuItemExecuter menuItemExecuter;
    private Sound sound;
    private float soundPitch = 1;
    private float soundVolume = 1;

    /**
     * @param displayItem The ItemStack that represents the MenuItem.
     */
    public MenuItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    /**
     * @param displayItem The ItemStack that represents the MenuItem.
     * @param sound The sound that will play when the MenuItem is clicked.
     */
    public MenuItem(ItemStack displayItem, Sound sound) {
        this.displayItem = displayItem;
        this.sound = sound;
    }

    /**
     *
     * @param displayItem The ItemStack that represents the MenuItem.
     * @param sound The sound that will play when the MenuItem is clicked.
     * @param soundVolume The volume that the sound will be played at.
     * @param soundPitch The pitch that the sound will be played at.
     */
    public MenuItem(ItemStack displayItem, Sound sound, float soundVolume, float soundPitch) {
        this.displayItem = displayItem;
        this.sound = sound;
        this.soundVolume = soundVolume;
        this.soundPitch = soundPitch;
    }

    /**
     * Sets the chunk of code that will be run when the item is clicked.
     * @param menuItemExecute An interface that holds the onClick(InventoryClickEvent, Menu) method.
     * @return The MenuItem instance (to allow continuous coding format).
     */
    public MenuItem executes(MenuItemExecuter menuItemExecute){
        this.menuItemExecuter = menuItemExecute;
        return this;
    }

    /**
     * @return The ItemStack that represents the MenuItem.
     */
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    /**
     * Sets the item that represents the MenuItem.
     * Note: this will not change how the item looks while the Menu is open, to do so you must call menu.refresh().
     * @param displayItem The ItemStack that represents the MenuItem.
     */
    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    /**
     * Clones the MenuItem.
     * @return The clone of the MenuItem.
     */
    public MenuItem clone(){
        try {
            return (MenuItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return The sounds that the MenuItem will play when clicked.
     */
    public Sound getSound() {
        return sound;
    }

    /**
     * Sets the sound that the MenuItem will play when clicked.
     * @param sound
     * @return The MenuItem instance (to allow continuous coding format).
     */
    public MenuItem setSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    /**
     * @return The pitch of the sound.
     */
    public float getSoundPitch() {
        return soundPitch;
    }

    /**
     * @return The volume of the sound.
     */
    public float getSoundVolume() {
        return soundVolume;
    }

    /**
     * Sets the pitch of the sound.
     * @param soundPitch
     * @return The MenuItem instance (to allow continuous coding format).
     */
    public MenuItem setSoundPitch(float soundPitch) {
        this.soundPitch = soundPitch;
        return this;
    }

    /**
     * Sets the volume of the sound.
     * @param soundVolume
     * @return The MenuItem instance (to allow continuous coding format).
     */
    public MenuItem setSoundVolume(float soundVolume) {
        this.soundVolume = soundVolume;
        return this;
    }

    public MenuItemExecuter getMenuItemExecuter() {
        return menuItemExecuter;
    }
}
