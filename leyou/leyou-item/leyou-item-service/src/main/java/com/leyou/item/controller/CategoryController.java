package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /*  [分类管理]
        根据父节点的id查询子节点
    */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam(value = "pid", defaultValue = "0") Long pid) {

        if (pid == null || pid < 0) {
            //400: 参数不合法
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = categoryService.queryCategoriesByPid(pid);
        if (CollectionUtils.isEmpty(categories)) {
            //404: 响应数据为空
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.notFound().build();
        }
        //200: 查询成功
        return ResponseEntity.ok(categories);

    }

    /**
     * 缺少 增删改
     */

    /**
     * 返回categories分类信息
     *
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> selectCategoryByBrandId(@PathVariable("bid") Long bid) {
        if (bid == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Category> categorie = categoryService.selectCategoryByBrandId(bid);
        if (categorie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categorie);
    }

    // 为 search 中 cids 提供方法
    @GetMapping
    public ResponseEntity<List<String>> selectNamesByIds(@RequestParam("ids") List<Long> ids) {
        List<String> names = categoryService.selectNamesByIds(ids);
        if (names.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }


}
