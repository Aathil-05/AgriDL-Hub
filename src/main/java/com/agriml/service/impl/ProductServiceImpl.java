
package com.agriml.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.agriml.model.Product;
import com.agriml.repository.ProductRepository;
import com.agriml.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {


	@Autowired
	private ProductRepository productRepository;

	@Override
	public Product saveProduct(Product product) {
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Boolean deleteProduct(int id) {
		Product product = productRepository.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(product)) {
			productRepository.delete(product);
			return true;
		}

		return false;
	}

	@Override
	public Product getProductById(int id) {
		Product product = productRepository.findById(id).orElse(null);
		return product;
	}

	@Override
	public Product updateProduct(Product product, MultipartFile image) {

		Product oldProduct = getProductById(product.getId());

		String imageName = image.isEmpty() ? oldProduct.getImage() : image.getOriginalFilename();

		oldProduct.setTitle(product.getTitle());
		oldProduct.setDescription(product.getDescription());
		oldProduct.setCategory(product.getCategory());
		oldProduct.setPrice(product.getPrice());
		oldProduct.setStacks(product.getStacks());
		oldProduct.setImage(imageName);
		oldProduct.setIsActive(product.getIsActive());
		oldProduct.setDiscount(product.getDiscount());

		// d = 100*(5/100) => 100-5 = 95
		Double discount = product.getPrice() * (product.getDiscount() / 100.0);
		Double discountPrice = product.getPrice() - discount;

		oldProduct.setDiscountPrice(discountPrice);

		Product updateProduct = productRepository.save(oldProduct);

		if (!ObjectUtils.isEmpty(updateProduct)) {

			if (!image.isEmpty()) {

				try {
					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
							+ image.getOriginalFilename());
					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			return product;
		}

		return null;
	}

	@Override
	public List<Product> getAllActiveProducts(String category) {

		List<Product> products = null;
		if (ObjectUtils.isEmpty(category)) {
			products = productRepository.findByIsActiveTrue();
		} else {
			products = productRepository.findByCategory(category);
		}

		return products;
	}

	@Override
	public List<Product> searchProduct(String ch) {

		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
	}
	
	

	@Override
	public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {
		
		Pageable pageable = PageRequest.of(pageNo,pageSize);
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);
	}

	@Override
	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {

		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> pageProduct = null;
		
		if (ObjectUtils.isEmpty(category)) {
			pageProduct = productRepository.findByIsActiveTrue(pageable);
		} else {
			pageProduct = productRepository.findByCategory(pageable,category);
		}
		return pageProduct;
	}

	@Override
	public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findAll(pageable);
	}

	@Override
	public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch) {
		
		Page<Product> pageProduct = null;
		Pageable pageable = PageRequest.of(pageNo,pageSize);
		pageProduct = productRepository.findByIsActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);
		
//		if (ObjectUtils.isEmpty(category)) {
//			pageProduct = productRepository.findByIsActiveTrue(pageable);
//		} else {
//			pageProduct = productRepository.findByCategory(pageable,category);
//		}
		return pageProduct;
		
	}

	@Override
	public List<Product> getAllProductsByUserId(Integer userId) {
		
		return productRepository.findByUserId(userId);
	}

	@Override
	public Page<Product> getAllProductsByUserIdPagination(Integer userId, Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findByUserId(pageable, userId);
	}
	
}
