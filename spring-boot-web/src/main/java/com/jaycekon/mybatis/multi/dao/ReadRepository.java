package com.jaycekon.mybatis.multi.dao;

import com.jaycekon.mybatis.multi.domain.Book;
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
