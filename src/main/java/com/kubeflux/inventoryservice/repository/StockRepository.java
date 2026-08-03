package com.kubeflux.inventoryservice.repository;

import com.kubeflux.inventoryservice.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
}