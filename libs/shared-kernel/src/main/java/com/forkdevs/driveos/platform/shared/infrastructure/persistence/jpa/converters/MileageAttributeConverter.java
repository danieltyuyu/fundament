package com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.converters;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Mileage;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Attribute Converter for the Mileage value object.
 * Converts Mileage to Integer for database storage and back to Mileage for entity attributes.
 * This converter is automatically applied to all Mileage attributes in JPA entities.
 * @author Joel Huamani Estefanero
 */
@Converter(autoApply = true)
public class MileageAttributeConverter implements AttributeConverter<Mileage, Integer> {

    /**
     * Converts a Mileage value object to its Integer representation for database storage.
     * @param attribute  the entity attribute value to be converted
     * @return the Integer representation of the Mileage value object, or null if the attribute is null
     */
    @Override
    public Integer convertToDatabaseColumn(Mileage attribute) {
        return (attribute == null) ? null : attribute.value();
    }

    /**
     * Converts an Integer from the database column back to a Mileage value object for entity attributes.
     * @param dbData  the data from the database column to be converted
     * @return a Mileage value object created from the Integer database data, or null if the dbData is null
     */
    @Override
    public Mileage convertToEntityAttribute(Integer dbData) {
        return (dbData == null) ? null : new Mileage(dbData);
    }
}
