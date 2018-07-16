package com.jaycekon.apollo.controller

import com.alibaba.fastjson.JSONObject
import com.jaycekon.apollo.entity.Author
import com.jaycekon.apollo.service.AuthorService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(value = "/authors")
class AuthorController {

    @Autowired
    private lateinit var authorService: AuthorService

    /**
     * 查询用户列表
     */
    @RequestMapping(method = [RequestMethod.GET])
    fun getAuthorList(request: HttpServletRequest): Map<String, Any> {
        val authorList = this.authorService.findAuthorList()
        val param = HashMap<String, Any>()
        param["total"] = authorList.size
        param["rows"] = authorList
        return param
    }

    /**
     * 查询用户信息
     */
    @RequestMapping(value = "/{userId:\\d+}", method = [RequestMethod.GET])
    fun getAuthor(@PathVariable userId: Long, request: HttpServletRequest): Author {
        return authorService.findAuthor(userId) ?: throw RuntimeException("查询错误")
    }

    /**
     * 新增方法
     */
    @RequestMapping(method = [RequestMethod.POST])
    fun add(@RequestBody jsonObject: JSONObject) {
        val userId = jsonObject.getString("user_id")
        val realName = jsonObject.getString("real_name")
        val nickName = jsonObject.getString("nick_name")

        val author = Author()
        author.id = java.lang.Long.valueOf(userId)
        author.realName = realName
        author.nickName = nickName
        try {
            this.authorService.add(author)
        } catch (e: Exception) {
            throw RuntimeException("新增错误")
        }
    }

    /**
     * 更新方法
     */
    @RequestMapping(value = "/{userId:\\d+}", method = [RequestMethod.PUT])
    fun update(@PathVariable userId: Long, @RequestBody jsonObject: JSONObject) {
        var author = this.authorService.findAuthor(userId)
        val realName = jsonObject.getString("real_name")
        val nickName = jsonObject.getString("nick_name")
        try {
            if (author != null) {
                author.realName = realName
                author.nickName = nickName
                this.authorService.update(author)
            }
        } catch (e: Exception) {
            throw RuntimeException("更新错误")
        }

    }

    /**
     * 删除方法
     */
    @RequestMapping(value = "/{userId:\\d+}", method = [RequestMethod.DELETE])
    fun delete(@PathVariable userId: Long) {
        try {
            this.authorService.delete(userId)
        } catch (e: Exception) {
            throw RuntimeException("删除错误")
        }
    }
}