package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    /**
     * 根据条件来分页查询spu
     *
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> selectSpuBoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", defaultValue = "true") Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    ) {
        PageResult<SpuBo> pageResult = goodsService.selectSpuBoByPage(key, saleable, page, rows);

        if (pageResult == null || CollectionUtils.isEmpty(pageResult.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 新增商品
     *
     * @param spuBo
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo) {
        goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新商品信息
     *
     * @param spuBo
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo) {
        goodsService.updateGoods(spuBo);
        return ResponseEntity.noContent().build();
    }


    /**
     * 删除还没做
     */


    /**
     * 通过spuId获取 spuDetail
     *
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> selectSpuDetailBySpuId(@PathVariable("spuId") Long spuId) {
        SpuDetail spuDetail = goodsService.selectSpuDetailBySpuId(spuId);
        if (spuDetail == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据 SpuId 查询 sku集合
     *
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> selectSkusBySpuId(@RequestParam("id") Long spuId) {
        List<Sku> skus = goodsService.selectSkusBySpuId(spuId);
        if (skus.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }

    @GetMapping("{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        Spu spu = goodsService.querySpuById(id);
        if (spu == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }

}
