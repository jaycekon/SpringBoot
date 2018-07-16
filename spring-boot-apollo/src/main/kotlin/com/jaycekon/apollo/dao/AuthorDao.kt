package com.jaycekon.apollo.dao

import com.jaycekon.apollo.entity.Author


interface AuthorDao {
    fun add(author: Author): Int
    fun update(author: Author): Int
    fun delete(id: Long): Int
    fun findAuthor(id: Long): Author?
    fun findAuthorList(): List<Author>
}