package evich.model;

import javafx.scene.paint.Color;

import java.io.Serializable;

public enum Stage implements Serializable
{
    y0("Истинные ряды (Y)", Color.ROYALBLUE),
    originalTrends("Истинные тренды", Color.GREEN),
    y0withTrends("Y с трендами", Color.DARKGREEN),
    originalSteps("Истинные скачки", Color.YELLOW),
    y0withSteps("Y со скачками", Color.GREY),
    z("Разностные ряды (Z)", Color.PURPLE),
    originalNoise("Истинные выбросы", Color.BLACK),
    noise_filtered("Z - выбросы отфильтрованы", Color.ORANGERED),
    found_steps("Найденные скачки", Color.YELLOW.darker()),
    Z_no_lin("Z - скачки отфильтрованы", Color.MEDIUMVIOLETRED),
    YnLin("Y - скачки отфильтрованы", Color.DARKRED),
    y1lin("Найденные линейные тренды", Color.GREENYELLOW),
    y2lin("Найденные квадратичные тренды", Color.GREENYELLOW),
    Yres_lin("Y - тренды отфильтрованы", Color.RED);
    
    private static final long serialVersionUID = -8622516432957845223L;
    private String name;
    private Color color;
    
    Stage(String name) {
        this(name, Color.ROYALBLUE);
    }
    
    Stage(String name, Color color) {
        this.name = name;
        this.color = color;
    }
    
    Stage() {
        this.name = this.name();
    }
    
    public String getName() {
        return name;
    }
    
    public Color getColor() {
        return color;
    }
}
