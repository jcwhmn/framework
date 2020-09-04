package com.yangzhou.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import io.netty.util.internal.StringUtil;

@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
@Constraint(validatedBy = Phone.PhoneCheck.class)
public @interface Phone {
	boolean required() default false; 
	String message() default "手机号码格式不正确";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

	public static class PhoneCheck implements ConstraintValidator<Phone, String>{
		private boolean required = false;

		@Override
		public void initialize(Phone phone) {
			required = phone.required();
		}
		private Pattern pattern = Pattern.compile("^[1][3,4,5,7,8,9][0-9]{9}$");
		@Override
		public boolean isValid(String value, ConstraintValidatorContext context) {
			if(StringUtil.isNullOrEmpty(value)) {
				if(!required)
					return true;
				else
					return false;
			}
			return pattern.matcher(value).matches();
		}
	}
}
