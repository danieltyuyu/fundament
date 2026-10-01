package com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.converters;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigDecimal;

/**
 * JPA Attribute Converter for the Money value object. This converter allows JPA to persist Money objects as BigDecimal values in the database and convert them back to Money objects when retrieving data.
 * The @Converter annotation with autoApply=true indicates that this converter should be applied automatically to all
 * attributes of type Money in JPA entities without needing to specify the converter explicitly on each attribute.
 * @author Joel Huamani Estefanero
 */
@Converter(autoApply = true)
public class MoneyAttributeConverter implements AttributeConverter<Money, BigDecimal> {

    /**
     * Converts a Money object to its database column representation (BigDecimal). If the attribute is null, it returns null; otherwise, it returns the amount as a BigDecimal.
     * @param attribute  the entity attribute value to be converted
     * @return the converted database column value (BigDecimal) or null if the attribute is null
     */
    @Override
    public BigDecimal convertToDatabaseColumn(Money attribute) {
        return (attribute == null) ? null : attribute.amount();
    }

    /**
     * Converts a BigDecimal from the database column back to a Money object. If the database value is null, it returns null; otherwise, it creates a new Money object using the BigDecimal value.
     * @param dbData  the data from the database column to be converted
     * @return the converted entity attribute (Money) or null if the database value is null
     */
    @Override
    public Money convertToEntityAttribute(BigDecimal dbData) {
        return (dbData == null) ? null : new Money(dbData);
    }
}
