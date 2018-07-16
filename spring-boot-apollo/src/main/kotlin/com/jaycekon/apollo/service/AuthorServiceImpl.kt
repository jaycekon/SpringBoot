package com.jaycekon.apollo.service
import com.jaycekon.apollo.dao.AuthorDao
import com.jaycekon.apollo.entity.Author
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("authorService")
open class AuthorServiceImpl : AuthorService {

    @Autowired
    private lateinit var authorDao: AuthorDao

    override fun update(author: Author): Int {
        return this.authorDao.update(author)
    }

    override fun add(author: Author): Int {
        return this.authorDao.add(author)
    }

    override fun delete(id: Long): Int {
        return this.authorDao.delete(id)
    }

    override fun findAuthor(id: Long): Author? {
        return this.authorDao.findAuthor(id)
    }

    override fun findAuthorList(): List<Author> {
        return this.authorDao.findAuthorList()
    }
}