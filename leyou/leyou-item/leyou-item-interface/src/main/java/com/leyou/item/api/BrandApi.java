package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.*;

@RequestMapping("brand")
public interface BrandApi {

    // 为 search 中 brand 提供方法
    @GetMapping("{id}")
    public Brand selectBrandById(@PathVariable("id") Long id) ;


}
