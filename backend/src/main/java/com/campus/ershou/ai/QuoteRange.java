package com.campus.ershou.ai;

public record QuoteRange(int floor, int ceiling, int rulePrice) {

    public static QuoteRange of(int listPrice, String condition) {
        int percent = switch (condition == null ? "" : condition) {
            case "NEW" -> 90;
            case "LIKE_NEW" -> 80;
            case "GOOD" -> 70;
            case "FAIR" -> 60;
            case "POOR" -> 50;
            default -> 50;
        };
        int ceiling = listPrice;
        int floor = Math.max(1, listPrice * percent / 100);
        if (floor > ceiling) {
            floor = ceiling;
        }
        return new QuoteRange(floor, ceiling, (floor + ceiling) / 2);
    }

    public boolean contains(int price) {
        return price >= floor && price <= ceiling;
    }
}
