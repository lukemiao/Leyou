package com.leyou.item.api;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {

    // 为 search 中 cids 提供方法
    @GetMapping
    public List<String> selectNamesByIds(@RequestParam("ids") List<Long> ids);


}
