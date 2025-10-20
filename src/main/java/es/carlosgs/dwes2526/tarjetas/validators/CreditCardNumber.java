package es.carlosgs.dwes2526.tarjetas.validators;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CreditCardNumberValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CreditCardNumber {
  String message() default "El número de tarjeta no es válido";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
