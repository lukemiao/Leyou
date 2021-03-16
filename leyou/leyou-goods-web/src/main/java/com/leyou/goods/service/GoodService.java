package com.leyou.goods.service;


import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GoodService {

    @Autowired
    BrandClient brandClient;
    @Autowired
    CategoryClient categoryClient;
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    SpecificationClient specificationClient;

    public Map<String, Object> loadData(Long SpuId) {
        Map<String, Object> model = new HashMap<>();

        //根据spuId查询 spu
        Spu spu = goodsClient.querySpuById(SpuId);

        //根据spuId查询 spuDetail
        SpuDetail spuDetail = goodsClient.selectSpuDetailBySpuId(SpuId);

        //查询分类：Map<String,Object>
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = categoryClient.selectNamesByIds(cids);
        //初始化一个分类的map
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }

        //查询品牌
        Brand brand = brandClient.selectBrandById(spu.getBrandId());

        //skus
        List<Sku> skus = goodsClient.selectSkusBySpuId(SpuId);

        //查询规格数组
        List<SpecGroup> groups = specificationClient.queryGroupsWithParam(spu.getCid3());

        //查询特殊的规格参数
        List<SpecParam> params = specificationClient.selectParams(null, spu.getCid3(), false, null);
        //初始化特殊规格参数的map
        Map<Long, String> paramMap = new HashMap<>();
        params.forEach(param -> {
            paramMap.put(param.getId(), param.getName());
        });
        model.put("spu", spu);
        model.put("spuDetail", spuDetail);
        model.put("categories", categories);
        model.put("brand", brand);
        model.put("skus", skus);
        model.put("groups", groups);
        model.put("paramMap", paramMap);
        return model;
    }

}
