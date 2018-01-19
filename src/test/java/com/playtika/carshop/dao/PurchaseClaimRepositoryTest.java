package com.playtika.carshop.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.playtika.carshop.dao.entity.PurchaseClaimEntity;
import org.junit.Test;
import org.springframework.test.annotation.Commit;

import static com.playtika.carshop.dao.entity.status.PurchaseClaimStatus.declined;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class PurchaseClaimRepositoryTest extends AbstractEntityTest<PurchaseClaimEntityRepository> {
    @Test
    @DataSet(value = "datasets/purchase-empty.xml", disableConstraints = true)
    @ExpectedDataSet("datasets/purchase_claim_added.xml")
    @Commit
    public void purchaseClaimCouldBeAdded() {
        dao.save(new PurchaseClaimEntity(null, 23000L, null));
    }

    @Test
    @DataSet(value = "datasets/purchase_claim.xml", disableConstraints = true)
    public void ifPurchaseClaimByIdExistsItIsReturned() {
        assertThat(dao.findOne(1L).getPrice(), equalTo(23000L));
    }

    @Test
    @DataSet(value = "datasets/purchase_claim.xml", disableConstraints = true)
    public void ifNoPurchaseClaimByIdFoundReturnNull() {
        assertThat(dao.findOne(2L), equalTo(null));
    }

    @Test
    @DataSet(value = "datasets/purchase_claim.xml", disableConstraints = true)
    @ExpectedDataSet(value = "datasets/purchase_claim_rejected.xml")
    @Commit
    public void declinedStatusCouldBeSetForPurchaseClaim() {
        PurchaseClaimEntity claim = dao.findOne(1L);
        claim.setStatus(declined);
        dao.save(claim);
    }

    @Test
    @DataSet(value = "datasets/purchase_claims.xml", disableConstraints = true)
    public void purchaseClaimWithMaxPriceShouldBeReturned() throws Exception {
        assertThat(dao.findMaxPriceBySaleClaimId(1L), equalTo(23000L));
    }
}
