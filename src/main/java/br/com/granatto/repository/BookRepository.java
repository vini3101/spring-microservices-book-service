package br.com.granatto.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.granatto.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
