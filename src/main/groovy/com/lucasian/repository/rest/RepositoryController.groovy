package com.lucasian.repository.rest

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile

/**
 * Created by blzb on 10/28/15.
 */
@Controller
class RepositoryController {
  @RequestMapping(
    value = "/repository/post",
    method = RequestMethod.POST
  )
  public ResponseEntity uploadFile(MultipartFile file) {
    println(file.name)
    println(file.originalFilename)
    println(file.size)
    return new ResponseEntity("{}", HttpStatus.OK);
  }

}
