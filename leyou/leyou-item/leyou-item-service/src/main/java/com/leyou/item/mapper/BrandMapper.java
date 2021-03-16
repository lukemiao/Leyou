package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    //    新增品牌
    @Insert("insert into tb_category_brand (category_id,brand_id) values (#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    //    编辑品牌
    @Update("update tb_category_brand set category_id = #{cid} where brand_id = #{bid}")
    void updateCategoryByBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    void deleteCategoryIdByBrandId(@Param("bid") Long bid);

    @Select("select * from tb_brand where id in(select brand_id from tb_category_brand where category_id =#{cid})")
    //SELECT * FROM tb_brand a INNER JOIN tb_category_brand b on b.brand_id=a.id WHERE b.category_id=76
    List<Brand> selectBrandNameBycid(@Param("cid") Long cid);
}
