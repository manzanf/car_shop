package com.playtika.carshop.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.playtika.carshop.dao.entity.SellerEntity;
import org.junit.Test;
import org.springframework.test.annotation.Commit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class SellerRepositoryTest extends AbstractEntityTest<SellerEntityRepository> {
    @Test
    @DataSet(value = "datasets/seller.xml", disableConstraints = true)
    public void ifSellerWithSuchEmailIsFoundItIsReturned() {
        SellerEntity actual = dao.findByEmail("tom@gmail.com");
        assertThat(actual.getEmail(), equalTo("tom@gmail.com"));
    }

    @Test
    @DataSet(value = "datasets/seller.xml", disableConstraints = true)
    public void ifNoSellerWasFoundByEmailNullIsReturned() {
        assertThat(dao.findByEmail("1"), equalTo(null));
    }

    @Test
    @DataSet(value = "seller-empty.xml", disableConstraints = true)
    @ExpectedDataSet("datasets/seller.xml")
    @Commit
    public void sellerCouldBeAdded() {
        dao.save(new SellerEntity("tom@gmail.com"));
    }
}
