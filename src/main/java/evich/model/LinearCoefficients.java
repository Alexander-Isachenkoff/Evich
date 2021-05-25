package evich.model;

import java.io.Serializable;

public class LinearCoefficients implements Serializable
{
    private static final long serialVersionUID = -5291083257406853146L;
    public double a;
    public double b;
    
    public LinearCoefficients(double a, double b) {
        this.a = a;
        this.b = b;
    }
}
