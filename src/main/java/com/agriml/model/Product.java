package com.agriml.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private Userdetail user;
	
	@Column(length = 500)
	private String title;
	
	@Column(length = 1000)
	private String description;
	
	private String category;
	
	private double price;
	
	private int stacks;
	
	private String image;
	
	private int discount;
	
	private Double discountPrice;
	
	private Boolean isActive;
	

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public Double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(Double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getStacks() {
		return stacks;
	}

	public void setStacks(int stacks) {
		this.stacks = stacks;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Userdetail getUser() {
		return user;
	}

	public void setUser(Userdetail user) {
		this.user = user;
	}

}
