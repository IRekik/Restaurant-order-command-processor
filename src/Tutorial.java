import java.util.*;

public class Tutorial {

    public static void main(String[] args) {
        String[] lines = getStdin();
        String input = String.format(lines[0]);
        switch (Integer.parseInt(input)) {
            case 1:
                step1(lines);
                break;
            case 2:
                step2(lines);
                break;
            case 3:
                step3(lines);
                break;
            case 4:
                step4(lines);
                break;
        }

    }

    private static String[] getStdin() {
        Scanner scanner = new Scanner(System.in);
        ArrayList< String > lines = new ArrayList < > ();
        while (scanner.hasNext()) {
            lines.add(scanner.nextLine());
            if (lines.get(lines.size() - 1).equals("q")) {
                lines.remove("q");
                break;
            }
        }
        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Description: The method to execute step 1 of the assessment
     * @param       input
     * @return      None
     */
    private static List <String> step1(String[] input) {
        // Retrieve and store menu items in Menu objects
        int numOfMenus = Integer.parseInt(input[1]);
        Menu[] listOfMenus = storeMenuItems(input, numOfMenus);

        // Retrieve and store orders in Order objects
        int numOfOrders = input.length - (2 + numOfMenus);
        Order[] listOfOrders = storeOrders(input, numOfOrders);

        int sales = 0;
        boolean soldOutOnce = false;
        List <String> acceptedOrderList = new ArrayList <> ();

        // For each order, get ID of that order, find the menu with the same ID, sell menu as long as it
        // is not out of stock. Else, print that menu is sold out once
        for (int i = 0; i < listOfOrders.length; i++) {
            int id = listOfOrders[i].getMenuId();
            for (int j = 0; j < listOfMenus.length; j++) {
                boolean isOutOfStock = false;
                if (listOfMenus[j].getMenuID() == id) {
                    for (int k = 0; k < listOfOrders[i].getQuantity(); k++) {
                        if (listOfMenus[j].getStockQuantity() >= listOfOrders[i].getQuantity()
                                || isOutOfStock) {
                            isOutOfStock = true;
                            soldOutOnce = true;
                            listOfMenus[j].sell();
                            sales++;
                            String orderMessage = "received order " + listOfOrders[i].getTableId()
                                    + " " + id;
                            acceptedOrderList.add(orderMessage);
                            System.out.println(orderMessage);
                        }
                        else {
                            if (soldOutOnce) {
                                System.out.println("sold out " + listOfOrders[i].getTableId());
                                soldOutOnce = false;
                            }
                        }
                    }
                }
            }
        }
        return acceptedOrderList;
    }

    /**
     * The method that contains all the logic for the part 2 of the assessment
     * @param   input
     * @return  None
     */
    private static void step2(String[] input) {
        String secondLine = input[1];
        int numOfMenus = Integer.parseInt(secondLine.substring(0, secondLine.indexOf(" ")));
        int numOfMicrowaves = Integer.parseInt(secondLine.substring(secondLine.indexOf(" ") + 1));

        Menu[] listOfMenus = storeMenuItems(input, numOfMenus);

        List <Order> microwaveOrders = new ArrayList <> ();
        Deque <Order> microwaveQueue = new LinkedList <>();
        // For each command in input, parse command and execute the appropriate operations
        for (int i = 2 + numOfMenus; i < input.length; i++) {
            // if the command is a reception, add order to microwaves if at least one is available
            // and add to microwave wait queue if all are full
            if (isCommandReception(input[i])) {
                int[] orderValues = getOrderInfo(input[i]);
                if (microwaveOrders.size() < numOfMicrowaves) {
                    microwaveOrders.add(new Order(orderValues[0], orderValues[1]));
                    System.out.println(orderValues[1]);
                } else {
                    microwaveQueue.add(new Order(orderValues[0], orderValues[1]));
                    System.out.println("wait");
                }
            }
            // else when the command is a completion, verify if the menuId is under preparation
            // and prints the id. If not, prompt error message.
            else {
                int menuId = getCompletionNumber(input[i]);
                if (hasOrderInsideMicrowave(microwaveOrders, menuId)) {
                    Order currentOrder = microwaveOrders.get(microwaveOrders.size() - 1);
                    updateInventory(currentOrder, listOfMenus);
                    microwaveOrders = removeCompletedOrder(microwaveOrders, menuId);
                    System.out.print("ok");
                    if (!microwaveQueue.isEmpty()) {
                        microwaveOrders.add(microwaveQueue.removeFirst());
                        int orderId = microwaveOrders.get(microwaveOrders.size() - 1).getMenuId();
                        System.out.println(" " + orderId);
                    }
                } else {
                    System.out.println("unexpected input");
                }
            }
        }

    }

    /**
     * Contains the logic and operations for the part 3
     * @param   input
     * @return  None
     */
    private static void step3(String[] input) {
        int numOfMenus = Integer.parseInt(input[1]);
        Menu[] listOfMenus = storeMenuItems(input, numOfMenus);
        Deque < Order > orderList = new LinkedList < > ();

        for (int i = 2 + numOfMenus; i < input.length; i++) {
            // If current line is a "received order", store it inside a list
            if (isCommandReception(input[i])) {
                int[] values = getOrderInfo(input[i]);
                orderList.add(new Order(values[0], values[1]));
            }
            // Else, when it's a "complete", remove order from list
            else {
                int orderId = getCompletionNumber(input[i]);
                Order completeOrder = findOrder(orderList, orderId);
                orderList.remove(completeOrder);
                updateInventory(completeOrder, listOfMenus);
                System.out.println("ready " + completeOrder.getTableId() + " " + completeOrder.getMenuId());
            }
        }
    }

    /**
     * The method that contains the logic for the step 4 of the assessment
     * @param   input
     * @Return  None
     */
    private static void step4(String[] input) {
        int numOfMenus = Integer.parseInt(input[1]);
        Menu[] listOfMenus = storeMenuItems(input, numOfMenus);
        boolean receiveFlag = false;
        boolean readyFlag = false;
        Order currentOrder = null;

        // For each command,
        for (int i = 2 + numOfMenus; i < input.length; i++) {
            String firstWord = getFirstWord(input[i]);
            if (firstWord.equals("received")) {
                int[] values = getOrderInfo(input[i]);
                currentOrder = new Order(values[0], values[1]);
                receiveFlag = true;
            }
            if (firstWord.equals("ready")) {
                readyFlag = true;
                updateInventory(currentOrder, listOfMenus);
            }
            if (firstWord.equals("check")) {
                if (readyFlag && receiveFlag) {
                    System.out.println(findMenuPrice(currentOrder, listOfMenus));
                    receiveFlag = false;
                    readyFlag = false;
                } else {
                    System.out.println("please wait");
                }
            }
        }
    }

    /**
     * Description  Method that takes a number of menus and a string containing
     *              menus and split it into Menu objects and return those in a
     *              Menu array. Used in part 1, 3 and 4.
     * @param       input
     * @param       numOfMenus
     * @return      An array of Menu objects
     */
    private static Menu[] storeMenuItems(String[] input, int numOfMenus) {
        int menuItemId;
        int initialStock;
        int priceLand;

        Menu[] listOfMenus = new Menu[numOfMenus];

        // For each menu line, retrieve menu information, create a menu object and
        // store it inside the listOfMenus
        for (int i = 0; i < numOfMenus; i++) {
            String line = input[i + 2];
            StringTokenizer st = new StringTokenizer(line);
            int[] menuInfos = new int[3];
            for (int j = 0; j < 3; j++) {
                menuInfos[j] = Integer.parseInt(st.nextToken());
            }
            menuItemId = menuInfos[0];
            initialStock = menuInfos[1];
            priceLand = menuInfos[2];
            listOfMenus[i] = new Menu(menuItemId, initialStock, priceLand);
        }
        return listOfMenus;
    }

    /**
     * Takes a number of orders and a string containing these orders,
     * parse it to create Order objects and returns an array of Orders.
     * Used in part 1.
     * @param   input
     * @param   numOfOrders
     * @return  An array containing all the orders
     */
    private static Order[] storeOrders(String[] input, int numOfOrders) {
        int numOfMenus = Integer.parseInt(input[1]);

        int tableId;
        int menuOrderId;
        int quantity;

        // For each line of orders, retrieve order information, create an
        // Order object and add it to listOfOrders
        Order[] listOfOrders = new Order[numOfOrders];
        for (int i = 0; i < numOfOrders; i++) {
            String line = input[i + 2 + numOfMenus];
            StringTokenizer st = new StringTokenizer(line);
            st.nextToken();
            int[] orderInfos = new int[3];
            for (int j = 0; j < numOfOrders; j++) {
                orderInfos[j] = Integer.parseInt(st.nextToken());
            }
            tableId = orderInfos[0];
            menuOrderId = orderInfos[1];
            quantity = orderInfos[2];
            listOfOrders[i] = new Order(tableId, menuOrderId, quantity);
        }
        return listOfOrders;
    }

    /**
     * Determines if the command is a reception of completion method.
     * Used in part 2 and 3.
     * @param   command
     * @return  A boolean if command is a reception or not
     */
    private static boolean isCommandReception(String command) {
        if (command.startsWith("received")) {
            return true;
        } else if (command.startsWith("complete")) {
            return false;
        }
        return false;
    }

    /**
     * Retrieves order ID and table number of a receive command
     * @param   command
     * @return  an array of 2 elements containing order ID and table number
     */
    private static int[] getOrderInfo(String command) {
        StringTokenizer st = new StringTokenizer(command);
        String[] values = new String[4];
        int counter = 0;
        while (st.hasMoreTokens()) {
            values[counter] = st.nextToken();
            counter++;
        }
        return new int[] {
                Integer.parseInt(values[2]), Integer.parseInt(values[3])
        };
    }

    /**
     * Retrieves order ID of a completion command
     * @param   command
     * @return  The ID of the ordre that has been completed as an int
     */
    private static int getCompletionNumber(String command) {
        String menuId = command.substring(9);
        int number = Integer.parseInt(menuId);
        return number;
    }

    /**
     * Checks if an order with a given menu ID is currently being microwaved.
     * @param   microwaveOrders
     * @param   menuId
     * @return  Whether the order is inside a microwave or not, true or false
     */
    private static boolean hasOrderInsideMicrowave(List <Order> microwaveOrders, int menuId) {
        for (Order o: microwaveOrders) {
            if (o.getMenuId() == menuId) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param microwaveOrders
     * @param menuId
     * @return
     */
    private static List < Order > removeCompletedOrder(List <Order> microwaveOrders, int menuId) {
        microwaveOrders.removeIf(o -> o.getMenuId() == menuId);
        return microwaveOrders;
    }

    /**
     * Finds the first order in a list of order that matches with the provided orderId.
     * Used in step 3
     * @param   orderList
     * @param   orderId
     * @return  The order that matches the orderId
     */
    private static Order findOrder(Deque <Order> orderList, int orderId) {
        for (Order o: orderList) {
            if (o.getMenuId() == orderId) {
                return o;
            }
        }
        return null;
    }

    /**
     * Retrieve first word of a sentence, used to know if the command mentions
     * "received", "check" or "ready". Used in part 4.
     * @param   command
     * @return  the first word of the sentence as a string
     */
    public static String getFirstWord(String command) {
        String firstWord = command.substring(0, command.indexOf(" "));
        return firstWord;
    }

    /**
     * Finds the price of the menu that has been ordered, returns the price. Used
     * in part 4.
     * @param   currentOrder
     * @param   listOfMenus
     * @return  The price of the menu that has been ordered
     */
    public static int findMenuPrice(Order currentOrder, Menu[] listOfMenus) {
        for (int i = 0; i < listOfMenus.length; i++) {
            if (listOfMenus[i].getMenuID() == currentOrder.getMenuId()) {
                return listOfMenus[i].getPrice();
            }
        }
        return -1;
    }

    /**
     * Given an order and the list of menus, finds the order menu in the list and
     * update the inventory to reduce by one quantity.
     * @param currentOrder
     * @param listOfMenus
     */
    public static void updateInventory(Order currentOrder, Menu[] listOfMenus) {
        Menu item = null;
        for (int i = 0; i < listOfMenus.length; i++) {
            if (listOfMenus[i].getMenuID() == currentOrder.getMenuId()) {
                item = listOfMenus[i];
            }
        }
        item.sell();
    }
}