package vn.iotstar.controller.api;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping(path = "/api/product")
public class ProductAPIController {

	@Autowired
	private IProductService productService;

	@Autowired
	private ICategoryService categoryService;

	@Autowired
	private IStorageService storageService;

	@GetMapping
	public ResponseEntity<?> getAllProduct() {
		return new ResponseEntity<Response>(new Response(true, "Thành công", productService.findAll()), HttpStatus.OK);
	}

	@PostMapping(path = "/addProduct", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> addProduct(@Validated @RequestParam("productName") String productName,
			@RequestParam("quantity") int quantity, @RequestParam("unitPrice") double unitPrice,
			@RequestParam(value = "images", required = false) MultipartFile image,
			@RequestParam(value = "description", defaultValue = "") String description,
			@RequestParam(value = "discount", defaultValue = "0") double discount,
			@RequestParam("categoryId") Long categoryId) {

		Optional<Category> optCategory = categoryService.findById(categoryId);
		if (optCategory.isEmpty()) {
			return new ResponseEntity<Response>(new Response(false, "Category không tồn tại trong hệ thống", null),
					HttpStatus.BAD_REQUEST);
		}

		Product product = new Product();
		product.setProductName(productName);
		product.setQuantity(quantity);
		product.setUnitPrice(unitPrice);
		product.setDescription(description);
		product.setDiscount(discount);
		product.setCreateDate(new Date());
		product.setStatus((short) 1);
		product.setCategory(optCategory.get());

		if (image != null && !image.isEmpty()) {
			UUID uuid = UUID.randomUUID();
			String filename = storageService.getSorageFilename(image, uuid.toString());
			storageService.store(image, filename);
			product.setImages(filename);
		}

		productService.save(product);
		return new ResponseEntity<Response>(new Response(true, "Thêm sản phẩm thành công", product), HttpStatus.OK);
	}
}
