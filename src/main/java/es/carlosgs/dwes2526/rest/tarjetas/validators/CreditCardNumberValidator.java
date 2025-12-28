package es.carlosgs.dwes2526.rest.tarjetas.validators;


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
    if (creditCardField == null) {
      return true; // Permitir valores null, otras anotaciones controlan esto
    }
    boolean valid16Digits = creditCardField.matches("[0-9]+")
        && creditCardField.length() == 16;
    boolean valid16WithSpaces = creditCardField.matches("([0-9]{4} ){3}[0-9]{4}");
    boolean valid16WithDashes = creditCardField.matches("([0-9]{4}-){3}[0-9]{4}");
    return valid16Digits || valid16WithSpaces || valid16WithDashes;
  }

}