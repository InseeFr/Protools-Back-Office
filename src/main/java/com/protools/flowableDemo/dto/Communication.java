package com.protools.flowableDemo.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.protools.flowableDemo.enums.CommunicationModel;
import com.protools.flowableDemo.enums.CommunicationProtocol;
import com.protools.flowableDemo.enums.CommunicationSupport;
import com.protools.flowableDemo.enums.CommunicationType;
import lombok.Data;

import java.util.Collection;

@Data
public class Communication {
    private String id;

    private String label;

    @JacksonXmlProperty(localName = "Protocole")
    private CommunicationProtocol protocol;

    @JacksonXmlProperty(localName = "MoyenCommunication")
    private CommunicationSupport support;

    @JacksonXmlProperty(localName = "TypeCommunication")
    private CommunicationType type;

    @JacksonXmlProperty(localName = "ModeleCommunication")
    private CommunicationModel model;

    @JacksonXmlProperty(localName = "Echeance")
    private DeadLine deadLine;

    @JacksonXmlProperty(localName = "Objet")
    private String object;

    @JacksonXmlProperty(localName = "MailRetour")
    private String returnMail;

    @JacksonXmlProperty(localName = "ContenuCommunication")
    private Collection<Variable> content;

    public void setDeadLine(DeadLine deadLine) throws Exception {
        if (!deadLine.isValid()) {
            throw new Exception(String.format(
                    "deadline %s - %s is not valid", deadLine.getType(), deadLine.getValue()));
        }
    }
}
