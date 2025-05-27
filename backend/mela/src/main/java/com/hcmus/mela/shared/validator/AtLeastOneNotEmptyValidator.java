package com.hcmus.mela.shared.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class AtLeastOneNotEmptyValidator implements ConstraintValidator<AtLeastOneNotEmpty, Object> {
    private String[] fields;

    @Override
    public void initialize(AtLeastOneNotEmpty constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return false;
        }

        boolean hasValidField = false;

        try {
            for (String fieldName : fields) {
                Field field;
                try {
                    field = object.getClass().getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    continue;
                }

                field.setAccessible(true);
                Object value = field.get(object);

                if (value instanceof String str) {
                    if (str.trim().isEmpty()) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(fieldName + " must not be empty if present")
                                .addConstraintViolation();
                        return false;
                    } else {
                        hasValidField = true;
                    }
                }
            }

            if (!hasValidField) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("At least one of " + String.join(", ", fields) + " must be provided and not empty")
                        .addConstraintViolation();
            }

            return hasValidField;

        } catch (IllegalAccessException e) {
            return false;
        }
    }
}
