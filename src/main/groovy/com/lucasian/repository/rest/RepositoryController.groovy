package com.lucasian.repository.rest

import com.lucasian.repository.RepositoryItem
import com.lucasian.repository.RepositoryItemContents
import com.lucasian.repository.RepositoryService
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
  RepositoryService repositoryService

  @RequestMapping(
    value = "/repository/post",
    method = RequestMethod.POST
  )
  public ResponseEntity uploadFile(FileUploadForm form) {
    RepositoryItem item = new RepositoryItem(
      name: form.file.originalFilename,
      path: form.path,
      tags: [],
      contents: new RepositoryItemContents(
        binary: form.file.bytes,
        metadata: [:]
      )
    )
    String id = repositoryService.storeItemAndGetId(item)
    println("File stored with id: ${id}")
    return new ResponseEntity("{}", HttpStatus.OK);
  }

}
