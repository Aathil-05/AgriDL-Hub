package com.agriml.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.agriml.model.Userdetail;
import com.agriml.repository.UserRepository;
import com.agriml.service.UserService;
import com.agriml.util.AppConstant;


@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public Userdetail saveUser(Userdetail user) {
		user.setRole("ROLE_USER");
		user.setIsEnabled(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setLockTime(null);
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		return userRepository.save(user);
	}

	@Override
	public Userdetail getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<Userdetail> getAllUsers(String role) {
		return userRepository.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {

		Optional<Userdetail> findByUser = userRepository.findById(id);

		if (findByUser.isPresent()) {
			Userdetail userdetail = findByUser.get();
			userdetail.setIsEnabled(status);
			userRepository.save(userdetail);
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailedAttempt(Userdetail user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);

	}

	@Override
	public void userAccLock(Userdetail user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepository.save(user);

	}

	@Override
	public Boolean unlockAccTimeExpired(Userdetail user) {

		long lockTime = user.getLockTime().getTime();
		long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

		long currentTime = System.currentTimeMillis();

		if (unLockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true;
		}

		return false;
	}

	@Override
	public void resetAttempt(int userId) {

	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		Userdetail findByEmail = userRepository.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepository.save(findByEmail);

	}

	@Override
	public Userdetail getUserByToken(String token) {
		return userRepository.findByResetToken(token);
	}

	@Override
	public Userdetail updateUser(Userdetail user) {
		return userRepository.save(user);
	}

	@Override
	public Userdetail updateUserProfile(Userdetail user, MultipartFile img) {

		Userdetail duser = userRepository.findById(user.getId()).get();

		if (!img.isEmpty()) {
			duser.setImageName(img.getOriginalFilename());
		}

		if (!ObjectUtils.isEmpty(duser)) {

			duser.setName(user.getName());
			duser.setPhone(user.getPhone());
			duser.setHouseNo(user.getHouseNo());
			duser.setStreet(user.getStreet());
			duser.setPlace(user.getPlace());
			duser.setDistrict(user.getDistrict());
			duser.setState(user.getState());
			duser.setPincode(user.getPincode());
			userRepository.save(duser);
		}
		try {

			if (!img.isEmpty()) {
				File saveFile;

				saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ img.getOriginalFilename());

				Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return duser;
	}

	@Override
	public Userdetail saveAdmin(Userdetail user) {
		
		user.setRole("ROLE_ADMIN");
		user.setIsEnabled(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);
		user.setLockTime(null);
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		return userRepository.save(user);
	}

	@Override
	public Boolean existsEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public Boolean existsPhone(String phone) {
		return userRepository.existsByPhone(phone);
	}

	@Override
	public List<Userdetail> getAllUsers() {
		return userRepository.findAll();
	}

}
