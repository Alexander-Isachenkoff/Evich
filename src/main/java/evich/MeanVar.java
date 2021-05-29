package evich;

import java.io.Serializable;

public class MeanVar implements Serializable
{
    private static final long serialVersionUID = 3188415124250224010L;
    double beforeM;
    double beforeD;
    double afterM;
    double afterD;
    
    public MeanVar(double beforeM, double beforeD, double afterM, double afterD) {
        this.beforeM = beforeM;
        this.beforeD = beforeD;
        this.afterM = afterM;
        this.afterD = afterD;
    }
}
