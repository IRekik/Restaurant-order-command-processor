public class Order {
    private int tableId;
    private int menuId;
    private int quantity;

    /**
     *
     * @param t
     * @param m
     * @param q
     */
    public Order(int t, int m, int q) {
        tableId = t;
        menuId = m;
        quantity = q;
    }

    /**
     *
     * @param i
     * @param m
     */
    public Order(int i, int m) {
        tableId = i;
        menuId = m;
        quantity = 1;
    }

    /**
     *
     * @return quantity
     */
    public int getQuantity () {
        return quantity;
    }

    /**
     *
     * @return menu ID
     */
    public int getMenuId () {
        return menuId;
    }

    /**
     *
     * @return table ID
     */
    public int getTableId() {
        return tableId;
    }
}
