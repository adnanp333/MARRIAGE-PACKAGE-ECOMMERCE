package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BundleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Bundle getBundleSample1() {
        return new Bundle().id(1L).bundleName("bundleName1").priceRange("priceRange1").maxPeople("maxPeople1");
    }

    public static Bundle getBundleSample2() {
        return new Bundle().id(2L).bundleName("bundleName2").priceRange("priceRange2").maxPeople("maxPeople2");
    }

    public static Bundle getBundleRandomSampleGenerator() {
        return new Bundle()
            .id(longCount.incrementAndGet())
            .bundleName(UUID.randomUUID().toString())
            .priceRange(UUID.randomUUID().toString())
            .maxPeople(UUID.randomUUID().toString());
    }
}
