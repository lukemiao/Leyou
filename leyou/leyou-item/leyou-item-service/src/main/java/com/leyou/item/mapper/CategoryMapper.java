package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category,Long> {
    //尝试一下 查询所有分类 已成功    以后用select即可
//    @Select("select * from tb_category where parent_id = #{pid}")
//    List<Category> selectByPid(@Param("pid") Long pid);

    //    查询 brand_id 得到 category_id 可能有多个  查询到的 category 数组
    @Select("select * from tb_category where id in (select category_id from tb_category_brand where brand_id = #{bid} )")
    List<Category> selectCategoryByBrandId(@Param("bid") Long bid);


}
