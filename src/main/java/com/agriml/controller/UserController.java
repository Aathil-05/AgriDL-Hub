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

import com.agriml.model.Cart;
import com.agriml.model.Category;
import com.agriml.model.OrderRequest;
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
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private CartService cartService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

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

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {

		Cart saveCart = cartService.saveCart(pid, uid);
		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errMsg", "Product add to cart failed");
		} else {
			session.setAttribute("succMsg", "Product added to cart");
		}

		return "redirect:/viewDetails/" + pid;
	}

	@GetMapping("/cart")
	public String cartPage(Principal p, Model m) {

		Userdetail user = commonUtil.getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartByUser(user.getId());
		m.addAttribute("carts", carts);
		m.addAttribute("cartSize",carts.size());
		if (carts.size() > 0) {
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			m.addAttribute("totalOrderPrice", totalOrderPrice);
		}
		return "user/cart";
	}

	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {

		cartService.updateQuantity(sy, cid);
		return "redirect:/user/cart";
	}

	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
		Userdetail user = commonUtil.getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartByUser(user.getId());
		m.addAttribute("carts", carts);
		if (carts.size() > 0) {
			Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
			Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 250 + 100;
			m.addAttribute("totalOrderPrice", totalOrderPrice);
			m.addAttribute("orderPrice", orderPrice);
		}

		return "user/order";
	}

	@PostMapping("/saveOrder")
	public String saveOrder(@ModelAttribute OrderRequest request, Principal p) throws Exception {
		// System.out.println(request);
		Userdetail user = commonUtil.getLoggedInUserDetails(p);
		orderService.saveOrder(user.getId(), request);

		return "redirect:/user/success";
	}

	@GetMapping("/success")
	public String loadSuccess() {
		return "user/success";
	}

	@GetMapping("/userOrders")
	public String myOrders(Model m, Principal p) {

		Userdetail loginUser = commonUtil.getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrderByUser(loginUser.getId());
		m.addAttribute("orders", orders);

		return "user/myOrders";
	}

	@GetMapping("/updateStatus")
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
		return "redirect:/user/userOrders";
	}

	@GetMapping("/addProducts")
	public String addProducts(Model m) {
		List<Category> categories = categoryService.getAllCategory();
		m.addAttribute("categories", categories);
		return "user/addProduct";
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile file, Principal p,
			HttpSession session) throws IOException {

		Userdetail user = commonUtil.getLoggedInUserDetails(p);

		String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();

		product.setUser(user);

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

		return "redirect:/user/addProducts";
	}

	@GetMapping("/myProducts")
	public String userProducts(Model m, Principal p, @RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "9") Integer pageSize) {

		Userdetail user = commonUtil.getLoggedInUserDetails(p);

		Page<Product> page = null;
		if (ch != null && ch.length() > 0) {
			page = productService.searchProductPagination(pageNo, pageSize, ch);
		} else {
			page = productService.getAllProductsByUserIdPagination(user.getId(), pageNo, pageSize);
		}
		List<Product> products = page.getContent();
		m.addAttribute("products", products);
		m.addAttribute("productSize", products.size());
		m.addAttribute("pageNo", page.getNumber());
		m.addAttribute("pageSize", pageSize);
		m.addAttribute("totalElements", page.getTotalElements());
		m.addAttribute("totalPages", page.getTotalPages());
		m.addAttribute("isFirst", page.isFirst());
		m.addAttribute("isLast", page.isLast());

		return "user/myProducts";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product deleted successfully");
		} else {
			session.setAttribute("errMsg", "Something wrong on server");
		}

		return "redirect:/user/myProducts";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("category", categoryService.getAllCategory());
		return "user/editProduct";
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

		return "redirect:/user/editProduct/" + product.getId();
	}

	@GetMapping("/profile")
	public String profile() {
		return "user/profile";
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

		return "redirect:/user/profile";
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

		return "redirect:/user/profile";
	}
}
