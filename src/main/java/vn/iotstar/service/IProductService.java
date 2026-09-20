package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Product;

public interface IProductService {

	List<Product> findAll();

	Optional<Product> findById(Long id);

	<S extends Product> S save(S entity);

	void deleteById(Long id);

	void delete(Product entity);

	List<Product> findByProductNameContaining(String name);

	Page<Product> findByProductNameContaining(String name, Pageable pageable);

	Optional<Product> findByProductName(String name);
}
