package ru.spbau.mit;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static ru.spbau.mit.SecondPartTasks.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        List<String> paths1 = Arrays.asList("src/test/resources/stanza1.txt",
                                           "src/test/resources/stanza2.txt",
                                           "src/test/resources/stanza3.txt",
                                           "src/test/resources/stanza4.txt");
        List<String> quotes =
                Arrays.asList("There is many a free French peasant who is richer and sadder than we.",
                              "The fine French kings came over in a flutter of flags and dames.",
                              "The blood ran red to Bosworth and the high French lords went down;",
                              "Americans, Frenchmen, Irish; but we knew not the things they spoke.",
                              "The strange fierce face of the Frenchmen who knew for what they fought,"
                              );
        assertEquals(quotes, findQuotes(paths1, "French"));

        List<String> paths2 = Collections.singletonList("src/test/resources/empty.txt");
        List<String> quotes2 = Collections.emptyList();
        assertEquals(quotes2, findQuotes(paths2, "cabbage"));
    }

    @Test
    public void testPiDividedBy4() {
        assertEquals(Math.PI / 4, piDividedBy4(), 0.001);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> compositions1 = new HashMap<>();
        compositions1.put("Dostoevsky", Arrays.asList("The Idiot", "The Gambler"));
        compositions1.put("Pérez-Reverte", Arrays.asList("Cabo Trafalgar", "El Asedio"));
        compositions1.put("Remark", Arrays.asList("Arc de Triomphe", "Der schwarze Obelisk"));
        assertEquals("Remark", findPrinter(compositions1));

        Map<String, List<String>> compositions2 = new HashMap<>();
        compositions2.put("Single author", Collections.singletonList("Single string"));
        assertEquals("Single author", findPrinter(compositions2));;
    }

    @Test
    public void testCalculateGlobalOrder() {
        Map<String, Integer> order1 = new HashMap<>();
        order1.put("Apple", 3);
        order1.put("Large apple", 1);
        order1.put("Small apple", 4);
        Map<String, Integer> order2 = new HashMap<>();
        order2.put("Apple", 1);
        order2.put("Large apple", 0);
        order2.put("Small apple", 3);
        order2.put("Cabbage", 5);
        Map<String, Integer> order3 = new HashMap<>();
        order3.put("Apple", 5);
        order3.put("Cabbage", 13);
        List<Map<String, Integer>> orders = Arrays.asList(order1, order2, order3);
        Map<String, Integer> globalOrders = new HashMap<>();
        globalOrders.put("Apple", 9);
        globalOrders.put("Small apple", 7);
        globalOrders.put("Large apple", 1);
        globalOrders.put("Cabbage", 18);
        assertEquals(globalOrders, calculateGlobalOrder(orders));
    }
}
