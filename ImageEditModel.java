import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class ImageEditModel {
    private BufferedImage image;
    private UndoManager undoManager = new UndoManager();

    public ImageEditModel(String chemin) {
        try {
            image = ImageIO.read(new File(chemin));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fillZone(Rectangle z, int[][] pixels) {
        int xStart = (int) z.getX();
        int yStart = (int) z.getY();
        int width = (int) z.getWidth();
        int height = (int) z.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = pixels[y][x];
                image.setRGB(xStart + x, yStart + y, pixelValue);
            }
        }
    }

    public void clearZone(Rectangle z) {
        Color color = Color.WHITE;
        int srgb = color.getRGB();

        int width = (int) z.getWidth();
        int height = (int) z.getHeight();
        int[][] pixels = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = srgb;
            }
        }

        fillZone(z, pixels);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void updateImage(BufferedImage newImage) {
        image = newImage;
    }

    public void saveCut(Rectangle z) {
        BufferedImage subImage = image.getSubimage((int) z.getX(), (int) z.getY(), (int) z.getWidth(), (int) z.getHeight());
        Coupe c = new Coupe((int) z.getX(), (int) z.getY(), (int) z.getWidth(), (int) z.getHeight(), subImage);
        c.doit();

        UndoableEdit cutEdit = new CutEdit(c);
        undoManager.addEdit(cutEdit);
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

    public void performUndo() {
        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (CannotUndoException ex) {
            ex.printStackTrace();
        }
    }

    public void performRedo() {
        try {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (CannotRedoException ex) {
            ex.printStackTrace();
        }
    }

    private class Coupe {
        private Rectangle z;
        private int[][] pixels;

        public Coupe(int x, int y, int width, int height, BufferedImage image) {
            z = new Rectangle(x, y, width, height);
            pixels = new int[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    pixels[i][j] = image.getRGB(x + j, y + i);
                }
            }
        }

        public void doit() {
            clearZone(z);
        }

        public void undo() {
            fillZone(z, pixels);
        }
    }

    private class CutEdit extends AbstractUndoableEdit {
        private Coupe c;

        public CutEdit(Coupe coupe) {
            c = coupe;
        }

        @Override
        public void undo() throws CannotUndoException {
            super.undo();
            c.undo();
        }

        @Override
        public void redo() throws CannotRedoException {
            super.redo();
            c.doit();
        }
    }
}
