package com.kubeflux.inventoryservice.kafka;

import com.kubeflux.inventoryservice.event.OrderCreatedEvent;
import com.kubeflux.inventoryservice.model.ProcessedEvent;
import com.kubeflux.inventoryservice.model.Stock;
import com.kubeflux.inventoryservice.repository.ProcessedEventRepository;
import com.kubeflux.inventoryservice.repository.StockRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private final StockRepository stockRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final DeadLetterProducer deadLetterProducer;

    public OrderEventConsumer(StockRepository stockRepository,
                              ProcessedEventRepository processedEventRepository,
                              DeadLetterProducer deadLetterProducer) {
        this.stockRepository = stockRepository;
        this.processedEventRepository = processedEventRepository;
        this.deadLetterProducer = deadLetterProducer;
    }

    @KafkaListener(topics = "orders", groupId = "inventory-service-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            // ---------- 1. Vérification d'idempotence ----------
            if (processedEventRepository.existsById(event.getEventId())) {
                System.out.println("Événement déjà traité, ignoré: " + event.getEventId());
                return;
            }

            // ---------- 2. Traitement métier ----------
            Stock stock = stockRepository.findById(event.getProductId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Produit inconnu: " + event.getProductId()));

            if (stock.getQuantityAvailable() < event.getQuantity()) {
                throw new IllegalStateException(
                        "Stock insuffisant pour " + event.getProductId());
            }

            stock.setQuantityAvailable(stock.getQuantityAvailable() - event.getQuantity());
            stockRepository.save(stock);

            // ---------- 3. Marquer l'événement comme traité ----------
            processedEventRepository.save(new ProcessedEvent(event.getEventId()));

            System.out.println("Stock mis à jour pour " + event.getProductId()
                    + ", nouvelle quantité: " + stock.getQuantityAvailable());

        } catch (Exception e) {
            // ---------- 4. En cas d'échec, envoyer en DLQ plutôt que bloquer ----------
            deadLetterProducer.sendToDlq(event, e.getMessage());
        }
    }
}