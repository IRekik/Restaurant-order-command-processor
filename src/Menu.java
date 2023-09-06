public class Menu {
    private int menuID;
    private int stockQuantity;
    private int price;

    /**
     *
     * @param m
     * @param i
     * @param p
     */
    public Menu (int m, int i, int p) {
        menuID = m;
        stockQuantity = i;
        price = p;
    }

    /**
     *
     * @return menu ID
     */
    public int getMenuID () {
        return menuID;
    }

    /**
     *
     * @return stock quantity
     */
    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     *
     * @return price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sells a quantity of the menu and reduces the stock by 1
     */
    public void sell() {
        stockQuantity--;
    }
}
