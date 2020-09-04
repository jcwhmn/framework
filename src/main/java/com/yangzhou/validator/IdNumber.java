package com.yangzhou.validator;

import io.netty.util.internal.StringUtil;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({FIELD, METHOD})
@Constraint(validatedBy = IdNumber.IdNumberCheck.class)
public @interface IdNumber {
    boolean required() default false;

    String message() default "身份证号格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public static class IdNumberCheck implements ConstraintValidator<IdNumber, String> {
        private boolean required = false;

        @Override
        public void initialize(IdNumber idNumber) {
            required = idNumber.required();
        }

        private Pattern pattern = Pattern.compile("^[1-9]\\d{5}(18|19|20|(3\\d))\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (StringUtil.isNullOrEmpty(value)) {
                if (!required)
                    return true;
                else
                    return false;
            }
            return pattern.matcher(value).matches();
        }
    }
}
