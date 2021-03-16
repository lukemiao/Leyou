package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    SpecificationService specificationService;

    /**
     *  根据cid查询对应 spec_group 中信息
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> selectGroupsByCid(@PathVariable("cid") Long cid) {
        List<SpecGroup> groups = specificationService.selectGroupsByCid(cid);
        if(groups.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    /**
     *  根据cid查询对应 spec_param 中信息
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> selectParams(
            @RequestParam(value = "gid" ,required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
        List<SpecParam> params = specificationService.selectParams(gid,cid,generic,searching);
        if(params.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid")Long cid){
        List<SpecGroup> specGroups = specificationService.queryGroupsWithParam(cid);
        if(specGroups.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroups);
    }

    /**
     *  2个增、删、改
     */





}
