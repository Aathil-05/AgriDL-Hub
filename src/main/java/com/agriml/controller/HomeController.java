package com.agriml.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.agriml.model.Category;
import com.agriml.model.Product;
import com.agriml.model.Userdetail;
import com.agriml.service.CartService;
import com.agriml.service.CategoryService;
import com.agriml.service.ProductService;
import com.agriml.service.UserService;
import com.agriml.util.CommonUtil;

import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	private final String pythonApiUrl = "http://localhost:8000/predict/";

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private CartService cartService;

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			Userdetail userDetail = userService.getUserByEmail(email);
			m.addAttribute("user", userDetail);
			Integer countCart = cartService.getCountCart(userDetail.getId());
			m.addAttribute("countCart", countCart);
		}
	}

	@GetMapping("/")
	public String base(Model m) {

		List<Category> categories = categoryService.getAllActiveCategory().stream()
				.sorted((c1, c2) -> c2.getId().compareTo(c1.getId())).limit(6).toList();
		List<Product> products = productService.getAllActiveProducts("").stream()
				.sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).limit(4).toList();

		m.addAttribute("categories", categories);
		m.addAttribute("products", products);

		return "index";
	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}

	@GetMapping("/service")
	public String service(Model model) {

		model.addAttribute("errMsg", "Upload Image");

		return "service";
	}

	@PostMapping("/home/data-processing")
	public String serviceprocessing(Model model, @RequestParam("file") MultipartFile file) throws Exception {

		if (!file.isEmpty()) {
			File saveFile = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "crop_img" + File.separator
					+ file.getOriginalFilename());
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			try {
				RestTemplate restTemplate = new RestTemplate();

		        // Prepare the request body as multipart
		        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		        body.add("file", file.getResource());  // Adding the file to the request body

		        // Set up the HTTP headers
		        HttpHeaders headers = new HttpHeaders();
		        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		        // Create the request entity with the body and headers
		        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		        // Make the POST request to the Python API
		        ResponseEntity<Map> response = restTemplate.exchange(
		            pythonApiUrl, HttpMethod.POST, requestEntity, Map.class);

		        // Get the prediction and confidence values from the response
		        String prediction = (String) response.getBody().get("class");
		        double confidence = Double.parseDouble(response.getBody().get("confidence").toString());

		        // Output the prediction and confidence
		        System.out.println("Prediction: " + prediction);
		        System.out.println("Confidence: " + confidence);

		        // Add the prediction and confidence to the model for rendering
		        model.addAttribute("prediction", prediction);
		        model.addAttribute("confidence", confidence);

		        // Optionally, add a success message to the model
		        model.addAttribute("succMsg", "Image processed successfully");

			} catch (Exception e) {
				model.addAttribute("errMsg", "Error processing image: " + e.getMessage());
			}
		} else {
			model.addAttribute("errMsg", "Something wrong on server");
		}

		return "service"; // Return the view name
	}

	@GetMapping("/products")
	public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "12") Integer pageSize,
			@RequestParam(defaultValue = "") String ch) {

		List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("paramValue", category);
		m.addAttribute("categories", categories);

		// List<Product> products = productService.getAllActiveProducts(category);
		// m.addAttribute("products", products);
		Page<Product> page = null;
		if (StringUtils.isEmpty(ch)) {
			page = productService.getAllActiveProductPagination(pageNo, pageSize, category);
		} else {
			page = productService.searchActiveProductPagination(pageNo, pageSize, category, ch);
		}

		List<Product> products = page.getContent();
		m.addAttribute("products", products);
		m.addAttribute("productsSize", products.size());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "product";
	}

	@GetMapping("/viewDetails/{id}")
	public String viewDetails(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		return "viewDetails";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute Userdetail user, @RequestParam("image") MultipartFile file,
			HttpSession session) throws IOException {

		Boolean existsEmail = userService.existsEmail(user.getEmail());

		if (existsEmail) {
			session.setAttribute("errMsg", "Email already exists");
		} else {

			String imageName = !file.isEmpty() ? file.getOriginalFilename() : "default.jpg";
			user.setImageName(imageName);
			Userdetail saveUser = userService.saveUser(user);

			if (!ObjectUtils.isEmpty(saveUser)) {
				if (!file.isEmpty()) {
					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
							+ file.getOriginalFilename());

					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

					session.setAttribute("succMsg", "User saved successfully");
				} else {
					session.setAttribute("errMsg", "Something wrong on server");
				}
			} else {
				session.setAttribute("errMsg", "User not saved");
			}
		}

		return "redirect:/register";
	}

	// Forgot password

	@GetMapping("/forgotPassword")
	public String forgotPassword() {
		return "forget_password";
	}

	@PostMapping("/forgotPassword")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {

		Userdetail userByEmail = userService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errMsg", "Invalid Email");
		} else {

			String resetToken = UUID.randomUUID().toString();

			userService.updateUserResetToken(email, resetToken);

			// Generate URL : http://localhost:8080/resetPassword?token=asdfghjuytrfdcvb

			String url = CommonUtil.generateUrl(request) + "/resetPassword?token=" + resetToken;
			// System.out.println(url);

			Boolean sendMail = commonUtil.sendMail(url, email);
			if (sendMail) {
				session.setAttribute("succMsg", "Please check your mail.. Password reset link is sent");
			} else {
				session.setAttribute("errMsg", "Something wrong on server ! mail not sent");
			}
		}

		return "redirect:/forgotPassword";
	}

	@GetMapping("/resetPassword")
	public String resetPasswordPage(@RequestParam String token, HttpSession session, Model m) {

		Userdetail userByToken = userService.getUserByToken(token);

		if (userByToken == null) {
			m.addAttribute("errMsg", "Your link is invalid or expired");
			return "error";
		}
		m.addAttribute("token", token);
		return "/reset_password";
	}

	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session,
			Model m) {

		Userdetail userByToken = userService.getUserByToken(token);

		if (userByToken == null) {
			m.addAttribute("msg", "Your link is invalid or expired");
			return "message";
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			session.setAttribute("succMsg", "Password changed Suceesfully");
			m.addAttribute("msg", "Password changed Suceesfully");
			return "message";
		}
	}

	@GetMapping("/search")
	public String searchProduct(@RequestParam String ch, Model m) {

		List<Product> searchProduct = productService.searchProduct(ch);
		m.addAttribute("products", searchProduct);

		List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("categories", categories);

		return "product";
	}

	@GetMapping("adminlogin")
	public String adminLogin() {
		return "adminLogin";
	}

}
