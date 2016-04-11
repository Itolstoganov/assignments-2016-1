package ru.spbau.mit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths.stream()
                .flatMap(p -> {
                    Stream<String> s = null;
                    try {
                        s = Files.lines(Paths.get(p));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return s;
                })
                .filter(p -> p.contains(sequence))
                .collect(Collectors.toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать,
    // какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        final int sampleSize = 1000000;
        final double centerX = 0.5;
        final double centerY = 0.5;
        final double radius = 0.5;
        Random r = new Random(System.currentTimeMillis());
        return Stream.generate(() -> new double[]{r.nextDouble(), r.nextDouble()}).limit(sampleSize)
                .filter(p ->
                        Math.pow((p[0] - centerX), 2) + Math.pow((p[1] - centerY), 2)
                                < Math.pow(radius, 2))
                .count() / (double) sampleSize;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        return compositions.entrySet().stream()
                .max(Comparator.comparing(p ->
                        p.getValue().stream()
                                .mapToInt(w -> w.length())
                                .sum()))
                .get().getKey();
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        Map<String, List<Integer>> groupedOrders =  orders.stream()
                .flatMap(p -> p.entrySet().stream())
                .collect(
                        Collectors.groupingBy(p -> p.getKey(),
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList()
                                )
                        )
                );
        return groupedOrders.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, l -> l.getValue().stream()
                                                .mapToInt(el -> el)
                                                .sum()));
    }
}
