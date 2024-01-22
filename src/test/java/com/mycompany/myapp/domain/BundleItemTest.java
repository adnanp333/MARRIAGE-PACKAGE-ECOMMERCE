package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BundleItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BundleItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BundleItem.class);
        BundleItem bundleItem1 = getBundleItemSample1();
        BundleItem bundleItem2 = new BundleItem();
        assertThat(bundleItem1).isNotEqualTo(bundleItem2);

        bundleItem2.setId(bundleItem1.getId());
        assertThat(bundleItem1).isEqualTo(bundleItem2);

        bundleItem2 = getBundleItemSample2();
        assertThat(bundleItem1).isNotEqualTo(bundleItem2);
    }
}
