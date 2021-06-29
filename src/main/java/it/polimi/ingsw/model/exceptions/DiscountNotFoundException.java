package it.polimi.ingsw.model.exceptions;

public class DiscountNotFoundException extends Throwable {
    public DiscountNotFoundException() { super(); }
    @Override
    public String getMessage() {
        return "Discount not found";
    }
}
