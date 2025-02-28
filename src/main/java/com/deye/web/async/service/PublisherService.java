package com.deye.web.async.service;

import java.util.Set;
import java.util.UUID;

public interface PublisherService {
    void onProductsDeleted(Set<UUID> productId);
}
