package com.lkm.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

//@Configuration
//public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {
//    @Override
//    @Bean
//    public RestHighLevelClient elasticsearchClient() {
//        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("192.168.1.66",9200,"http")
//                )
//        );
//        return restHighLevelClient;
//    }
//}

@Configuration
public class ElasticSearchConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.1.66", 9200, "http")
                )
        );
        return restHighLevelClient;
    }
}
