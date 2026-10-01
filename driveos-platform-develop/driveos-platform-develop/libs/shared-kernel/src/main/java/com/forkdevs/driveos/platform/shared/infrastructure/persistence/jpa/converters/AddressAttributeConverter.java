package com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.converters;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Address;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA AttributeConverter to convert between Address value object and its String representation in the database.
 * This converter allows JPA to persist Address objects as Strings in the database and to convert them
 * @author Joel Huamani Estefanero
 */
@Converter(autoApply = true)
public class AddressAttributeConverter implements AttributeConverter<Address, String> {
    /**
     * Converts an Address value object to its String representation for database storage. If the attribute is null, it returns null; otherwise, it returns the string value of the Address.
     * @param attribute  the entity attribute value to be converted
     * @return the converted database column value, which is a String representation of the Address, or null if the attribute is null
     */
    @Override
    public String convertToDatabaseColumn(Address attribute) {
        return (attribute == null) ? null : attribute.value();
    }

    /**
     * Converts a String from the database column back to an Address value object. If the database value is null or blank, it returns null; otherwise, it creates a new Address object using the string value.
     * @param dbData  the data from the database column to be converted
     * @return the converted entity attribute, which is an Address object created from the database string value, or null if the database value is null or blank
     */
    @Override
    public Address convertToEntityAttribute(String dbData) {
        return (dbData == null || dbData.isBlank()) ? null : new Address(dbData);
    }
}