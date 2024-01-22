package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BundleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BundleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Bundle.class);
        Bundle bundle1 = getBundleSample1();
        Bundle bundle2 = new Bundle();
        assertThat(bundle1).isNotEqualTo(bundle2);

        bundle2.setId(bundle1.getId());
        assertThat(bundle1).isEqualTo(bundle2);

        bundle2 = getBundleSample2();
        assertThat(bundle1).isNotEqualTo(bundle2);
    }
}
