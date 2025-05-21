package com.bytedesk.ticket.ticket;

public enum TicketRatingEnum {
    GOOD(5), // 非常满意
    SATISFIED(4), // 满意
    NORMAL(3), // 一般
    UNSATISFIED(2), // 不满意
    BAD(1); // 非常不满意

    private Integer value;

    TicketRatingEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
