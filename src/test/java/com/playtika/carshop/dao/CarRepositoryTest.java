package com.playtika.carshop.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.playtika.carshop.dao.entity.CarEntity;
import org.junit.Test;
import org.springframework.test.annotation.Commit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CarRepositoryTest extends AbstractEntityTest<CarEntityRepository> {
    @Test
    @DataSet(value = "datasets/car.xml")
    public void ifCarWithSuchNumberIsFoundItIsReturned() {
        CarEntity actual = dao.findByNumber("GT23HH");
        assertThat(actual.getColor(), equalTo("red"));
        assertThat(actual.getYear(), equalTo(2015));
        assertThat(actual.getBrand(), equalTo("BMW"));
    }

    @Test
    @DataSet(value = "datasets/car.xml")
    public void ifThereIsNoCarWithSuchNumberNullIsReturned() {
        assertThat(dao.findByNumber("11"), equalTo(null));
    }

    @Test
    @DataSet(value = "datasets/car-empty.xml")
    @ExpectedDataSet("datasets/car.xml")
    @Commit
    public void carCouldBeAdded() {
        dao.save(new CarEntity("GT23HH", "red", 2015, "BMW"));
    }

    @Test
    @DataSet(value = "datasets/car.xml")
    @ExpectedDataSet("datasets/car-empty.xml")
    @Commit
    public void carCouldBeDeleted() {
        dao.delete(dao.findByNumber("GT23HH").getId());
    }
}
