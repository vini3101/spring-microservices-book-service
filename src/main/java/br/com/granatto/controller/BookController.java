package br.com.granatto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.granatto.model.Book;
import br.com.granatto.proxy.CambioProxy;
import br.com.granatto.repository.BookRepository;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Book endpoint")
@RestController
@RequestMapping("book-service")
public class BookController {
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private CambioProxy proxy;
	
	@Autowired
	private BookRepository repository;
	
	@Operation(summary = "Find a specific book by ID")
	@GetMapping(value = "/{id}/{currency}")
	@Retry(name = "default")
	@CircuitBreaker(name = "default")
	@RateLimiter(name = "default")
	@Bulkhead(name = "default")
	public Book findBook(
			@PathVariable("id") Long id,
			@PathVariable("currency") String currency
			) {
		
		var book = repository.getById(id);
		
		if(book == null) throw new RuntimeException("Book not found");
		
	
		
		var cambio = proxy.getCambio(book.getPrice(), "USD", currency);
		
		var port = environment.getProperty("local.server.port");
		
		book.setPrice(cambio.getConvertedValued());
		
		book.setEnvironment(port);
		
		return book;
	}

}
