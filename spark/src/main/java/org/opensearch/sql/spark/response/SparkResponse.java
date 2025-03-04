/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.sql.spark.response;

import static org.opensearch.sql.spark.data.constants.SparkConstants.SPARK_INDEX_NAME;

import com.google.common.annotations.VisibleForTesting;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.opensearch.ResourceNotFoundException;
import org.opensearch.action.ActionFuture;
import org.opensearch.action.DocWriteResponse;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.Client;
import org.opensearch.common.util.concurrent.ThreadContext;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.sql.datasources.exceptions.DataSourceNotFoundException;

@Data
public class SparkResponse {
  private final Client client;
  private String value;
  private final String field;
  private static final Logger LOG = LogManager.getLogger();

  /**
   * Response for spark sql query.
   *
   * @param client Opensearch client
   * @param value  Identifier field value
   * @param field  Identifier field name
   */
  public SparkResponse(Client client, String value, String field) {
    this.client = client;
    this.value = value;
    this.field = field;
  }

  public JSONObject getResultFromOpensearchIndex() {
    return searchInSparkIndex(QueryBuilders.termQuery(field, value));
  }

  private JSONObject searchInSparkIndex(QueryBuilder query) {
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.indices(SPARK_INDEX_NAME);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(query);
    searchRequest.source(searchSourceBuilder);
    ActionFuture<SearchResponse> searchResponseActionFuture;
    try {
      searchResponseActionFuture = client.search(searchRequest);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    SearchResponse searchResponse = searchResponseActionFuture.actionGet();
    if (searchResponse.status().getStatus() != 200) {
      throw new RuntimeException(
          "Fetching result from " + SPARK_INDEX_NAME + " index failed with status : "
          + searchResponse.status());
    } else {
      JSONObject data = new JSONObject();
      for (SearchHit searchHit : searchResponse.getHits().getHits()) {
        data.put("data", searchHit.getSourceAsMap());
        deleteInSparkIndex(searchHit.getId());
      }
      return data;
    }
  }

  @VisibleForTesting
  void deleteInSparkIndex(String id) {
    DeleteRequest deleteRequest = new DeleteRequest(SPARK_INDEX_NAME);
    deleteRequest.id(id);
    ActionFuture<DeleteResponse> deleteResponseActionFuture;
    try {
      deleteResponseActionFuture = client.delete(deleteRequest);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    DeleteResponse deleteResponse = deleteResponseActionFuture.actionGet();
    if (deleteResponse.getResult().equals(DocWriteResponse.Result.DELETED)) {
      LOG.debug("Spark result successfully deleted ", id);
    } else if (deleteResponse.getResult().equals(DocWriteResponse.Result.NOT_FOUND)) {
      throw new ResourceNotFoundException("Spark result with id "
          + id + " doesn't exist");
    } else {
      throw new RuntimeException("Deleting spark result information failed with : "
          + deleteResponse.getResult().getLowercase());
    }
  }
}
