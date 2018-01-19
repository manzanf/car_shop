package com.playtika.carshop.web;

import com.google.common.io.Resources;
import com.playtika.carshop.domain.*;
import com.playtika.carshop.service.CarDealService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.rmi.NoSuchObjectException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
        assertThat(Long.parseLong(id), is(equalTo((1L))));
    }

    @Test
    public void ifCarDealWasAlreadyAddedReturnNoContentStatus() throws Exception {
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
    public void ifPriceIsMissedReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/cars")
                .content("{\"color\":\"red\", \"model\":\"GT23\"}")
                .param("sellerContacts", "tomasMann@gmail.com")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ifSellerContactIsMissedReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/cars")
                .content("{\"color\":\"red\", \"model\":\"GT23\"}")
                .param("price", "19284")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ifModelIsMissedReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/cars")
                .content("{\"color\":\"red\"}")
                .param("price", "19284")
                .param("sellerContacts", "tomasMann@gmail.com")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ifColorIsMissedReturnBadRequestStatus() throws Exception {
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
    public void IfThereAreNoCarDealsReturnEmptyCollection() throws Exception {
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
    public void ifNoCarDealFoundReturnNotFoundStatus() throws Exception {
        when(service.getSaleInfoById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/cars/1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ifCarDealIdIsNullReturnBadRequestStatus() throws Exception {
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

    @Test
    public void purchaseClaimShouldBeAdded() throws Exception {
        when(service.addPurchase(2L, 23000)).thenReturn(1L);
        String id = mockMvc.perform(post("/purchase")
                .param("carDealId", "2")
                .param("price", "23000")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(("application/json;charset=UTF-8")))
                .andReturn().getResponse().getContentAsString();
        assertThat(id, is(equalTo("1")));
    }

    @Test
    public void ifCarDealIdIsNotPassedInRequestReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/purchase")
                .param("price", "23000")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ifPriceIsNotPassedInRequestReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/purchase")
                .param("carDealId", "2")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ifPurchaseClaimWasNotAddedReturnNoContentStatus() throws Exception {
        when(service.addPurchase(2L, 23000)).thenThrow(NoSuchObjectException.class);
        mockMvc.perform(post("/purchase")
                .param("carDealId", "2")
                .param("price", "23000")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void purchaseClaimCouldBeRejected() throws Exception {
        when(service.rejectPurchaseClaim(1L)).thenReturn(true);
        mockMvc.perform(post("/purchase/reject")
                .param("purchaseClaimId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    public void ifPurchaseClaimWasNotRejectedReturnNoContentStatus() throws Exception {
        when(service.rejectPurchaseClaim(1L)).thenReturn(false);
        mockMvc.perform(post("/purchase/reject")
                .param("purchaseClaimId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void ifPurchaseClaimIdWasNotPassedReturnBadRequestStatus() throws Exception {
        mockMvc.perform(post("/purchase/reject")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void bestBidIsReturnedWithAcceptedState() throws Exception {
        when(service.acceptBestPurchaseClaim(1L)).thenReturn(new PriceWithState(10L, AcceptBidState.ACCEPTED));
        String actual = mockMvc.perform(get("/cars/bestBid")
                .param("carDealId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(actual, is(equalTo(getMessage("10", "ACCEPTED"))));
    }

    @Test
    public void ifCarDealWasNotFoundReturnNoCarDealState() throws Exception {
        when(service.acceptBestPurchaseClaim(1L)).thenReturn(new PriceWithState(AcceptBidState.NO_CAR_DEAL));
        String actual = mockMvc.perform(get("/cars/bestBid")
                .param("carDealId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(actual, is(equalTo(getMessage("null", "NO_CAR_DEAL"))));
    }

    @Test
    public void ifPurchaseClaimsForTheCarDealWasNotFoundReturnNoPurchaseClaimsState() throws Exception {
        when(service.acceptBestPurchaseClaim(1L)).thenReturn(new PriceWithState(AcceptBidState.NO_PURCHASE_CLAIMS));
        String actual = mockMvc.perform(get("/cars/bestBid")
                .param("carDealId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(actual, is(equalTo(getMessage("null", "NO_PURCHASE_CLAIMS"))));
    }

    @Test
    public void ifCarDealWasAlreadyClosedReturnAlreadyClosedCarDealState() throws Exception {
        when(service.acceptBestPurchaseClaim(1L)).thenReturn(new PriceWithState(AcceptBidState.ALREADY_CLOSED_CAR_DEAL));
        String actual = mockMvc.perform(get("/cars/bestBid")
                .param("carDealId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(actual, is(equalTo(getMessage("null", "ALREADY_CLOSED_CAR_DEAL"))));
    }

    @Test
    public void ifCarDealIdWasNotPassedReturnBadRequestStatus() throws Exception {
        mockMvc.perform(get("/cars/bestBid")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    private String getMessage(String price, String message) {
        return "{\"price\":" + price + ",\"state\":\"" + message +"\"}";
    }
}