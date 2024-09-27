package fr.insee.protools.backend.service.scheduled;

import fr.insee.protools.backend.repository.IUniteEnquetee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaskService {

  private final IUniteEnquetee iUniteEnquetee;

  public TaskService(IUniteEnquetee iUniteEnquetee) {
    this.iUniteEnquetee = iUniteEnquetee;
  }

  public boolean isTerminated(String processInstanceId, String currentActivityId, long nbInterogation) throws InterruptedException {
    log.info("IUniteEnquetee.isTerminated");
//    TODO : iUniteEnquetee.getCommandesBygetProcessInstanceIdAndIdTask(1,2)
    return iUniteEnquetee.isTerminated(processInstanceId, currentActivityId, nbInterogation);
  }


}