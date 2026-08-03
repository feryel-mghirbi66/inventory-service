package com.kubeflux.inventoryservice;

import com.kubeflux.inventoryservice.controller.StockController;
import com.kubeflux.inventoryservice.model.Stock;
import com.kubeflux.inventoryservice.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockControllerTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockController stockController;

    @Test
    void getAllStock_returnsAllProducts() {
        Stock s1 = new Stock();
        s1.setProductId("laptop-1");
        s1.setQuantityAvailable(10);

        when(stockRepository.findAll()).thenReturn(List.of(s1));

        List<Stock> result = stockController.getAllStock();

        assertEquals(1, result.size());
        assertEquals("laptop-1", result.get(0).getProductId());
    }

    @Test
    void addOrUpdateStock_savesProduct() {
        Stock newStock = new Stock();
        newStock.setProductId("lenovo3");
        newStock.setQuantityAvailable(20);

        when(stockRepository.save(any(Stock.class))).thenReturn(newStock);

        Stock result = stockController.addOrUpdateStock(newStock);

        assertEquals("lenovo3", result.getProductId());
        verify(stockRepository, times(1)).save(newStock);
    }
}