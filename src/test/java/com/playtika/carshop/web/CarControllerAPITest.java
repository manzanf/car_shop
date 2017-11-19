package com.playtika.carshop.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CarControllerAPITest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void carDealCouldBeAdded() throws Exception {
        addCarDeal("tom@gmail.com")
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }

    @Test
    public void allAddedCarDealsAreReturnedToClient() throws Exception {
        addCarDeal("tom@gmail.com")
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        addCarDeal("mike@gmail.com")
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
        mockMvc.perform(get("/cars")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("tom@gmail.com"),
                        containsString("mike@gmail.com"))));
    }

    @Test
    public void carDealCouldBeDeleted() throws Exception {
        String response = addCarDeal("tom@gmail.com")
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        long id = Long.parseLong(response);
        mockMvc.perform(delete("/cars/{id}", id)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    public void saleInfoCanBeRetrieved() throws Exception {
        String response = addCarDeal("tom@gmail.com")
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        long id = Long.parseLong(response);
        mockMvc.perform(get("/cars/{id}", id)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));
    }

    private ResultActions addCarDeal(String sellerContacts) throws Exception {
        return mockMvc.perform(post("/cars")
                .content("{\"color\":\"red\", \"model\":\"GT23\"}")
                .param("price", "10")
                .param("sellerContacts", sellerContacts)
                .contentType("application/json;charset=UTF-8"));
    }
}
