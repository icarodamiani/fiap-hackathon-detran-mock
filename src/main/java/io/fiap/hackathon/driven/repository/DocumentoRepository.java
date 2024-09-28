package io.fiap.hackathon.driven.repository;


import io.fiap.hackathon.driven.core.domain.Documento;
import io.fiap.hackathon.driven.core.domain.ImmutableDocumento;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@Repository
public class DocumentoRepository {
    private static final String TABLE_NAME = "detran_tb";

    private final DynamoDbAsyncClient client;

    public DocumentoRepository(DynamoDbAsyncClient client) {
        this.client = client;
    }

    public Mono<Void> save(Documento documento) {
        var atributos = new HashMap<String, AttributeValueUpdate>();
        atributos.put("EMITIDO",
            AttributeValueUpdate.builder().value(v -> v.s(documento.getEmitido().toString()).build()).build());
        atributos.put("WEBHOOK",
            AttributeValueUpdate.builder().value(v -> v.s(documento.getWebhook()).build()).build());

        var request = UpdateItemRequest.builder()
            .attributeUpdates(atributos)
            .tableName(TABLE_NAME)
            .key(Map.of("ID", AttributeValue.fromS(documento.getId())))
            .build();

        return Mono.fromFuture(client.updateItem(request))
            .then();
    }
    public Mono<Documento> findById(String id) {
        var request = QueryRequest.builder()
            .tableName(TABLE_NAME)
            .keyConditionExpression("#id = :id")
            .expressionAttributeNames(Map.of("#id", "ID"))
            .expressionAttributeValues(Map.of(":id", AttributeValue.fromS(id)))
            .build();

        return Mono.fromFuture(client.query(request))
            .filter(QueryResponse::hasItems)
            .map(response -> response.items().get(0))
            .map(this::convertItem);
    }

    private Documento convertItem(Map<String, AttributeValue> item) {
        return ImmutableDocumento.builder()
            .id(item.get("ID").s())
            .webhook(item.get("WEBHOOK").s())
            .emitido(Boolean.valueOf(item.get("EMITIDO").s()))
            .build();
    }
}
