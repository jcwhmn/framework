package com.yangzhou.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import com.yangzhou.domain.BaseEntityWithID;
import com.yangzhou.mapper.BaseEntityMapper;
import com.yangzhou.service.EntityExtensionManager;
import com.yangzhou.util.StringUtil;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = Unique.UniqueValidator.class)
@Documented
public @interface Unique {
  boolean required() default true;

  String type();

  String field();
  String message() default "数据唯一性验证";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

  public class UniqueValidator implements ConstraintValidator<Unique, String> {
    protected final static EntityExtensionManager    extensionManager = new EntityExtensionManager();
    private final BaseEntityMapper<BaseEntityWithID> mapper;
    private String type;
    private String field;

    public UniqueValidator(BaseEntityMapper<BaseEntityWithID> mapper) {
      this.mapper = mapper;
    }

    @Override
    public void initialize(Unique unique) {
      type = unique.type();
      field = unique.field();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (StringUtil.isEmpty(value)) return true; // 当值为空时，若有非空验证，则非空验证会报错，若无，允许空值，空值无需进行是否存在判断
      return !extensionManager.exist(type, field, value);
    }
  }

}
