package fr.insee.protools.backend.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RequiredArgsConstructor
@Repository
@Slf4j
public class UniteEnqueteeImpl implements IUniteEnquetee {

	private final MongoTemplate mongoTemplate;

	@Setter
    @Getter
    private String processInstanceId;
	@Setter
	@Getter
	private String currentActivityId;

	private static final ObjectMapper objectMapper =
			new ObjectMapper()
					.configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
					.configure(FAIL_ON_MISSING_CREATOR_PROPERTIES, true);


	private BasicDBObject getUEUpdate(JsonNode test){
		String correlationID = UUID.randomUUID().toString();
		BasicDBObject dbObject = new BasicDBObject();
		HashMap<String, Object> keyValuePairs = null;
		keyValuePairs = new HashMap<>();
        try {
            keyValuePairs.put("payload",  new ObjectMapper().writeValueAsString(test));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        keyValuePairs.put("correlationID", correlationID);
		keyValuePairs.put("inProgress", false);
		keyValuePairs.put("done", false);
		keyValuePairs.put("processInstanceID", getProcessInstanceId());
		keyValuePairs.put("currentActivityID", getCurrentActivityId());
		dbObject.putAll(keyValuePairs);
		return dbObject;
	}


	@Override
	public void addManyUniteEnquetee(List<JsonNode> listeUe) {
		List<BasicDBObject> bo = listeUe.parallelStream()
				.map(this::getUEUpdate)
				.toList();
		mongoTemplate.insert(bo, "commandes");
	}

	@Override
	public void addManyUniteEnqueteeDeleteColonneClass(List<JsonNode> listeUe) {
		List<BasicDBObject> bo = listeUe.parallelStream()
				.map(this::getUEUpdate)
				.toList();
		//remove _class
		MappingMongoConverter converter =
				new MappingMongoConverter(mongoTemplate.getMongoDatabaseFactory(), new MongoMappingContext());
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		MongoTemplate mongoTemplate2 = new MongoTemplate(mongoTemplate.getMongoDatabaseFactory(), converter);
		mongoTemplate2.insert(bo, "commandes");
	}

	@Override
	public void addManyUniteEnquetee(List<JsonNode> listeUe, String processInstanceId, String currentActivityId) {
		this.setProcessInstanceId(processInstanceId);
		this.setCurrentActivityId(currentActivityId);
//		List<BasicDBObject> bo = listeUe.parallelStream()
//				.map(this::getUEUpdate)
//				.toList();
		//remove _class
		MappingMongoConverter converter =
				new MappingMongoConverter(mongoTemplate.getMongoDatabaseFactory(), new MongoMappingContext());
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		MongoTemplate mongoTemplate2 = new MongoTemplate(mongoTemplate.getMongoDatabaseFactory(), converter);
		mongoTemplate2.insert(listeUe, "commandes");
	}

	@Override
	public boolean isTerminated(String processInstanceId, String currentActivityId, long numberCommandes) {
		log.debug("UniteEnqueteeImpl.isTerminated.");
		boolean result=false;
		Query query = new Query();
		query.addCriteria(Criteria.where("processInstanceID").is(processInstanceId));
		query.addCriteria(Criteria.where("currentActivityID").is(currentActivityId));
		query.addCriteria(where("inProgress").is(true).and("done").is(true));
		List<String> listeCommandes = mongoTemplate.find(query, String.class, "commandes");
		if(!listeCommandes.isEmpty() && listeCommandes.size()==numberCommandes){
			result=true;
		}
		return result;
	}

	@Override
	public long getCommandesByProcessInstanceIdAndCurrentActivityId(String processInstanceId, String currentActivityId) {
		log.debug("getCommandesByProcessInstanceIdAndCurrentActivityId : " + processInstanceId + "|" + currentActivityId);
		long result=0;
		Query query = new Query();
		query.addCriteria(Criteria.where("processInstanceID").is(processInstanceId));
		query.addCriteria(Criteria.where("currentActivityID").is(currentActivityId));
		result = mongoTemplate.count(query, String.class, "commandes");
		return result;
	}

}