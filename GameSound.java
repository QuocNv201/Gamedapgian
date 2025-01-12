// Source code is decompiled from a .class file using FernFlower decompiler.
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GameSound {
    private Clip clip;

    public GameSound() {
    }

    public void playBackgroundMusic(String var1) {
        try {
            File var2 = new File(var1);
            AudioInputStream var3 = AudioSystem.getAudioInputStream(var2);
            this.clip = AudioSystem.getClip();
            this.clip.open(var3);
            this.clip.loop(-1);
            this.clip.start();
        } catch (Exception var4) {
            System.err.println("L\u1ed7i ph\u00e1t nh\u1ea1c n\u1ec1n: " + var4.getMessage());
        }

    }

    public void stopBackgroundMusic() {
        if (this.clip != null && this.clip.isRunning()) {
            this.clip.stop();
        }

    }
    public void playSoundEffect(String path) {
        try {
            // Sử dụng dấu "/" hoặc "\\" để tránh lỗi trong đường dẫn
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                System.err.println("Tệp âm thanh không tồn tại: " + path);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip soundClip = AudioSystem.getClip();
            soundClip.open(audioStream);
            soundClip.start();
        } catch (Exception e) {
            System.err.println("Lỗi phát hiệu ứng âm thanh: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void playSoundend(String path) {
        try {
            // Sử dụng dấu "/" hoặc "\\" để tránh lỗi trong đường dẫn
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                System.err.println("Tệp âm thanh không tồn tại: " + path);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip soundClip = AudioSystem.getClip();
            soundClip.open(audioStream);
            soundClip.start();
        } catch (Exception e) {
            System.err.println("Lỗi phát hiệu ứng âm thanh: " + e.getMessage());
            e.printStackTrace();
        }
    }


}