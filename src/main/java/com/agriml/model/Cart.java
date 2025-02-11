package com.agriml.model;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Cart {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	
	@ManyToOne
	private Userdetail user;
	
	@ManyToOne
	private Product product;
	
	private Integer quantity;
	
	@Transient
	private Double discountPrice;
	
	@Transient
	private Double totalPrice;
	
	@Transient
	private Double totalDiscoutPrice;
	
	@Transient
	private Integer totalDiscount;
	
	@Transient
	private Double totalOrderPrice;
	
	
	
	
	
	
	
	
	public Double getTotalOrderPrice() {
		return totalOrderPrice;
	}

	public void setTotalOrderPrice(Double totalOrderPrice) {
		this.totalOrderPrice = totalOrderPrice;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Userdetail getUser() {
		return user;
	}

	public void setUser(Userdetail user) {
		this.user = user;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(Double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public Double getTotalDiscoutPrice() {
		return totalDiscoutPrice;
	}

	public void setTotalDiscoutPrice(Double totalDiscoutPrice) {
		this.totalDiscoutPrice = totalDiscoutPrice;
	}

	public Integer getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Integer totalDiscount) {
		this.totalDiscount = totalDiscount;
	}
	
	

}
