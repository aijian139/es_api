package com.yj;

import com.alibaba.fastjson.JSON;
import com.yj.entity.User;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class EsDemo1ApplicationTests {

	@Qualifier("client")
	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private ElasticsearchRestTemplate restTemplate;


	// 添加索引
	@Test
	public void indices() throws IOException {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest("yijian");
		CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);

		System.out.println(createIndexResponse);



	}

	@Test
	public void add(){
		IndexRequest request = new IndexRequest("posts");
		request.id("1");
		String jsonString = "{" +
				"\"user\":\"kimchy\"," +
				"\"postDate\":\"2013-01-30\"," +
				"\"message\":\"trying out Elasticsearch\"" +
				"}";
		request.source(jsonString, XContentType.JSON);

	}


	// 获取索引 判断是否存在
	@Test
	public void testExist () throws IOException {
		GetIndexRequest getIndexRequest = new GetIndexRequest("yijian");

		boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);

		System.out.println(exists);
	}

	// 删除索引
	@Test
	public void testDelete() throws IOException {
		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("yijian");
		AcknowledgedResponse delete = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
		System.out.println(delete.isAcknowledged());
	}

	// 向索引中添加文档
	@Test
	public void testAddDocument() throws IOException {

		// 向文档中添加的数据
		User user = new User().setName("意见").setAddress("湖南长沙");

		IndexRequest indexRequest = new IndexRequest("yijian");

		indexRequest.id("1");
		indexRequest.timeout(TimeValue.timeValueMinutes(1));

		indexRequest.source(JSON.toJSONString(user),XContentType.JSON);

		IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

		RestStatus status = indexResponse.status();

		System.out.println(status);

	}

	// 获取文档信息
	@Test
	public void testGetDocument() throws IOException {
		GetRequest getRequest = new GetRequest("yijian","1");
		GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

		System.out.println(getResponse.getSourceAsString());
		System.out.println(getResponse);

	}

	// 更新文档信息
	@Test
	public void testUpdateDocument() throws IOException {

		UpdateRequest updateRequest = new UpdateRequest("yijian", "1");
		User user = new User().setName("小刘").setAddress("湖南永州");
		updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);

		UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

		RestStatus status = updateResponse.status();
		System.out.println(status);

	}

	// 删除文档内容
	@Test
	public void testDeleteDocuemnt() throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest("yijian", "1");

		DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);

		RestStatus status = deleteResponse.status();
		System.out.println(status);
	}

	// 批量操作
	@Test
	public void testBatchDocument() throws IOException {
		BulkRequest bulkRequest = new BulkRequest();
		//IndexRequest indexRequest = new IndexRequest("yijian");
		List<User> list = new ArrayList<>();

		list.add(new User("张三","湖南岳阳"));
		list.add(new User("小王","湖南长沙"));
		list.add(new User("小一","湖南衡阳"));
		list.add(new User("小er","湖南衡阳"));
		list.add(new User("小san","湖南衡阳"));
		list.add(new User("小si","湖南衡阳"));

		for (int i = 0; i <list.size() ; i++) {
			bulkRequest.add(new IndexRequest("yijian").id(""+i+1).source(JSON.toJSONString(list.get(i)),XContentType.JSON));
		}

		BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
		RestStatus status = bulkResponse.status();
		System.out.println(status);

	}

	// 查询
	@Test
	public void testQuery() throws IOException {
		SearchRequest searchRequest = new SearchRequest("yijian");

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", "张三");
		searchSourceBuilder.query(matchQueryBuilder);
		searchSourceBuilder.from();
		searchSourceBuilder.size();


		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

		SearchHits hits = searchResponse.getHits();
		hits.forEach(documentFields -> {
			System.out.println(documentFields);
		});
	}

}
