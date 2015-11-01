package com.lucasian.repository.rest

import com.lucasian.repository.RepositoryItem
import com.lucasian.repository.RepositoryItemContents
import com.lucasian.repository.RepositoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

/**
 * Created by blzb on 10/28/15.
 */
@Controller
class RepositoryController {
  RepositoryService repositoryService

  @RequestMapping(
    value = "/repository/upload",
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
    return new ResponseEntity(id, HttpStatus.OK);
  }

  @RequestMapping(
    value = "/repository/createFolder",
    method = RequestMethod.POST
  )
  public ResponseEntity createFolder(String path, String name) {
    String id = repositoryService.createFolder(path, name)
    return new ResponseEntity(id, HttpStatus.OK);
  }

  @RequestMapping(
    value = "/repository/items",
    method = RequestMethod.GET
  )
  public ResponseEntity<List<RepositoryItem>> items(@RequestParam("path") String path) {
    return new ResponseEntity(repositoryService.listItemsInPath(path), HttpStatus.OK);
  }

  @RequestMapping(
    value = "/repository/folders",
    method = RequestMethod.GET
  )
  public ResponseEntity<List<RepositoryItem>> folders(@RequestParam("path") String path) {
    return new ResponseEntity(repositoryService.listFoldersInPath(path), HttpStatus.OK);
  }

  @RequestMapping(
    value = "/repository/item",
    method = RequestMethod.GET
  )
  public ResponseEntity<RepositoryItem> item(@RequestParam("path") String path) {
    getItem(repositoryService.getItemByPath(path))
  }
  @RequestMapping(
    value = "/repository/item/{id}",
    method = RequestMethod.GET
  )
  public ResponseEntity<RepositoryItem> itemById(@PathVariable("id") String id) {
    getItem(repositoryService.getItemById(id))
  }
  @RequestMapping(
    value = "/repository/item/{id}",
    method = RequestMethod.DELETE
  )
  public ResponseEntity<RepositoryItem> deleteItemById(@PathVariable("id") String id) {
    repositoryService.deleteLatestVersion(id)
    return new ResponseEntity('{}', HttpStatus.OK);
  }

  ResponseEntity getItem(Optional<RepositoryItem> item){
    if (item.isPresent()) {
      return new ResponseEntity(item.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity('{}', HttpStatus.NOT_FOUND);
    }
  }
}
