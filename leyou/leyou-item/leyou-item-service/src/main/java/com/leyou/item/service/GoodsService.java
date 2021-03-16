package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    SpuMapper spuMapper;

    @Autowired
    SpuDetailMapper spuDetailMapper;

    @Autowired
    BrandMapper brandMapper;

    @Autowired
    CategoryService categoryService;

    @Autowired
    SkuMapper skuMapper;

    @Autowired
    StockMapper stockMapper;

    @Autowired
    AmqpTemplate amqpTemplate;

    /**
     * 根据条件来分页查询spu
     *
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> selectSpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //添加查询条件
        if (StringUtils.isNotBlank(key))
            criteria.andLike("title", "%" + key + "%");

        //添加山下架的过滤条件
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        //添加分页
        PageHelper.startPage(page, rows);

        //执行查询 获取spu集合
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo<Spu> PageInfo = new PageInfo<>(spus);

        //spu集合转化成spubo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);

            //查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());

            //查询分类名称
            List<String> names = categoryService.selectNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "-"));
            return spuBo;

        }).collect(Collectors.toList());

        //返回PageResult<spuBo>
        return new PageResult<>(PageInfo.getTotal(), spuBos);
    }

    /**
     * 新增商品
     *
     * @param spuBo
     * @return
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //新增数据存在先后顺序   spu -> spuDetail -> sku -> stock
        //抽取方法 新增 spu spuDetail
        //1. 新增 spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuMapper.insertSelective(spuBo);

        //2. 新增 spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);

        //抽取方法 新增 sku stock
        saveSkuAndStock(spuBo);

        SendMsg("insert", spuBo.getId());

    }

    private void SendMsg(String type, Long id) {
        try {
            amqpTemplate.convertAndSend("item." + type, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        //新增 sku stock
        List<Sku> skus = spuBo.getSkus();
        skus.forEach(sku -> {
            //新增sku
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);
            //新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        });
    }

    /**
     * 通过spuId获取 spuDetail
     *
     * @param spuId
     * @return
     */
    public SpuDetail selectSpuDetailBySpuId(Long spuId) {
//        SpuDetail record = new SpuDetail();
//        record.setSpuId(spuId);
//        return spuDetailMapper.selectOne(record);
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据 SpuId 查询 skus
     *
     * @param spuId
     * @return
     */
    public List<Sku> selectSkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(record);
        //还有库存信息
//        skus.forEach(sku -> {
//            Integer stock = stockMapper.selectByPrimaryKey(sku.getId()).getStock();
//            sku.setStock(stock);
//        });
//        return skus;
        return skus.stream().map(sku -> {
            Integer stock = stockMapper.selectByPrimaryKey(sku.getId()).getStock();
            sku.setStock(stock);
            return sku;
        }).collect(Collectors.toList());
    }

    /**
     * 更新商品信息
     *
     * @param spuBo
     * @return
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        //顺序删除 stock -> 删除 stock -> 添加 sku -> 添加 stock -> 更新 spu -> 更新 spuDetail

        //先根据spuId查询要删除的sku
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = skuMapper.select(record);

        //删除 stock  sku
        skus.forEach(sku -> {
            //删除 stock
            stockMapper.deleteByPrimaryKey(sku.getId());
            //删除 sku
            skuMapper.deleteByPrimaryKey(sku.getId());
        });

        //添加 sku stock
        saveSkuAndStock(spuBo);

        //更新 spu spuDetail
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        spuMapper.updateByPrimaryKeySelective(spuBo);

        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        SendMsg("update", spuBo.getId());
    }

    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }
}
