package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BookedItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static BookedItem getBookedItemSample1() {
        return new BookedItem().id(1L).bookingId(1).itemId(1).customItemName("customItemName1");
    }

    public static BookedItem getBookedItemSample2() {
        return new BookedItem().id(2L).bookingId(2).itemId(2).customItemName("customItemName2");
    }

    public static BookedItem getBookedItemRandomSampleGenerator() {
        return new BookedItem()
            .id(longCount.incrementAndGet())
            .bookingId(intCount.incrementAndGet())
            .itemId(intCount.incrementAndGet())
            .customItemName(UUID.randomUUID().toString());
    }
}
