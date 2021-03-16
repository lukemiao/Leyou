package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    BrandMapper brandMapper;

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
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化 example 对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        // 根据 name 模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        //添加分页条件
        PageHelper.startPage(page, rows);

        //添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        List<Brand> brands = brandMapper.selectByExample(example);
        //包装成pageinfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        //包装成分页结果集返回
//        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList()brands);
        return new PageResult<>(pageInfo.getTotal(), brands);
    }

    /**
     * 新增品牌
     *
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {

        //先新增brand
        brandMapper.insertSelective(brand);

        //再新增中间表
        cids.forEach(cid -> {
            brandMapper.insertCategoryAndBrand(cid, brand.getId());
        });

    }

    public void updateBrand(Brand brand, List<Long> cids) {
        //先更新brand
        brandMapper.updateByPrimaryKey(brand);
        //再更新中间表
        cids.forEach(cid -> {
            brandMapper.updateCategoryByBrand(cid, brand.getId());
        });

    }

    public void deletBrand(Long bid) {
        //先删除brand
//        Brand record = new Brand();
//        record.setId(bid);
//        brandMapper.delete(record);
        brandMapper.deleteByPrimaryKey(bid);
        //再删除中间表
        brandMapper.deleteCategoryIdByBrandId(bid);
    }

    /**
     * 根据分类id查询品牌列表
     *
     * @param cid
     */
    public List<Brand> selectBrandNameBycid(Long cid) {
        return brandMapper.selectBrandNameBycid(cid);
    }

    // 为 search 中 brand 提供方法
    public Brand selectBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
