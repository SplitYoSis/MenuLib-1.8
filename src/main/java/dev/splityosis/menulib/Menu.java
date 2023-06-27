package dev.splityosis.menulib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Menu implements InventoryHolder, Cloneable {

    public Menu parent;
    private Inventory inventory;
    private TitleUpdaterExecuter titleUpdaterExecuter;
    private int pageSize;
    private InventoryType inventoryType;

    private Map<Integer, MenuItem> staticItems = new HashMap<>();
    private List<Map<Integer, MenuItem>> pagesList = new ArrayList<>();
    private List<MenuItem> listedItems = new ArrayList<>();

    private int currentPage = 0;
    private Map<Integer, MenuItem> currentItems = new HashMap<>();

    public Menu(int pageSize) {
        this.pageSize = pageSize;
    }

    public Menu(InventoryType inventoryType) {
        this.inventoryType = inventoryType;
        this.pageSize = inventoryType.getDefaultSize();
    }

    /**
     * Gets the parent Menu which will be the menu that is opened when the back button is clicked.
     * @return The parent Menu or null if not set.
     */
    public Menu getParent() {
        return parent;
    }

    /**
     * Sets the parent Menu which will be the menu that is opened when the back button is clicked.
     * @param parent
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu setParent(Menu parent) {
        this.parent = parent;
        return this;
    }

    /**
     *
     * @return The title of the current page.
     */
    public String getTitle() {
        return getTitle(currentPage);
    }

    /**
     *
     * @param page
     * @return The title of given page
     */
    public String getTitle(int page) {
        if (titleUpdaterExecuter == null) return null;
        return titleUpdaterExecuter.getTitle(page, getPageItems(page));
    }

    /**
     * Sets a dynamic title
     * @param titleUpdaterExecuter
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu setTitle(TitleUpdaterExecuter titleUpdaterExecuter){
        this.titleUpdaterExecuter = titleUpdaterExecuter;
        return this;
    }

    /**
     * Sets a static title
     * @param title Constant title.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu setTitle(String title){
        setTitle((page, items) -> title);
        return this;
    }

    private String getTitle(int page, Map<Integer, MenuItem> items){
        if (titleUpdaterExecuter == null) return null;
        return titleUpdaterExecuter.getTitle(page, items);
    }

    /**
     * Sets a MenuItem in a specific page in a specific slot.
     * @param page The index of the page (starts from 0).
     * @param slot The index of the slot (starts from 0).
     * @param menuItem The MenuItem that will be set.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu setItem(int page, int slot, MenuItem menuItem){
        if (slot >= getPageSize()) return this;
        insurePage(page);
        Map<Integer, MenuItem> items = pagesList.get(page);
        items.put(slot, menuItem);
        pagesList.set(page, items);
        return this;
    }

    /**
     * Sets a MenuItem at given index, The page is dynamic meaning if the index is 19 and the size of the menu is 18 the MenuItem will be in page 2 slot 1.
     * @param index The index of where the MenuItem will be placed.
     * @param menuItem The MenuItem that will be set.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu setItem(int index, MenuItem menuItem){
        int page = index/getPageSize();
        int slot = index%getPageSize();
        if (slot >= getPageSize()) return this;
        insurePage(page);
        Map<Integer, MenuItem> items = pagesList.get(page);
        items.put(slot, menuItem);
        pagesList.set(page, items);
        return this;
    }

    /**
     * Sets a MenuItem as a listed item which will be set in the first available slot (after all the items with specified locations are set).
     * @param menuItem The MenuItem that will be added.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu addListedItem(MenuItem menuItem){
        listedItems.add(menuItem);
        return this;
    }

    /**
     * Removes a listed item.
     * @param menuItem The MenuItem that will be removed.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu removeListedItem(MenuItem menuItem){
        listedItems.add(menuItem);
        return this;
    }

    /**
     * Returns a modifiable list of listed items.
     * @return The list of listed items.
     */
    public List<MenuItem> getListedItems() {
        return listedItems;
    }

    /**
     * Replaces a listed item.
     * @param from The listed MenuItem you wish to replace.
     * @param to The replacement MenuItem.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu replaceListedItem(MenuItem from, MenuItem to){
        int i = listedItems.indexOf(from);
        if (i != -1)
            listedItems.set(i, to);
        return this;
    }

    /**
     * Updates the menu if changes were made.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu refresh(){
        currentItems = getPageItems(getCurrentPage());
        for (int i = 0; i < getPageSize(); i++)
            inventory.setItem(i, currentItems.get(i).getDisplayItem());
        return this;
    }

    /**
     * Sets a MenuItem in a specific slot in every page.
     * @param slot The slot the MenuItem will be set in.
     * @param menuItem The MenuItem that will be set.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu setStaticItem(int slot, MenuItem menuItem){
        if (slot >= getPageSize()) return this;
        staticItems.put(slot, menuItem);
        return this;
    }

    /**
     * Sets a MenuItem without saving it into the Menu data, The item will be there until the page is refreshed.
     * Note: Setting an item like this will override a listed item, meaning the listed item won't be shifted to
     * the next available slot it will just be removed until the page is refreshed.
     * @param slot The slot the MenuItem will be set in.
     * @param menuItem The MenuItem that will be set.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu setTemporaryItem(int slot, MenuItem menuItem){
        currentItems.put(slot, menuItem);
        inventory.setItem(slot, menuItem.getDisplayItem());
        return this;
    }

    /**
     * Sets a next page button.
     * @param slot The static slot the button will go in.
     * @param itemStack The ItemStack that the button will be.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu setNextPageButton(int slot, ItemStack itemStack){
        setStaticItem(slot, new MenuItem(itemStack).executes((event, menu) -> {
            if (menu.getCurrentPage() + 1 == menu.getPagesAmount()) return;
            menu.setPage(menu.getCurrentPage() + 1);
        }));
        return this;
    }

    /**
     * Sets a previous page button.
     * @param slot The static slot the button will go in.
     * @param itemStack The ItemStack that the button will be.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu setPreviousPageButton(int slot, ItemStack itemStack){
        setStaticItem(slot, new MenuItem(itemStack).executes((event, menu) -> {
            if (menu.getCurrentPage() == 0) return;
            menu.setPage(menu.getCurrentPage() - 1);
        }));
        return this;
    }

    /**
     * Sets a back button that will open the parent Menu.
     * @param slot The static slot the button will go in.
     * @param itemStack The ItemStack that the button will be.
     * @return The Menu instance (to allow continuous coding format).
     */
    public Menu setBackButton(int slot, ItemStack itemStack){
        setStaticItem(slot, new MenuItem(itemStack).executes((event, menu) -> {
            if (parent != null)
                parent.open((Player) event.getWhoClicked());
        }));
        return this;
    }

    /**
     * Opens the menu to given player.
     * @param player The player that will view the menu.
     */
    public void open(Player player){
        if (inventory == null){
            setPage(0);
        }
        player.openInventory(inventory);
    }

    /**
     *
     * @return The amount of slots per page.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Sets the page of the menu.
     * @param page The page index (starts from 0)
     */
    public void setPage(int page){
        Map<Integer, MenuItem> items = getPageItems(page);
        Inventory inv;

        if (inventoryType == null)
            inv = Bukkit.createInventory(this, pageSize, colorize(getTitle(page, items)));
        else
            inv = Bukkit.createInventory(this, inventoryType, colorize(getTitle(page, items)));

        for (Map.Entry<Integer, MenuItem> integerMenuItemEntry : items.entrySet()) {
            MenuItem menuItem = integerMenuItemEntry.getValue();
            if (menuItem != null)
                inv.setItem(integerMenuItemEntry.getKey(), menuItem.getDisplayItem());
        }

        this.currentItems = items;
        this.currentPage = page;
        Inventory old = this.inventory;
        this.inventory = inv;
        if (old != null){
            for (HumanEntity viewer : new ArrayList<>(old.getViewers())) {
                viewer.openInventory(inv);
            }
        }
    }

    /**
     * Gets the items that would be in certain page without opening it.
     * @param page
     * @return A map of slots with the menu items.
     */
    public Map<Integer, MenuItem> getPageItems(int page){
        Map<Integer, MenuItem> items = new HashMap<>(staticItems);
        if (pagesList.size() > page)
            items.putAll(pagesList.get(page));

        int listStart = 0;
        if (page > 0)
            for (int i = 0; i < page; i++)
                listStart += slotsForListedItems(i);

        if (listStart < listedItems.size())
            for (int i = 0; i < getPageSize(); i++) {
                if (items.containsKey(i)) continue;
                if (listStart >= listedItems.size()) break;
                items.put(i, listedItems.get(listStart));
                listStart++;
            }
        return items;
    }

    private int slotsForListedItems(int page){
        Set<Integer> slots = new HashSet<>(staticItems.keySet());
        if (pagesList.size() > page)
            slots.addAll(pagesList.get(page).keySet());
        return getPageSize() - slots.size();
    }

    private void insurePage(int page){
        int dif = pagesList.size() - page;
        if (dif > 0) return;
        dif--;
        dif *= -1;
        for (int i = 0; i < dif; i++) {
            pagesList.add(new HashMap<>(staticItems));
        }
    }

    /**
     *
     * @return Total amount of pages (last page + 1).
     */
    public int getPagesAmount(){
        Set<Integer> indexesOfUsedLocations = new HashSet<>();
        int page = 0;
        for (Map<Integer, MenuItem> integerMenuItemMap : pagesList) {
            for (Integer integer : integerMenuItemMap.keySet())
                indexesOfUsedLocations.add(integer + page*getPageSize());

            for (Integer integer : staticItems.keySet())
                indexesOfUsedLocations.add(integer + page*getPageSize());
            page++;
        }
        int spacesForListedItems = pagesList.size() * getPageSize() - indexesOfUsedLocations.size();
        int extraListedItems = listedItems.size() - spacesForListedItems;
        if (extraListedItems <= 0) return page;
        double more = (double) extraListedItems /(getPageSize() - staticItems.size());

        if (more == (int)more) return page + (int) more;
        return page + (int) more + 1;
    }


    /**
     *
     * @param slot
     * @return The current MenuItem in given slot.
     */
    public MenuItem getItem(int slot){
        return currentItems.get(slot);
    }

    /**
     *
     * @return A map of <slot,MenuItem> of the currently displayed items.
     */
    public Map<Integer, MenuItem> getCurrentItems() {
        return currentItems;
    }

    /**
     *
     * @return The page index the menu is set to (starts from 0).
     */
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Clones the Menu.
     * @return The clone.
     */
    public Menu clone(){
        try {
            return (Menu) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");
    private static String colorize(String str) {
        Matcher matcher = HEX_PATTERN.matcher(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', str));
        StringBuffer buffer = new StringBuffer();

        while (matcher.find())
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    private static List<String> colorize(List<String> lst){
        if (lst == null) return null;
        List<String> newList = new ArrayList<>();
        lst.forEach(s -> {
            newList.add(colorize(s));
        });
        return newList;
    }
}
