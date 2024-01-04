package com.demo.orderservice.service;

import com.demo.orderservice.dto.InventoryResponse;
import com.demo.orderservice.dto.OrderLineItemsDto;
import com.demo.orderservice.dto.OrderRequest;
import com.demo.orderservice.model.Order;
import com.demo.orderservice.model.OrderLineItems;
import com.demo.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        //To check if the product is available in the inventory
        //by calling inventory-service
        InventoryResponse[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8082/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean allProductsInStock = false;
        if (inventoryResponseArray != null) {
            allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::getIsInStock);
        }
        if(allProductsInStock){
            orderRepository.save(order);
        }else {
            throw new IllegalArgumentException("Product is out of stock, Try after some time");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderlineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderlineItemsDto.getPrice());
        orderLineItems.setQuantity(orderlineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderlineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
