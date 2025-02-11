package com.agriml.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.agriml.model.Userdetail;

public interface UserService {
	
	public Userdetail saveUser(Userdetail user);
	
	public Userdetail getUserByEmail(String email);
	
	public List<Userdetail> getAllUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);
	
	public void increaseFailedAttempt(Userdetail user);
	
	public void userAccLock(Userdetail user);
	
	public Boolean unlockAccTimeExpired(Userdetail user);
	
	public void resetAttempt(int userId);

	public void updateUserResetToken(String email, String resetToken);
	
	public Userdetail getUserByToken(String token);
	
	public Userdetail updateUser(Userdetail user);

	public Userdetail updateUserProfile(Userdetail user, MultipartFile img);
	
	public Userdetail saveAdmin(Userdetail user);

	public Boolean existsEmail(String email);
	
	public Boolean existsPhone(String phone);

	public List<Userdetail> getAllUsers();
	
}
