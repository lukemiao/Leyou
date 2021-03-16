package com.leyou.item.service;

import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

/*
    根据父节点查询子节点
*/
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);
        return categoryMapper.select(record);

        //此方法与上述方法一样 注意驼峰命名
//       return categoryMapper.selectByPid(pid);
    }

    /**
     *  根据bid查询cid再得到category
     * @param bid
     * @return
     */
    public List<Category> selectCategoryByBrandId(Long bid) {
        return categoryMapper.selectCategoryByBrandId(bid);
    }

    /**
     *
     * @param ids
     * @return
     */
    public List<String> selectNamesByIds(List<Long> ids ){
        List<Category> categories = categoryMapper.selectByIdList(ids);
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }
}
