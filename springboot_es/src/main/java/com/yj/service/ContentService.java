package com.yj.service;

import com.alibaba.fastjson.JSON;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.yj.entity.Content;
import com.yj.utils.ParseHtmlUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private ParseHtmlUtils parseHtmlUtils;

    public boolean parseContent(String keyword) throws Exception {



        List<Content> contents = parseHtmlUtils.parseJd(keyword);
        System.out.println(contents);
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 0; i < contents.size(); i++) {

        bulkRequest.add(new IndexRequest("jd").source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        boolean b = bulk.hasFailures();
        return !b;
    }


    // 根据添加去es 中查询并分页
    public List<Map<String, Object>> searchContent(String keyword, int pageNum, int pageSize) throws IOException {
        List<Map<String,Object>> list = new ArrayList<>();

        if(pageNum<=1){
            pageNum = 1;
        }

        SearchRequest searchRequest = new SearchRequest("jd");


        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // term 精确匹配
        TermQueryBuilder termQuery = QueryBuilders.termQuery("name", keyword);


        sourceBuilder.query(termQuery);

        sourceBuilder.from(pageNum);

        sourceBuilder.size(pageSize);

        // 设置高亮
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.preTags("<span style='color:red'>");
//        highlightBuilder.highlightQuery(termQuery);
//        highlightBuilder.postTags("</span>");
//        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Map<String, Object> sourceAsMap = new HashMap<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            sourceAsMap = hit.getSourceAsMap();

            list.add(sourceAsMap);
        }
        System.out.println(sourceAsMap);
        return list;
    }

    // 根据添加去es 中查询并分页 并且高亮
    public List<Map<String, Object>> searchHighLightContent(String keyword, int pageNum, int pageSize) throws IOException {
        List<Map<String,Object>> list = new ArrayList<>();

        if(pageNum<=1){
            pageNum = 1;
        }

        SearchRequest searchRequest = new SearchRequest("jd");


        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // term 精确匹配
        TermQueryBuilder termQuery = QueryBuilders.termQuery("name", keyword);


        sourceBuilder.query(termQuery);

        sourceBuilder.from(pageNum);

        sourceBuilder.size(pageSize);

        // 设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.field("name");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Map<String, Object> sourceAsMap = new HashMap<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            sourceAsMap = hit.getSourceAsMap();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            HighlightField name = highlightFields.get("name");
            if(name!=null){
                Text[] fragments = name.fragments();
                String n_name = "";
                for (Text text : fragments) {
                    n_name += text;

                }
                System.out.println(n_name);
                sourceAsMap.put("name", n_name);
            }

            list.add(sourceAsMap);
        }
        System.out.println(sourceAsMap);
        return list;
    }

}
