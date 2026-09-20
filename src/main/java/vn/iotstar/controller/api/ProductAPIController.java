package vn.iotstar.controller.api;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	@PostMapping(path = "/getProduct")
	public ResponseEntity<?> getProduct(@RequestParam("id") Long id) {
		Optional<Product> opt = productService.findById(id);
		if (opt.isPresent()) {
			return new ResponseEntity<Response>(new Response(true, "Thành công", opt.get()), HttpStatus.OK);
		}
		return new ResponseEntity<Response>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
	}

	@PostMapping(path = "/addProduct", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> addProduct(
			@Validated @RequestParam("productName") String productName,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
			@RequestParam(value = "images", required = false) MultipartFile images,
			@RequestParam(value = "unitPrice", defaultValue = "0") Double unitPrice,
			@RequestParam(value = "discount", defaultValue = "0") Double discount,
			@RequestParam(value = "description", defaultValue = "") String description,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "quantity", defaultValue = "0") Integer quantity,
			@RequestParam(value = "status", defaultValue = "1") Short status) {

		Optional<Product> optProduct = productService.findByProductName(productName);
		if (optProduct.isPresent()) {
			return new ResponseEntity<Response>(
					new Response(false, "Sản phẩm này đã tồn tại trong hệ thống", optProduct.get()),
					HttpStatus.BAD_REQUEST);
		}

		Product product = new Product();
		product.setProductName(productName);
		product.setQuantity(quantity);
		product.setUnitPrice(unitPrice);
		product.setDescription(description);
		product.setDiscount(discount);
		product.setCreateDate(new Date());
		product.setStatus(status);

		if (categoryId != null) {
			Optional<Category> optCategory = categoryService.findById(categoryId);
			optCategory.ifPresent(product::setCategory);
		}

		MultipartFile fileToUpload = (imageFile != null && !imageFile.isEmpty()) ? imageFile : images;
		if (fileToUpload != null && !fileToUpload.isEmpty()) {
			UUID uuid = UUID.randomUUID();
			String filename = storageService.getSorageFilename(fileToUpload, uuid.toString());
			storageService.store(fileToUpload, filename);
			product.setImages(filename);
		}

		productService.save(product);
		return new ResponseEntity<Response>(new Response(true, "Thành công", product), HttpStatus.OK);
	}

	@PutMapping(path = "/updateProduct", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<?> updateProduct(
			@RequestParam("productId") Long productId,
			@RequestParam("productName") String productName,
			@RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
			@RequestParam(value = "images", required = false) MultipartFile images,
			@RequestParam(value = "unitPrice", defaultValue = "0") Double unitPrice,
			@RequestParam(value = "discount", defaultValue = "0") Double discount,
			@RequestParam(value = "description", defaultValue = "") String description,
			@RequestParam(value = "categoryId", required = false) Long categoryId,
			@RequestParam(value = "quantity", defaultValue = "0") Integer quantity,
			@RequestParam(value = "status", defaultValue = "1") Short status) {

		Optional<Product> opt = productService.findById(productId);
		if (opt.isEmpty()) {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy sản phẩm", null),
					HttpStatus.BAD_REQUEST);
		}

		Product product = opt.get();
		product.setProductName(productName);
		product.setQuantity(quantity);
		product.setUnitPrice(unitPrice);
		product.setDescription(description);
		product.setDiscount(discount);
		product.setStatus(status);

		if (categoryId != null) {
			Optional<Category> optCategory = categoryService.findById(categoryId);
			optCategory.ifPresent(product::setCategory);
		}

		MultipartFile fileToUpload = (imageFile != null && !imageFile.isEmpty()) ? imageFile : images;
		if (fileToUpload != null && !fileToUpload.isEmpty()) {
			UUID uuid = UUID.randomUUID();
			String filename = storageService.getSorageFilename(fileToUpload, uuid.toString());
			storageService.store(fileToUpload, filename);
			product.setImages(filename);
		}

		productService.save(product);
		return new ResponseEntity<Response>(new Response(true, "Cập nhật Thành công", product), HttpStatus.OK);
	}

	@DeleteMapping(path = "/deleteProduct")
	public ResponseEntity<?> deleteProduct(@RequestParam("productId") Long productId) {
		Optional<Product> opt = productService.findById(productId);
		if (opt.isEmpty()) {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy sản phẩm", null),
					HttpStatus.BAD_REQUEST);
		}
		productService.delete(opt.get());
		return new ResponseEntity<Response>(new Response(true, "Xóa Thành công", opt.get()), HttpStatus.OK);
	}
}
