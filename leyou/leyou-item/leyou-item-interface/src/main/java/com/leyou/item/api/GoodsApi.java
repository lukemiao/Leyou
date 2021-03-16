package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    /**
     * 通过spuId获取 spuDetail
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    SpuDetail selectSpuDetailBySpuId(@PathVariable("spuId") Long spuId);

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
    public PageResult<SpuBo> selectSpuBoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", defaultValue = "true") Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    );


    /**
     *  根据 SpuId 查询 sku集合
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> selectSkusBySpuId(@RequestParam("id") Long spuId);


    @GetMapping("{id}")
    public Spu querySpuById(@PathVariable("id") Long id);
}
