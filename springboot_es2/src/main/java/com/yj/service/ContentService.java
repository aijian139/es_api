package com.yj.service;

import com.alibaba.fastjson.JSON;
import com.yj.pojo.Content;
import com.yj.utils.ParseJdUtils;
import org.apache.lucene.search.highlight.Highlighter;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ContentService {

    @Autowired
    @Qualifier("client")
    private RestHighLevelClient client;


    @Autowired
    private ParseJdUtils parseJdUtils;

    // 向es 服务器中添加数据
    public Boolean parseContents(String keyword) throws Exception {

        List<Content> contents = parseJdUtils.parseJd(keyword);

        BulkRequest bulkRequest = new BulkRequest();

        for (int i = 0; i < contents.size(); i++) {
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        bulkResponse.status();

        return !bulkResponse.hasFailures();
    }

    // 查询es 服务器中的数据
    public List<Map<String, Object>> searchContents(String keyword, Integer pageNum, Integer size) throws IOException {
        SearchRequest searchRequest = new SearchRequest("jd_goods");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("name", keyword);

        searchSourceBuilder.from(pageNum);

        searchSourceBuilder.size(size);

        searchSourceBuilder.query(matchQuery);

        searchRequest.source(searchSourceBuilder);

        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            list.add(sourceAsMap);
        }

        return list;
    }

    // 查询es 服务器中的数据并且高亮字段
    public List<Map<String, Object>> searchHighLightContents(String keyword, Integer pageNum, Integer size) throws IOException {
        SearchRequest searchRequest = new SearchRequest("jd_goods");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery("name", keyword);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        searchSourceBuilder.highlighter(highlightBuilder);

        searchSourceBuilder.from(pageNum);

        searchSourceBuilder.size(size);

        searchSourceBuilder.query(matchQuery);

        searchRequest.source(searchSourceBuilder);

        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            HighlightField name = highlightFields.get("name");

            //System.out.println("HighlightField:-------------------->"+name);

            Text[] fragments = name.fragments();

            String n_name = "";
            for (Text text : fragments) {
                n_name += text;
                System.out.println(text);
            }
            sourceAsMap.put("name", n_name);
            list.add(sourceAsMap);
        }

        return list;
    }





}
