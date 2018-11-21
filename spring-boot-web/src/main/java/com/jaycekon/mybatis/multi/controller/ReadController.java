package com.jaycekon.mybatis.multi.controller;

import com.jaycekon.mybatis.multi.dao.ReadRepository;
import com.jaycekon.mybatis.multi.domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by weijie_huang on 2017/9/20.
 */
//@Controller
public class ReadController {
    @Autowired
    private ReadRepository readRepository;


    @RequestMapping(value="/{reader}", method= RequestMethod.GET)
    public String readersBooks(
            @PathVariable("reader") String reader,
            Model model) {
        List<Book> readingList =
                readRepository.findByReader(reader);
        if (readingList != null) {
            model.addAttribute("books", readingList);
        }
        return "readingList";
    }

    @RequestMapping(value="/{reader}", method=RequestMethod.POST)
    public String addToReadingList(
            @PathVariable("reader") String reader, Book book) {
        book.setReader(reader);
        readRepository.save(book);
        return "redirect:/{reader}";
    }

}
