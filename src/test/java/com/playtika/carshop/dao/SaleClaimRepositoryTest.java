package com.playtika.carshop.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.playtika.carshop.dao.entity.SaleClaimEntity;
import org.junit.Test;
import org.springframework.test.annotation.Commit;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class SaleClaimRepositoryTest extends AbstractEntityTest<SaleClaimEntityRepository> {
    @Test
    @DataSet(value = "datasets/claim-empty.xml", disableConstraints = true)
    @ExpectedDataSet("datasets/sale_claim_added.xml")
    @Commit
    public void saleClaimCouldBeAdded() {
        dao.save(new SaleClaimEntity(null, 50L, null));
    }

    @Test
    @DataSet(value = "datasets/sale_claim.xml", disableConstraints = true)
    public void allExistedSaleClaimsAreReturned() {
        assertThat(dao.findAll(), hasSize(2));
    }

    @Test
    @DataSet(value = "datasets/claim-empty.xml", disableConstraints = true)
    public void ifNoSaleClaimsReturnEmptyList() {
        assertThat(dao.findAll(), is(empty()));
    }

    @Test
    @DataSet(value = "datasets/sale_claim.xml", disableConstraints = true)
    public void ifSaleClaimByIdExistsItIsReturned() {
        assertThat(dao.findOne(2L).getPrice(), equalTo(31000L));
    }

    @Test
    @DataSet(value = "datasets/sale_claim.xml", disableConstraints = true)
    public void ifNoSaleClaimByIdReturnNull() {
        assertThat(dao.findOne(5L), equalTo(null));
    }
}