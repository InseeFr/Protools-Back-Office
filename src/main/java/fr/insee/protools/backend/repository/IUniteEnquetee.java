package fr.insee.protools.backend.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IUniteEnquetee {

	@Transactional
	void addManyUniteEnquetee(List<JsonNode> listeUe);

	void addManyUniteEnquetee(List<JsonNode> listeUe, String processInstanceId, String currentActivityId);

	void addManyUniteEnqueteeDeleteColonneClass(List<JsonNode> listeUe);

	boolean isTerminated(String processInstanceId, String currentActivityId, long numberCommandes);

	long getCommandesByProcessInstanceIdAndCurrentActivityId(String processInstanceId, String currentActivityId);
}