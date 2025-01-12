
import java.awt.*;

public class Gian {
    private Rectangle rectangle;
    private boolean isSmashed; // Trạng thái bị đập
    private long smashedTime;  // Thời gian bị đập
    public long getSmashedTime() {
        return smashedTime;
    }    

    public Gian(int x, int y) {
        this.rectangle = new Rectangle(x, y, 80, 100); // Gián có kích thước 80x100
        this.isSmashed = false;
        this.smashedTime = 0;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void move(int speed) {
        rectangle.y += speed;
    }

    public boolean isOutOfBounds(int screenHeight) {
        return rectangle.y > screenHeight;
    }

    public boolean contains(Point point) {
        return rectangle.contains(point);
    }

    public void smash() {
        isSmashed = true;
        smashedTime = System.currentTimeMillis();
    }

    public boolean isSmashed() {
        return isSmashed;
    }

    public boolean shouldRemove() {
        return isSmashed && (System.currentTimeMillis() - smashedTime > 500);
    }
}