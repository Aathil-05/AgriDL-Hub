package com.agriml.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.agriml.model.Cart;
import com.agriml.model.OrderAddress;
import com.agriml.model.OrderRequest;
import com.agriml.model.ProductOrder;
import com.agriml.repository.CartRepository;
import com.agriml.repository.ProductOrderRepository;
import com.agriml.service.OrderService;
import com.agriml.util.CommonUtil;
import com.agriml.util.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private ProductOrderRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CommonUtil commonUtil;

	@Override
	public void saveOrder(Integer userId, OrderRequest orderRequest) throws Exception {

		List<Cart> carts = cartRepository.findByUserId(userId);

		for (Cart cart : carts) {
			ProductOrder order = new ProductOrder();

			order.setOrderId(UUID.randomUUID().toString());
			order.setOrderDate(LocalDate.now());

			order.setProduct(cart.getProduct());
			order.setPrice(cart.getProduct().getDiscountPrice());

			order.setQuantity(cart.getQuantity());
			order.setUser(cart.getUser());

			order.setStatus(OrderStatus.IN_PROGRESS.getName());
			order.setPaymentType(orderRequest.getPaymentType());

			OrderAddress address = new OrderAddress();
			address.setFirstName(orderRequest.getFirstName());
			address.setLastName(orderRequest.getLastName());
			address.setEmail(orderRequest.getEmail());
			address.setPhone(orderRequest.getPhone());
			address.setAddress(orderRequest.getAddress());
			address.setDistrict(orderRequest.getDistrict());
			address.setState(orderRequest.getState());
			address.setPincode(orderRequest.getPincode());

			order.setOrderAddress(address);

			ProductOrder saveOrder = orderRepository.save(order);
			commonUtil.sendMailForProductOrder(saveOrder, "success");
			
		}
	}

	@Override
	public List<ProductOrder> getOrderByUser(Integer userId) {
		
		List<ProductOrder> orders = orderRepository.findByUserId(userId);
		
		return orders;
	}

	@Override
	public ProductOrder updateOrderStatus(Integer id, String status) {
		
		Optional<ProductOrder> findById = orderRepository.findById(id);
		if(findById.isPresent()) {
			ProductOrder productOrder = findById.get();
			productOrder.setStatus(status);
			ProductOrder updateOrder = orderRepository.save(productOrder);
			return updateOrder;
		}	
		return null;
	}

	@Override
	public List<ProductOrder> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public ProductOrder getOrdersByOrderId(String orderId) {
		return orderRepository.findByOrderId(orderId);
	}

	@Override
	public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {		
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return orderRepository.findAll(pageable);
	}
	
}
