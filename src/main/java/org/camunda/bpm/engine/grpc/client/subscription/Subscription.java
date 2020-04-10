package org.camunda.bpm.engine.grpc.client.subscription;

import org.camunda.bpm.engine.grpc.client.domain.BusinessKey;

import java.util.List;

public interface Subscription {

    /**
     * @return the topic name of the subscription
     */
    String getTopicName();

    /**
     * @return <ul>
     * <li> the duration of the lock applied to the topic
     * <li> if {@code null}, the client or the default lock duration is applied
     * </ul>
     */
    Long getLockDuration();

    /**
     * @return the external task handler of the topic
     */
    SubscriptionHandler getHandler();

    /**
     * @return a list of variable names which are supposed to be retrieved
     */
    List<String> getVariableNames();

    /**
     * @return the business key associated with the external tasks which are supposed to be fetched and locked
     */
    BusinessKey getBusinessKey();

    /**
     * @return the process definition id associated with the external tasks which are supposed to be fetched and locked
     */
    String getProcessDefinitionId();


    /**
     * @return the process definition ids associated with the external tasks which are supposed to be fetched and locked
     */
    List<String> getProcessDefinitionIdIn();

    /**
     * @return the process definition key associated with the external tasks which are supposed to be fetched and locked
     */
    String getProcessDefinitionKey();

    /**
     * @return the process definition keys associated with the external tasks which are supposed to be fetched and locked
     */
    List<String> getProcessDefinitionKeyIn();

    /**
     * @return the process definition version tag associated with the external task which are supposed to be fetched and locked
     */
    String getProcessDefinitionVersionTag();

    /**
     * @return the tenant id presence for associated with the external tasks which are supposed to be fetched and locked
     */
    boolean isWithoutTenantId();

    /**
     * @return the tenant ids associated with the external tasks which are supposed to be fetched and locked
     */
    List<String> getTenantIdIn();
}
