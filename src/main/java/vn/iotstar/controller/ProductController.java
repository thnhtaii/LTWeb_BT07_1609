package vn.iotstar.controller;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import vn.iotstar.service.IStorageService;

@Controller
@RequestMapping(path = "/admin/products")
public class ProductController {

	@Autowired
	private IStorageService storageService;

	@GetMapping(path = "/images/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		Resource file = storageService.loadAsResource(filename);
		String contentType = null;
		try {
			contentType = Files.probeContentType(file.getFile().toPath());
		} catch (IOException e) {
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		if (contentType == null) {
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
				.contentType(MediaType.parseMediaType(contentType))
				.body(file);
	}
}
