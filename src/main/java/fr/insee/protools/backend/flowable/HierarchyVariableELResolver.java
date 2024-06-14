package fr.insee.protools.backend.flowable;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.el.VariableContainerELResolver;
import org.flowable.common.engine.impl.javax.el.ELContext;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.entitylink.api.EntityLink;
import org.flowable.entitylink.api.EntityLinkService;
import org.flowable.entitylink.service.EntityLinkServiceConfiguration;

import java.util.List;

public class HierarchyVariableELResolver
        extends VariableContainerELResolver {
    public static final String ROOT_TOKEN = "root";
    public static final String PARENT_TOKEN = "parent";
    public static final String SELF_TOKEN = "self";
    public static final String BPMN_TOKEN = "bpmn";

    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null) {
            Object resolvedVariableContainer = resolveVariableContainer(getVariableContainer(context), context, property);
            if (resolvedVariableContainer != null) {
                return resolvedVariableContainer;
            }
            return super.getValue(context, base, property);
        }

        if (base instanceof VariableContainer) {
            Object resolvedVariableContainer = resolveVariableContainer(base, context, property);
            if (resolvedVariableContainer != null) {
                return resolvedVariableContainer;
            }

            String variable = (String) property;
            if (((VariableContainer) base).hasVariable(variable)) {
                context.setPropertyResolved(true);
                return ((VariableContainer) base).getVariable(variable);
            }
        }
        return null;
    }


    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base == null && property instanceof  String) {
            String variable = (String) property;
            VariableContainer variableContainer = getVariableContainer(context);
            if (variableContainer.hasVariable(variable)) {
                context.setPropertyResolved(true);
                variableContainer.setVariable(variable, value);
            }

        } else if (base instanceof VariableContainer && property instanceof  String) {
            String variable = (String) property;
            VariableContainer baseVariableContainer = (VariableContainer) base;
            if (baseVariableContainer.hasVariable(variable)) {
                context.setPropertyResolved(true);
                baseVariableContainer.setVariable(variable, value);
            }
            //custom
            else {
                context.setPropertyResolved(true);
                baseVariableContainer.setVariable(variable, value);
            }
        }
    }

    protected Object resolveVariableContainer(Object possibleVariableContainer, ELContext context, Object property) {
        if (ROOT_TOKEN.equals(property)) {
            if (possibleVariableContainer instanceof ExecutionEntity) {
                ExecutionEntity executionVariableContainer = (ExecutionEntity) possibleVariableContainer;
                context.setPropertyResolved(true);
                return resolveRoot(executionVariableContainer);
            }

        } else if (PARENT_TOKEN.equals(property)) {
            if (possibleVariableContainer instanceof ExecutionEntity) {
                ExecutionEntity executionVariableContainer = (ExecutionEntity) possibleVariableContainer;
                context.setPropertyResolved(true);
                return resolveParent(executionVariableContainer);
            }
        } else if (SELF_TOKEN.equals(property)) {
            if (possibleVariableContainer instanceof ExecutionEntity) {
                context.setPropertyResolved(true);
                ExecutionEntity executionEntity = (ExecutionEntity) possibleVariableContainer;
                if (!executionEntity.isProcessInstanceType()) {
                    return executionEntity.getProcessInstance();
                }
                return executionEntity;
            }
            context.setPropertyResolved(true);
            return getVariableContainer(context);
        }


        return null;
    }

    protected VariableContainer resolveRoot(ExecutionEntity variableContainer) {
        EntityLinkService entityLinkService = getEntityLinkService(getProcessEngineConfiguration());
        List<EntityLink> entityLinks = entityLinkService.findEntityLinksByReferenceScopeIdAndType(variableContainer.getProcessInstanceId(), "bpmn", "child");


        if (entityLinks != null) {
            for (EntityLink entityLink : entityLinks) {
                if (ROOT_TOKEN.equals(entityLink.getHierarchyType())) {
                    if (BPMN_TOKEN.equals(entityLink.getScopeType())) {
                        return getProcessInstance(entityLink.getScopeId());
                    }
                }
            }
        }

        return variableContainer.getProcessInstance();
    }

    protected VariableContainer resolveParent(ExecutionEntity variableContainer) {
        EntityLinkService entityLinkService = getEntityLinkService(getProcessEngineConfiguration());
        List<EntityLink> entityLinks = entityLinkService.findEntityLinksByReferenceScopeIdAndType(variableContainer.getProcessInstanceId(), BPMN_TOKEN, "child");
        entityLinks=entityLinks.stream().filter(entityLink -> BPMN_TOKEN.equals(entityLink.getScopeType())).toList();

        if (entityLinks != null) {
            EntityLink candidateLink = null;
            for (EntityLink entityLink : entityLinks) {
                if (PARENT_TOKEN.equals(entityLink.getHierarchyType())) {
                    return getProcessInstance(entityLink.getScopeId());
                }
                if (ROOT_TOKEN.equals(entityLink.getHierarchyType())) {
                    candidateLink = entityLink;
                }
            }
            if (candidateLink != null) {
                return getProcessInstance(candidateLink.getScopeId());
            }
        }

        return variableContainer.getProcessInstance();
    }


    protected ExecutionEntity getProcessInstance(String processInstanceId) {
        ExecutionEntity executionEntity = getProcessEngineConfiguration().getExecutionEntityManager().findById(processInstanceId);
        if (executionEntity == null || !executionEntity.isProcessInstanceType()) {
            throw new FlowableIllegalArgumentException("Unable to resolve process instance with id " + processInstanceId);
        }
        return executionEntity;
    }


    protected EntityLinkService getEntityLinkService(ProcessEngineConfigurationImpl processEngineConfiguration) {
        EntityLinkServiceConfiguration entityLinkServiceConfiguration = (EntityLinkServiceConfiguration) processEngineConfiguration.getServiceConfigurations().get("cfg.entityLinkService");
        return entityLinkServiceConfiguration.getEntityLinkService();
    }

    protected ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
        return CommandContextUtil.getProcessEngineConfiguration();
    }
}