package com.leyou.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    BrandClient brandClient;

    @Autowired
    CategoryClient categoryClient;

    @Autowired
    GoodsClient goodsClient;

    @Autowired
    SpecificationClient specificationClient;

    @Autowired
    GoodsRepository goodsRepository;

    static final ObjectMapper MAPPER = new ObjectMapper();
    private String name;

    public SearchResult search(SearchRequest request) {

        if (request.getKey().isEmpty()) {
            return null;
        }
        //自定义查询构建起
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
//        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND);
        BoolQueryBuilder basicQuery = buildBoolQueryBuilder(request);
        queryBuilder.withQuery(basicQuery);
        //添加分页，分页页码从0开始
        queryBuilder.withPageable(PageRequest.of(request.getPage() - 1, request.getSize()));
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));

        //添加分类和品牌的聚合
        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //执行查询，获取结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        //获取聚合结果集并解析
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        //判断是否是一个分类，只有一个分类时才做规格参数聚合
        List<Map<String, Object>> specs = null;
        if (!categories.isEmpty() && categories.size() == 1) {
            //对规格参数进行聚合
            specs = getParamAggResult((Long) categories.get(0).get("id"), basicQuery);
        }
        return new SearchResult(goodsPage.getTotalElements(), goodsPage.getTotalPages(), goodsPage.getContent(), categories, brands, specs);
    }

    /**
     * 构建布尔查询
     *
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //给布尔查询添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        //添加过滤条件
        //获取用户选择的过滤信息
        Map<String, Object> filter = request.getFilter();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                key = "cid3";
            } else {
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 根据查询条件聚合规格参数
     *
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {
        //自定义查询对象构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本查询条件
        queryBuilder.withQuery(basicQuery);

        //查询要聚合的规格参数
        List<SpecParam> params = specificationClient.selectParams(null, cid, null, true);

        //添加规格参数的聚合
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
        //添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

        //执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());

        List<Map<String, Object>> specs = new ArrayList<>();
        //解析聚合结果集，key-聚合名称（规格参数名） value-聚合对象
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            //初始化一个map{k:}
            Map<String, Object> map = new HashMap<>();
            map.put("k", entry.getKey());
            //初始化一个options集合，搜集桶中的key
            List<String> options = new ArrayList<>();
            //获取聚合
            StringTerms terms = (StringTerms) entry.getValue();
            //获取桶集合
            terms.getBuckets().forEach(bucket -> {
                options.add(bucket.getKeyAsString());
            });
            map.put("options", options);
            specs.add(map);
        }
        return specs;
    }

    /**
     * 解析品牌的聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;

//        List<Brand> brands = new ArrayList<>();
        //获取聚合中的桶

        return terms.getBuckets().stream().map(bucket -> {
            return brandClient.selectBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());

//        terms.getBuckets().forEach(bucket -> {
//            Brand brand = brandClient.selectBrandById(bucket.getKeyAsNumber().longValue());
//            brands.add(brand);
//        });
//
//        return brands;
    }

    /**
     * 解析分类的聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;

        //获取桶的集合 转化成 List<Map<String, Object>>集合
        return terms.getBuckets().stream().map(bucket -> {
            //初始化一个map
            Map<String, Object> map = new HashMap<>();
            //获取桶中的分类id（key）
            long id = bucket.getKeyAsNumber().longValue();
            //根据分类id查询分类名称
            List<String> names = categoryClient.selectNamesByIds(Arrays.asList(id));
            map.put("id", id);
            map.put("name", names.get(0));
            return map;
        }).collect(Collectors.toList());
    }

    public Goods buildGoods(Spu spu) throws JsonProcessingException {
        Goods goods = new Goods();

        //All : 根据分类 id 查询分类名称
        List<String> names = categoryClient.selectNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //All : 根据 SpuId 查询 品牌Id
        Brand brand = brandClient.selectBrandById(spu.getBrandId());

        //根据 SpuId 查询所有的 sku
        List<Sku> skus = goodsClient.selectSkusBySpuId(spu.getId());

        //初始化一个价格
        List<Long> prices = new ArrayList<>();

        //搜集sku的必要字段信息
        List<Map<String, Object>> skuMaps = new ArrayList<>();

        skus.forEach(sku -> {
            prices.add(sku.getPrice());

            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            //获取sku中的图片，数据库中的图片 可能是多张 是以“，”来分割，所以也以逗号来切割返回图片数组，获取第一张图片
            map.put("image", sku.getImages().isEmpty() ? "" : StringUtils.split(sku.getImages(), ",")[0]);

            skuMaps.add(map);
        });

        //根据spu中的 cid3 查询出所有的搜索规格参数
        List<SpecParam> specParams = specificationClient.selectParams(null, spu.getCid3(), null, true);

        //先根据spuId查询spuDetail
        SpuDetail spuDetail = goodsClient.selectSpuDetailBySpuId(spu.getId());
        //把通用的规格参数值，进行反序列化
        Map<String, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        //把特殊的规格参数值，进行反序列化
        Map<String, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });

        //创建map接收值
        Map<String, Object> specs = new HashMap<>();
        specParams.forEach(specParam -> {
            //判断规格参数的类型，是否是通用的
            if (specParam.getGeneric()) {
                //如果是通用类型的参数，从genericSpecMap获取规格参数值
                String value = genericSpecMap.get(specParam.getId().toString()).toString();
                //判断是否是数值类型，如果是数值类型，应该返回一个区间
                if (specParam.getNumeric()) {
                    value = chooseSegment(value, specParam);
                }
                specs.put(specParam.getName(), value);
            } else {
                //如果是特殊的规格参数，从specialSpecMap中获取值
                List<Object> value = specialSpecMap.get(specParam.getId().toString());
                specs.put(specParam.getName(), value);
            }
        });

        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接 All 字段，需要分类名称以及品牌名称
        goods.setAll(spu.getTitle() + "" + StringUtils.join(names, "") + "" + brand.getName());
        //获取 spu 下的所有 sku 的价格
        goods.setPrice(prices);
        //获取 spu 下的所有 sku ，并转化成json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMaps));
        //获取所有查询的规格参数 { name ：value }
        goods.setSpecs(specs);
        return goods;

    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
    public void save(Long id) throws JsonProcessingException {
        Spu spu = goodsClient.querySpuById(id);
        Goods goods = buildGoods(spu);
        goodsRepository.save(goods);
    }

    public void delete(Long id) {
        goodsRepository.deleteById(id);
    }
}
