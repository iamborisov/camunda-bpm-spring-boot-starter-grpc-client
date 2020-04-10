package org.camunda.bpm.engine.grpc.client.subscription;

import java.util.Collection;

public interface SubscriptionRepository {

    void add(Subscription subscription);

    void remove(Subscription subscription);

    void clear();

    Boolean isEmpty();

    Collection<Subscription> get();
}
