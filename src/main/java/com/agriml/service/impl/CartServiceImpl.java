package com.agriml.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.agriml.model.Cart;
import com.agriml.model.Product;
import com.agriml.model.Userdetail;
import com.agriml.repository.CartRepository;
import com.agriml.repository.ProductRepository;
import com.agriml.repository.UserRepository;
import com.agriml.service.CartService;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;

	@Override
	public Cart saveCart(Integer productId, Integer userId) {
		
		Userdetail userdetail = userRepository.findById(userId).get();
		Product product = productRepository.findById(productId).get();
		
		Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);
		
		Cart cart=null;
		
		if(ObjectUtils.isEmpty(cartStatus)) {
			cart = new Cart();
			cart.setProduct(product);
			cart.setUser(userdetail);
			cart.setQuantity(1);
			cart.setTotalPrice(1 * product.getDiscountPrice());
		} else {
			cart=cartStatus;
			cart.setQuantity(cart.getQuantity()+1);
			cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());
		}
		
		Cart saveCart = cartRepository.save(cart);
		
		return saveCart;
	}

	@Override
	public List<Cart> getCartByUser(Integer userId) {
		 List<Cart> carts = cartRepository.findByUserId(userId);
		 
		 Double totalOrderPrice = 0.0;
		 List<Cart> updateCart = new ArrayList<Cart>();
		 
		 for (Cart c : carts) {
			 Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());
			 c.setTotalPrice(totalPrice);
			 totalOrderPrice += totalPrice;
			 c.setTotalOrderPrice(totalOrderPrice);
			 updateCart.add(c);
		 }
		 return updateCart;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		return cartRepository.countByUserId(userId);
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {
		
		Cart cart = cartRepository.findById(cid).get();
		int updateQuantity;
		
		if(sy.equalsIgnoreCase("de")) {
			updateQuantity = cart.getQuantity()-1;
			
			if(updateQuantity<=0) {
				cartRepository.deleteById(cid);
			} else {
				cart.setQuantity(updateQuantity);
				cartRepository.save(cart);
			}
		} else {
			updateQuantity = cart.getQuantity()+1;
			cart.setQuantity(updateQuantity);
			cartRepository.save(cart);
		}
		
		
		
	}

}
