package es.carlosgs.dwes2526.tarjetas.validators;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreditCardNumberValidator implements
    ConstraintValidator<CreditCardNumber, String> {

  @Override
  public void initialize(CreditCardNumber creditCardNumber) {
  }

  @Override
  public boolean isValid(String creditCardField,
                         ConstraintValidatorContext context) {
    boolean valid16Digits = creditCardField.matches("[0-9]+")
        && creditCardField.length() == 16;
    boolean valid16WithSpaces = creditCardField.matches("([0-9]{4} ){3}[0-9]{4}");
    boolean valid16WithDashes = creditCardField.matches("([0-9]{4}-){3}[0-9]{4}");
    return creditCardField != null &&
        (valid16Digits || valid16WithSpaces || valid16WithDashes);
  }

}