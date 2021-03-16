package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandController {

    @Autowired
    BrandService brandService;

    /**
     * 根据查询条件分页并排序查询品牌信息
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("page")//前台的访问接口 key=&page=1&rows=5&sortBy=id&desc=false
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
//            @RequestParam(value = "sortBy",defaultValue = "id") String sortBy,
            @RequestParam(value = "sortBy", required = false) String sortBy,
//            @RequestParam(value = "desc",defaultValue = "asc") Boolean desc
            @RequestParam(value = "desc", required = false) Boolean desc
    ) {
        PageResult<Brand> result = brandService.queryBrandByPage(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(result.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     */
    @PostMapping //没有参数是因为  ？brand形式的
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 更新品牌
     *
     * @param
     * @param
     */
    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.updateBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 删除品牌
     */
    @DeleteMapping("bid/{bid}")
    public ResponseEntity<Void> deletBrand(@PathVariable("bid") Long bid) {
        brandService.deletBrand(bid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据分类id查询品牌列表
     *
     * @param cid
     */
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> selectBrandNameBycid(@PathVariable("cid") Long cid) {
        List<Brand> brands = brandService.selectBrandNameBycid(cid);
        if (brands.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brands);
    }

    // 为 search 中 brand 提供方法
    @GetMapping("{id}")
    public ResponseEntity<Brand> selectBrandById(@PathVariable("id") Long id) {
        Brand brand = brandService.selectBrandById(id);
        if (brand == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }

}
