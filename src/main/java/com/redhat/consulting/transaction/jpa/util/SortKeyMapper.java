package com.redhat.consulting.transaction.jpa.util;

import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.metadata.ClassMappingMetadata;
import com.github.dozermapper.core.metadata.MappingMetadata;
import com.github.dozermapper.core.metadata.MetadataLookupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Support class for mapping from Springs {@link Sort} class into a {@link List}&lt;{@link Order}&gt; for use with
 * criteria queries.
 * <p>
 * This relies upon a dozer mapping defined for the source DTO and the target entity.
 */
@Component
public class SortKeyMapper {

    private final MappingMetadata mappingMetadata;

    @Autowired
    public SortKeyMapper(Mapper mapper) {
        this.mappingMetadata = mapper.getMappingMetadata();
    }

    public List<Order> performMap(Class<?> source, From<?, ?> queryPath, CriteriaBuilder cb, Sort input) {
        if (input == null || input.isUnsorted()) {
            return Collections.emptyList();
        }

        Function<String, String> finalMapPropertyName = getMapPropertyNameFunction(source, queryPath.getJavaType());

        return input
                .stream()
                .map(sortOrder -> sortOrder.withProperty(finalMapPropertyName.apply(sortOrder.getProperty())))
                .flatMap(sortOrder -> {
                    try {
                        return QueryUtils.toOrders(Sort.by(sortOrder), queryPath, cb).stream();
                    } catch (PropertyReferenceException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    private Function<String, String> getMapPropertyNameFunction(Class<?> source, Class<?> target) {
        try {
            ClassMappingMetadata classMetadata = mappingMetadata.getClassMapping(source, target);

            // Return source->destination map
            return inputName -> {
                try {
                    return classMetadata.getFieldMappingBySource(inputName).getDestinationName();
                } catch (MetadataLookupException e) {
                    return inputName;
                }
            };
        } catch (MetadataLookupException e1) {
            try {
                ClassMappingMetadata classMetadata = mappingMetadata.getClassMapping(target, source);

                // Return destination->source (reversed) map
                return inputName -> {
                    try {
                        return classMetadata.getFieldMappingByDestination(inputName).getSourceName();
                    } catch (MetadataLookupException e) {
                        return inputName;
                    }
                };
            } catch (MetadataLookupException e2) {
                throw new IllegalStateException(
                        "Trying to map sort keys on objects that are not mapped: " +
                                source.getName() + " to " + target.getName(),
                        e2);
            }
        }
    }

}
