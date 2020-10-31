package com.yj;

import com.yj.service.ContentService;
import com.yj.utils.ParseHtmlUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@SpringBootTest
class SpringbootEsApplicationTests {

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private ElasticsearchRestTemplate restTemplate;

	@Autowired
	private ParseHtmlUtils parseHtmlUtils;


	@Autowired
	private ContentService contentService;

	@Test
	void contextLoads() throws Exception {
		Boolean aBoolean = contentService.parseContent("java");
		System.out.println(aBoolean);
	}


}
