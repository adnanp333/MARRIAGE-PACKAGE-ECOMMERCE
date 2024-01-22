package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BundleItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static BundleItem getBundleItemSample1() {
        return new BundleItem().id(1L).bundleId(1).itemId(1);
    }

    public static BundleItem getBundleItemSample2() {
        return new BundleItem().id(2L).bundleId(2).itemId(2);
    }

    public static BundleItem getBundleItemRandomSampleGenerator() {
        return new BundleItem().id(longCount.incrementAndGet()).bundleId(intCount.incrementAndGet()).itemId(intCount.incrementAndGet());
    }
}
