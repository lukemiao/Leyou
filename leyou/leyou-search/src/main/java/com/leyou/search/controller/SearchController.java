package com.leyou.search.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    @Autowired
    SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest request) {
        SearchResult result = searchService.search(request);
        if (result == null || result.getItems().isEmpty()){
            return  ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(result);

    }
}
