package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    SpecGroupMapper groupMapper;

    @Autowired
    SpecParamMapper paramMapper;

    /**
     * 根据cid查询参数组
     *
     * @param cid
     * @return
     */
    public List<SpecGroup> selectGroupsByCid(Long cid) {
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        return groupMapper.select(record);
    }

    /**
     * 根据gid查询
     *
     * @param gid
     * @return
     */
    public List<SpecParam> selectParams(Long gid, Long cid, Boolean generic, Boolean searching) {

        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setGeneric(generic);
        record.setSearching(searching);
        return paramMapper.select(record);
    }

    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        List<SpecGroup> groups = selectGroupsByCid(cid);
        groups.forEach(group -> {
            List<SpecParam> params = selectParams(group.getId(), null, null, null);
            group.setParams(params);
        });
        return groups;
    }
}
