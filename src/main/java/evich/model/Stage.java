package evich.model;

public enum Stage
{
    y0("Истинные ряды (Y)"),
    originalTrends("Истинные тренды"),
    y0withTrends("Y с трендами"),
    originalSteps("Истинные скачки"),
    y0withSteps("Y со скачками"),
    z("Разностные ряды (Z)"),
    originalNoise("Истинные выбросы"),
    noise_filtered("Z - выбросы отфильтрованы"),
    found_steps("Найденные скачки"),
    Z_no_lin("Z - скачки отфильтрованы"),
    YnLin("Y - скачки отфильтрованы"),
    y1lin("Найденные линейные тренды"),
    y2lin("Найденные квадратичные тренды"),
    Yres_lin("Y - тренды отфильтрованы");
    
    private String name;
    
    Stage(String name) {
        this.name = name;
    }
    
    Stage() {
        this.name = this.name();
    }
    
    public String getName() {
        return name;
    }
}
