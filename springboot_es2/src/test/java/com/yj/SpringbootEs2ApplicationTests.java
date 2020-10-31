package com.yj;

import com.yj.service.ContentService;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class SpringbootEs2ApplicationTests {

	@Autowired
	@Qualifier("client")
	private RestHighLevelClient client;

	@Autowired
	private ContentService contentService;


	@Test
	void contextLoads() {
	}

	@Test
	void testEs() throws IOException {
		IndexRequest indexRequest = new IndexRequest("yijian");
		Map<String,Object> map = new HashMap<>();

		map.put("name", "易健");

		indexRequest.source(map, XContentType.JSON);

		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

		System.out.println(indexResponse.status());

	}

	@Test
	public void testParseContests() throws Exception {
		String keyword = "java";
		Boolean aBoolean = contentService.parseContents(keyword);
		System.out.println(aBoolean);
	}

	@Test
	public void testSearchContents() throws IOException {
		String keyword = "java";
		int pageNum = 1;
		int pageSize = 10;

		List<Map<String, Object>> list = contentService.searchContents(keyword, pageNum, pageSize);
		System.out.println(list);
	}


	// 删除
	@Test
	public void testDelete(){
		new DeleteRequest("");
	}
}
