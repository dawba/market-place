package org.marketplace.specifications;

import org.marketplace.models.Advertisement;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

public class AdvertisementSpecification implements Specification<Advertisement> {

    private final String key;
    private final String operation;
    private final Object value;

    public AdvertisementSpecification(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    @Override
    public Predicate toPredicate(Root<Advertisement> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (operation.equalsIgnoreCase(":")) {
            if (key.equalsIgnoreCase("categoryId")) {
                return criteriaBuilder.equal(root.get("category").get("id"), value);
            } else if (root.get(key).getJavaType() == String.class) {
                return criteriaBuilder.like(root.get(key), "%" + value + "%");
            } else {
                return criteriaBuilder.equal(root.get(key), value);
            }
        }
        return null;
    }
}