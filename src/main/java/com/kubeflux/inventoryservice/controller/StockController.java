package com.kubeflux.inventoryservice.controller;

import com.kubeflux.inventoryservice.model.Stock;
import com.kubeflux.inventoryservice.repository.StockRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockRepository stockRepository;

    public StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @GetMapping
    public List<Stock> getAllStock() {
        return stockRepository.findAll();
    }

    @PostMapping
    public Stock addOrUpdateStock(@RequestBody Stock stock) {
        return stockRepository.save(stock);
    }

    @DeleteMapping("/{productId}")
    public void deleteStock(@PathVariable String productId) {
        stockRepository.deleteById(productId);
    }
}
