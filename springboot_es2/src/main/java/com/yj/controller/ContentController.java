package com.yj.controller;

import com.yj.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping({"/","/index"})
    public String index(){
        return "index";
    }

    @GetMapping("/search/{keyword}/{pageNum}/{pageSize}")
    @ResponseBody
    public List<Map<String,Object>> searchContents(@PathVariable String keyword,@PathVariable Integer pageNum,@PathVariable Integer pageSize) throws IOException {
        List<Map<String, Object>> list = contentService.searchHighLightContents(keyword, pageNum, pageSize);
        return list;
    }

}
