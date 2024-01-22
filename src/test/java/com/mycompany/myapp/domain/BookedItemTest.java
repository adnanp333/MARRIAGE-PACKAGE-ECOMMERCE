package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BookedItemTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookedItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BookedItem.class);
        BookedItem bookedItem1 = getBookedItemSample1();
        BookedItem bookedItem2 = new BookedItem();
        assertThat(bookedItem1).isNotEqualTo(bookedItem2);

        bookedItem2.setId(bookedItem1.getId());
        assertThat(bookedItem1).isEqualTo(bookedItem2);

        bookedItem2 = getBookedItemSample2();
        assertThat(bookedItem1).isNotEqualTo(bookedItem2);
    }
}
