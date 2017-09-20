package com.jaycekon.demo.dao;

import com.jaycekon.demo.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by weijie_huang on 2017/9/20.
 */
@Component
public interface ReadRepository extends JpaRepository<Book,Long> {
    List<Book> findByReader(String reader);
}
