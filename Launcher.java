import javax.swing.SwingUtilities;

public class Launcher {

    public static void main(String[] args) {

        ImageEditModel model = new ImageEditModel("your-image.jpg");

        SwingUtilities.invokeLater(() -> {
            ImageEditView view = new ImageEditView(model);
            view.pack(); 
            view.setVisible(true);
        });
    }
}
