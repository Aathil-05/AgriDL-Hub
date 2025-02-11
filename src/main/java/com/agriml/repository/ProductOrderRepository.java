package com.agriml.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agriml.model.ProductOrder;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

	List<ProductOrder> findByUserId(Integer userId);

	ProductOrder findByOrderId(String orderId);

}
