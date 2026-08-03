package com.kubeflux.inventoryservice.controller;

import com.kubeflux.inventoryservice.model.Stock;
import com.kubeflux.inventoryservice.repository.StockRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('STOCK_MANAGER')")
    public Stock addOrUpdateStock(@RequestBody Stock stock) {
        return stockRepository.save(stock);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('STOCK_MANAGER')")
    public Stock updateQuantity(@PathVariable String productId, @RequestBody Stock stock) {
        Stock existing = stockRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        existing.setQuantityAvailable(stock.getQuantityAvailable());
        return stockRepository.save(existing);
    }
}