package com.playtika.carshop.web;

import com.google.common.io.Resources;
import com.playtika.carshop.domain.Car;
import com.playtika.carshop.domain.CarDeal;
import com.playtika.carshop.domain.SaleInfo;
import com.playtika.carshop.service.CarDealService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CarControllerTest {

    @Mock
    private CarDealService service;

    private CarController controller;
    private MockMvc mockMvc;
    private Map<Long, CarDeal> deals = new ConcurrentHashMap<>();

    @Before
    public void setUp() throws Exception {
        controller = new CarController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

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
        Assert.assertThat(Long.parseLong(id), is(equalTo((1L))));
    }

    @Test
    public void ifCarDealWasAlreadyAddedReturnNoContent() throws Exception {
        when(service.addCarDeal(new Car("red", "GT23"), "tomasMann@gmail.com", 19284))
                .thenThrow(IllegalArgumentException.class);
        mockMvc.perform(post("/cars")
                .content("{\"color\":\"red\", \"model\":\"GT23\"}")
                .param("price", "19284")
                .param("sellerContacts", "tomasMann@gmail.com")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void ifPriceIsMissedThenThrowException() throws Exception {
        mockMvc.perform(post("/cars")
                .content("{\"color\":\"red\", \"model\":\"GT23\"}")
                .param("sellerContacts", "tomasMann@gmail.com")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ifSellerContactIsMissedThenThrowException() throws Exception {
        mockMvc.perform(post("/cars")
                .content("{\"color\":\"red\", \"model\":\"GT23\"}")
                .param("price", "19284")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ifModelIsMissedThenThrowException() throws Exception {
        mockMvc.perform(post("/cars")
                .content("{\"color\":\"red\"}")
                .param("price", "19284")
                .param("sellerContacts", "tomasMann@gmail.com")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ifColorIsMissedThenThrowException() throws Exception {
        mockMvc.perform(post("/cars")
                .content("{\"model\":\"GT23\"}")
                .param("price", "19284")
                .param("sellerContacts", "tomasMann@gmail.com")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void allCarDealsAreReturned() throws Exception {
        deals.put(1L, new CarDeal(1L, new SaleInfo("tomasMann@gmail.com", 19284), new Car("red", "GT23")));
        deals.put(2L, new CarDeal(2L, new SaleInfo("henrichFitch@gmail.com", 27362), new Car("green", "HN23")));
        when(service.getAllCarDeals()).thenReturn(deals.values());
        String response = Resources.toString(getResource("messages/getAllCarsResponse.json"), UTF_8);
        mockMvc.perform(get("/cars")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().json(response));
    }

    @Test
    public void IfThereAreNoCarDealsThenReturnEmptyCollection() throws Exception {
        when(service.getAllCarDeals()).thenReturn(Collections.emptyList());
        assertThat(controller.getAllCars(), is(equalTo(Collections.emptyList())));
    }

    @Test
    public void saleInfoIsReturnedById() throws Exception {
        SaleInfo saleInfo = new SaleInfo("tomasMann@gmail.com", 19284);
        when(service.getSaleInfoById(1L)).thenReturn(of(saleInfo));
        mockMvc.perform(get("/cars/1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().json("{\"sellerContacts\": \"tomasMann@gmail.com\",\"price\": 19284}"));
    }

    @Test
    public void ifNoCarDealFoundThenThrowException() throws Exception {
        when(service.getSaleInfoById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/cars/1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ifIdIsNullThenThrowException() throws Exception {
        mockMvc.perform(get("/cars/<null>")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ifCarDealIsAbsentThenReturnNoContentStatus() throws Exception {
        mockMvc.perform(delete("/cars/1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNoContent());
    }
}