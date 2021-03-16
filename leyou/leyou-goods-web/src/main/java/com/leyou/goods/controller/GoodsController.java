package com.leyou.goods.controller;

import com.leyou.goods.service.GoodService;
import com.leyou.goods.service.GoodsHtmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class GoodsController {

    @Autowired
    GoodService goodService;

    @Autowired
    GoodsHtmlService goodsHtmlService;

    @GetMapping("item/{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model) {
        Map<String, Object> map = goodService.loadData(id);
        model.addAllAttributes(map);

        goodsHtmlService.creatHtml(id);
        return "item";
    }
}
