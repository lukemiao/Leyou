package com.leyou.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class SearchTests {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    SearchService searchService;

    @Autowired
    GoodsClient goodsClient;

    @Test
    void test() {
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);

        Integer page = 1;
        Integer rows = 100;
        do {
            //分页查询spu，获取分页结果集
            PageResult<SpuBo> result = goodsClient.selectSpuBoByPage(null, null, page, rows);
            //获取当前页的数据
            List<SpuBo> items = result.getItems();
            //处理 List<SpuBo> ==> List<Goods>
            List<Goods> goodsList = items.stream().map(spuBo -> {
                try {
                    return searchService.buildGoods(spuBo);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            //执行新增数据的方法
            goodsRepository.saveAll(goodsList);

            rows = items.size();
            page++;
        } while (rows == 100);


    }
    @Autowired
    private CategoryClient categoryClient;
    @Test
    public void testQueryCategories() {
        List<String> names = this.categoryClient.selectNamesByIds(Arrays.asList(1L, 2L, 3L));
        names.forEach(System.out::println);
    }
}
