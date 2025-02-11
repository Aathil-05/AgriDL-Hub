package com.agriml.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agriml.model.Userdetail;

public interface UserRepository extends JpaRepository<Userdetail, Integer> {
	
	public Userdetail findByEmail(String email);

	public List<Userdetail> findByRole(String role);
	
	public Userdetail findByResetToken(String token);
	
	public Boolean existsByEmail(String email);
	
	public Userdetail findByPhone(String phone);
	
	public Boolean existsByPhone(String phone);
	
}
