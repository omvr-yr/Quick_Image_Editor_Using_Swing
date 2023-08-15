import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Graphics2D;

public class ImageEditView extends JFrame {

    private JButton cutButton;
    private JButton undoButton;
    private JButton redoButton;

    private ImagePane imagePane;
    private ImageEditModel model;

    public ImageEditView(ImageEditModel model) {
        this.model = model;

        setTitle("Image Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cutButton = new JButton("Cut");
        undoButton = new JButton("Undo");
        redoButton = new JButton("Redo");

        cutButton.setEnabled(false);
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);

        JMenuBar menuBar = new JMenuBar();
        JMenu editMenu = new JMenu("Edit");

        JMenuItem cutMenuItem = new JMenuItem("Cut");
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        JMenuItem redoMenuItem = new JMenuItem("Redo");

        editMenu.add(cutMenuItem);
        editMenu.add(undoMenuItem);
        editMenu.add(redoMenuItem);

        menuBar.add(editMenu);
        setJMenuBar(menuBar);

        imagePane = new ImagePane();
        setContentPane(imagePane);

        Dimension imageSize = new Dimension(model.getImage().getWidth(), model.getImage().getHeight());
        setSize(imageSize);

        cutButton.addActionListener((ActionEvent e) -> {
            Rectangle selectionRect = imagePane.selection.getRectangle();
            model.saveCut(selectionRect);
            imagePane.repaint();
            undoButton.setEnabled(true);
            redoButton.setEnabled(false);
            cutButton.setEnabled(false);
        });

        undoButton.addActionListener((ActionEvent e) -> {
            if (model.canUndo()) {
                model.performUndo();
                imagePane.repaint();
            }
        });

        redoButton.addActionListener((ActionEvent e) -> {
            if (model.canRedo()) {
                model.performRedo();
                imagePane.repaint();
            }
        });
    }

    private class ImagePane extends JPanel {
        private Selection selection = new Selection();

        public ImagePane() {
            Dimension imageSize = new Dimension(model.getImage().getWidth(), model.getImage().getHeight());
            setPreferredSize(imageSize);
            addMouseListener(selection);
            addMouseMotionListener(selection);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(model.getImage(), 0, 0, this);
            ((Graphics2D) g).draw(selection.getRectangle());
        }
    }

    private class Selection extends MouseAdapter {
        private int x1, y1, x2, y2;

        public Rectangle getRectangle() {
            int x = Math.min(x1, x2);
            int y = Math.min(y1, y2);
            int width = Math.abs(x2 - x1);
            int height = Math.abs(y2 - y1);
            return new Rectangle(x, y, width, height);
        }

        @Override
        public void mousePressed(MouseEvent event) {
            x1 = event.getX();
            y1 = event.getY();
            cutButton.setEnabled(false);
            imagePane.repaint();
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            x2 = event.getX();
            y2 = event.getY();
            cutButton.setEnabled(true);
            imagePane.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent event) {
            // nothing
        }
    }
}