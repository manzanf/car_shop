package com.playtika.carshop.web;

import com.playtika.carshop.service.CarDealService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CarControllerTest {

    @Mock
    private CarDealService service;

    private CarController controller;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        controller = new CarController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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
    public void IfThereAreNoCarDealsThenReturnEmptyCollection() throws Exception {
        when(service.getAllCars()).thenReturn(Collections.emptyList());
        assertThat(controller.getAllCars(), is(equalTo(Collections.emptyList())));
    }

    @Test
    public void ifNoCarDealFoundThenThrowException() throws Exception {
        when(service.getSaleInfoById(1L)).thenThrow(new CarNotFoundException());
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
    public void ifCarDealIsAbsentThenReturnOkStatus() throws Exception {
        mockMvc.perform(delete("/cars/1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());
    }
}