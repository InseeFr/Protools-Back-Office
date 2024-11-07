package fr.insee.protools.backend.service.exception;

import fr.insee.protools.backend.exception.ProtoolsBpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

//TODO : Créer une classe mère de toutes nos expcetions pour l'attrapper dans les controller advice
public class ProtoolsTaskBPMNError extends ProtoolsBpmnError {
    public ProtoolsTaskBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}
