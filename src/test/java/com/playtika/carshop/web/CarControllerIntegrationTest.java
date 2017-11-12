package com.playtika.carshop.web;

import com.google.common.io.Resources;
import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import com.playtika.carshop.service.CarDealService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CarController.class)
public class CarControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CarDealService service;

    private Map<Long, CarDeal> deals = new ConcurrentHashMap<>();

    @Test
    public void carDealCouldBeAdded() throws Exception {
        when(service.addCarDeal(new Car("red", "GT23"), "tomasMann@gmail.com", 19284)).thenReturn(1L);
        String id = mockMvc.perform(post("/cars")
                .content("{\"color\":\"red\", \"model\":\"GT23\"}")
                .param("price", "19284")
                .param("sellerContacts", "tomasMann@gmail.com")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        assertThat(Long.parseLong(id), is(equalTo((1L))));
    }

    @Test
    public void allCarDealsAreReturned() throws Exception {
        deals.put(1L, new CarDeal(1L, new SaleInfo("tomasMann@gmail.com", 19284), new Car("red", "GT23")));
        deals.put(2L, new CarDeal(2L, new SaleInfo("henrichFitch@gmail.com", 27362), new Car("green", "HN23")));
        when(service.getAllCars()).thenReturn(deals.values());
        String response = Resources.toString(getResource("messages/getAllCarsResponse.json"), UTF_8);
        mockMvc.perform(get("/cars")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().json(response));
    }

    @Test
    public void saleInfoIsReturnedById() throws Exception {
        when(service.getSaleInfoById(1L)).thenReturn(new SaleInfo("tomasMann@gmail.com", 19284));
        mockMvc.perform(get("/cars/1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().json("{\"sellerContacts\": \"tomasMann@gmail.com\",\"price\": 19284}"));
    }
}
