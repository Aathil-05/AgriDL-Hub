package com.agriml.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.agriml.model.Category;
import com.agriml.model.Product;
import com.agriml.model.ProductOrder;
import com.agriml.model.Userdetail;
import com.agriml.service.CartService;
import com.agriml.service.CategoryService;
import com.agriml.service.OrderService;
import com.agriml.service.ProductService;
import com.agriml.service.UserService;
import com.agriml.util.CommonUtil;
import com.agriml.util.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

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
	public String index() {
		return "admin/index";
	}

	@GetMapping("/category")
	public String category(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize) {
		// m.addAttribute("categories", categoryService.getAllCategory());
		Page<Category> page = categoryService.getAllCategoryPagination(pageNo, pageSize);
		List<Category> category = page.getContent();
		m.addAttribute("categories", category);
		m.addAttribute("categorySize", category.size());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/category";
	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existCategory = categoryService.existsCategory(category.getName());

		if (existCategory) {
			session.setAttribute("errMsg", "Category Name Already exist");
		} else {

			Category saveCategory = categoryService.saveCategory(category);

			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errMsg", "Not saved ! internal server error");
			} else {

				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("succMsg", "Saved successfully");
			}
		}
		return "redirect:/admin/category";
	}

	@GetMapping("/deletecategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {

		Boolean deleteCategory = categoryService.deleteCategory(id);
		if (deleteCategory) {
			session.setAttribute("succMsg", "Category deleted successfully");
		} else {
			session.setAttribute("errMsg", "Something wrong on server");
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/editcategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/editCategory";
	}

	@PostMapping("/updatecategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(category)) {

			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}

		Category updateCategory = categoryService.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updateCategory)) {

			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());

				// System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("succMsg", "Category update success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

		return "redirect:/admin/editcategory/" + category.getId();
	}

	@GetMapping("/addproduct")
	public String addProduct(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "addProduct";
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();

		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {
			File saveFile = new ClassPathResource("static/img").getFile();

			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
					+ file.getOriginalFilename());

			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Product saved !!");
		} else {
			session.setAttribute("errMsg", "Something Wrong on server");
		}

		return "redirect:/admin/addproduct";
	}

	@GetMapping("/viewProducts")
	public String viewProduct(Model m, @RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "9") Integer pageSize) {
		
		Page<Product> page = null;
		if (ch != null && ch.length() > 0) {
			page = productService.searchProductPagination(pageNo, pageSize, ch);
		} else {
			page = productService.getAllProductsPagination(pageNo, pageSize);
		}
		m.addAttribute("products", page.getContent());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product deleted successfully");
		} else {
			session.setAttribute("errMsg", "Something wrong on server");
		}

		return "redirect:/admin/viewProducts";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("category", categoryService.getAllCategory());
		return "editProduct";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Invalid discount");
		} else {

			Product updateProduct = productService.updateProduct(product, file);

			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Product update success");
			} else {
				session.setAttribute("errorMsg", "something wrong on server");
			}
		}

		return "redirect:/admin/editProduct/" + product.getId();
	}

	@GetMapping("/users")
	public String userDetails(Model m,@RequestParam Integer type) {
		
		List<Userdetail> users = null;
		if(type==1) {
			users = userService.getAllUsers("ROLE_USER");
		} else {
			users =  userService.getAllUsers("ROLE_ADMIN");
		}
		m.addAttribute("userType",type);
		m.addAttribute("users",users);		
		return "admin/users";
	}

	@GetMapping("/updateStatus")
	public String updateUserAccStatus(@RequestParam Boolean status, @RequestParam Integer id,@RequestParam Integer type, HttpSession session) {

		Boolean f = userService.updateAccountStatus(id, status);

		if (f) {
			session.setAttribute("succMsg", "Account status updated");
		} else {
			session.setAttribute("errMsg", "Something wrong on server");
		}

		return "redirect:/admin/users?type="+type;
	}

	@GetMapping("/orders")
	public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "9") Integer pageSize) {
		Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
		m.addAttribute("orders", page.getContent());
		m.addAttribute("srch", false);

		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "admin/orders";
	}

	@PostMapping("/updateOrderStatus")
	public String updateOrder(@RequestParam Integer id, @RequestParam Integer st, HttpSession session)
			throws Exception {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderStatus : values) {

			if (orderStatus.getId().equals(st)) {
				status = orderStatus.getName();
			}
		}

		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);
		commonUtil.sendMailForProductOrder(updateOrder, status);

		if (!ObjectUtils.isEmpty(updateOrder)) {
			session.setAttribute("succMsg", "Status Updated");
		} else {
			session.setAttribute("errMsg", "Status not updated");
		}
		return "redirect:/admin/orders";
	}

	@GetMapping("/searchOrder")
	public String searchProduct(@RequestParam String orderId, Model m, HttpSession session,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "9") Integer pageSize) {

		if (orderId != null || orderId.length() > 0) {

			ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());

			if (ObjectUtils.isEmpty(order)) {
				session.setAttribute("errMsg", "Incorrect Order id");
				m.addAttribute("orderDetails", null);
			} else {
				m.addAttribute("orderDetails", order);
			}
			m.addAttribute("srch", true);
		} else {
			Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
			m.addAttribute("orders", page);
			m.addAttribute("srch", false);

			m.addAttribute("pageNo", page.getNumber());
			m.addAttribute("pageSize", pageSize);
			m.addAttribute("totalElements", page.getTotalElements());
			m.addAttribute("totalPages", page.getTotalPages());
			m.addAttribute("isFirst", page.isFirst());
			m.addAttribute("isLast", page.isLast());
		}
		return "admin/orders";
	}

	@GetMapping("/addAdmin")
	public String addAdmin() {
		return "admin/addAdmin";
	}

	@PostMapping("/saveAdmin")
	public String saveAdmin(@ModelAttribute Userdetail user, @RequestParam("image") MultipartFile file,
			HttpSession session) throws IOException {
		String imageName = !file.isEmpty() ? file.getOriginalFilename() : "default.jpg";
		user.setImageName(imageName);
		Userdetail saveUser = userService.saveAdmin(user);

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

		return "redirect:/admin/addAdmin";
	}
	
	@GetMapping("/profile")
	public String profile() {
		return "admin/profile";
	}

	@PostMapping("/updateProfile")
	public String updateProfile(@ModelAttribute Userdetail user, @RequestParam("file") MultipartFile img,
			HttpSession session) {

		Userdetail updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			session.setAttribute("errMsh", "Profile not updated");
		} else {
			session.setAttribute("succMsg", "Profile updated successfully");
		}

		return "redirect:/admin/profile";
	}

	@PostMapping("/changePassword")
	public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword, Principal p,
			HttpSession session) {

		Userdetail loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			Userdetail updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				session.setAttribute("errMsg", "Something wrong on server");
			} else {
				session.setAttribute("succMsg", "Password updated successfully");
			}

		} else {
			session.setAttribute("errMsg", "Current password is Incorrect");
		}

		return "redirect:/admin/profile";
	}
	
	
}
