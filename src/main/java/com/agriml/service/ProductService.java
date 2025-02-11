package com.agriml.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.agriml.model.Product;

public interface ProductService {
	
	public Product saveProduct(Product product);
	
	public List<Product> getAllProducts();
	
	public Boolean deleteProduct(int id);
	
	public Product getProductById(int id);
	
	public Product updateProduct(Product product, MultipartFile image);
	
	public List<Product> getAllActiveProducts(String category);
	
	public List<Product> searchProduct(String ch);
	
	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category);
	
	public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch);
	
	public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize);

	public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch);
	
	public List<Product> getAllProductsByUserId(Integer userId);
	
	public Page<Product> getAllProductsByUserIdPagination(Integer userId, Integer pageNo, Integer pageSize);

	
}
