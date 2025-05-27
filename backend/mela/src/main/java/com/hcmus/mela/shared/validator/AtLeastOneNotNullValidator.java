package com.hcmus.mela.shared.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull, Object> {
    private String[] fields;

    @Override
    public void initialize(AtLeastOneNotNull annotation) {
        this.fields = annotation.fields();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return false;
        }

        try {
            for (String fieldName : fields) {
                Field field = object.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(object);
                if (value != null) {
                    return true;
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("At least one field must not be null")
                .addConstraintViolation();

        return false;
    }

}
