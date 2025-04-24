package com.example.spring6webapp.services;

import com.example.spring6webapp.domain.Author;

public interface AuthorService {
    Iterable<Author> findAll();
}
