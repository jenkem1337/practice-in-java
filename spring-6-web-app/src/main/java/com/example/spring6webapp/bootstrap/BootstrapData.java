package com.example.spring6webapp.bootstrap;

import com.example.spring6webapp.domain.Author;
import com.example.spring6webapp.domain.Book;
import com.example.spring6webapp.domain.Publisher;
import com.example.spring6webapp.repositories.AuthorRepository;
import com.example.spring6webapp.repositories.BookRepository;
import com.example.spring6webapp.repositories.PublisherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BootstrapData implements CommandLineRunner {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    public BootstrapData(BookRepository bookRepository, AuthorRepository authorRepository, PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var addisonWesley = new Publisher();
        addisonWesley.setName("Addison-Wesley");
        addisonWesley.setAddress("Example address");
        addisonWesley.setCity("Boston");
        addisonWesley.setState("USA");
        addisonWesley.setZip("231231");

        var savedAddisonWesley = publisherRepository.save(addisonWesley);


        var eric = new Author();
        eric.setFirstName("Eric");
        eric.setLastName("Evens");

        var ddd = new Book();
        ddd.setTitle("Domain Driven Design");
        ddd.setIsbn("123456789");
        ddd.setPublisher(savedAddisonWesley);

        var savedEric = authorRepository.save(eric);
        var savedDDD = bookRepository.save(ddd);

        var rod = new Author();
        rod.setFirstName("Rod");
        rod.setLastName("Johnson");

        var noEJB = new Book();
        noEJB.setTitle("J2EE Development without EJB");
        noEJB.setIsbn("987654321");
        noEJB.setPublisher(savedAddisonWesley);

        var savedRod = authorRepository.save(rod);
        var savedNoEjb = bookRepository.save(noEJB);

        savedEric.getBooks().add(savedDDD);
        savedRod.getBooks().add(savedNoEjb);
        savedDDD.getAuthors().add(savedEric);
        savedNoEjb.getAuthors().add(savedRod);

        authorRepository.save(savedRod);
        authorRepository.save(savedEric);
        bookRepository.save(savedNoEjb);
        bookRepository.save(savedDDD);

        System.out.println("Bootstrap");
        System.out.println("Author count : " + authorRepository.count());
        System.out.println("Book count : " + bookRepository.count());
        System.out.println("Publisher count : " + publisherRepository.count());
    }
}
