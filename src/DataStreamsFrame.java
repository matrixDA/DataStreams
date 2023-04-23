import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsFrame extends JFrame
{
    JPanel mainPnl, westPnl, eastPnl, northPnl;

    JTextArea loadedFileTA, searchFileTA;

    JScrollPane loadedFilePane, searchFilePane;

    JButton selectFileBtn, searchFileBtn, quitBtn, clearBtn;

    JTextField textField;
    JLabel label;
    StringBuffer stringBuffer;
    List<String> list = new LinkedList<>(Arrays.asList());
    Stream<String> stream;
    public DataStreamsFrame()
    {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeigh = screenSize.height;
        int screenWidth = screenSize.width;

        setSize(1230,615);
        setLocation(screenWidth / 12, (screenHeigh - 615) / 2);

        setTitle("Data Stream Processing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createGUI();
        setVisible(true);
    }

    public void createGUI()
    {
        mainPnl = new JPanel();
        westPnl = new JPanel();
        eastPnl = new JPanel();
        northPnl = new JPanel();

        mainPnl.setLayout(new BorderLayout());
        mainPnl.add(westPnl, BorderLayout.WEST);
        mainPnl.add(eastPnl, BorderLayout.EAST);
        mainPnl.add(northPnl, BorderLayout.NORTH);

        add(mainPnl);

        createNorthPnl();
        createWestPnl();
        createEastPnl();
    }

    public void createWestPnl() {

        westPnl.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1), "Original File"));

        loadedFileTA = new JTextArea(30,45);
        loadedFilePane = new JScrollPane(loadedFileTA);
        loadedFileTA.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));


        westPnl.add(loadedFilePane); loadedFileTA.setEditable(false);
    }
    public void createEastPnl() {

        eastPnl.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 1), "Filtered File"));

        searchFileTA = new JTextArea(30,45);
        searchFileTA.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        searchFilePane = new JScrollPane(searchFileTA);


        eastPnl.add(searchFilePane); searchFileTA.setEditable(false);
    }

    public void createNorthPnl()
    {
        quitBtn = new JButton("Quit");
        quitBtn.addActionListener((ActionEvent ae) -> System.exit(0));

        selectFileBtn = new JButton("Select File");
        selectFileBtn.addActionListener((ActionEvent ae) -> selectFile());

        label = new JLabel("Search File: ", JLabel.RIGHT);
        textField = new JTextField(8);
        searchFileBtn = new JButton("Search");
        searchFileBtn.addActionListener((ActionEvent ae) -> searchSelectedFile());

        clearBtn = new JButton("Clear");
        clearBtn.addActionListener((ActionEvent ae) -> clearFilter());

        northPnl.add(selectFileBtn);
        northPnl.add(label);
        northPnl.add(textField); textField.setEnabled(false);
        northPnl.add(searchFileBtn); searchFileBtn.setEnabled(false);
        northPnl.add(clearBtn);
        northPnl.add(quitBtn);
    }

    private void clearFilter() {
        loadedFileTA.setText("");
        searchFileTA.setText("");
        textField.setText("");

        selectFileBtn.setEnabled(true);
        searchFileBtn.setEnabled(false);


    }

    public void selectFile() {
        JFileChooser chooser = new JFileChooser();
        File selectedFile;

        try
        {
            File workingDirectory = new File(System.getProperty("user.dir"));
            chooser.setCurrentDirectory(workingDirectory);


            if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                selectedFile = chooser.getSelectedFile();
                loadedFileTA.setText(selectedFile.getName() + "\n\n");

                Stream lines = Files.lines(Paths.get(selectedFile.toURI()));
                list = lines.toList();
                list.forEach(word -> loadedFileTA.append(word.concat("\n")));
            }
        }
        catch (FileNotFoundException e)
        {
            JOptionPane.showMessageDialog(null, "File not found");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        selectFileBtn.setEnabled(false);
        searchFileBtn.setEnabled(true);
        textField.setEnabled(true);


    }
    private void searchSelectedFile() {
        if (loadedFileTA.getText().isBlank())
        {
            JOptionPane.showMessageDialog(null, "You failed to select a file", "Error Message", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            String res = textField.getText().toLowerCase();

            if (res.isBlank())
            {
                JOptionPane.showMessageDialog(null, "Text Field is empty", "Error Message", JOptionPane.ERROR_MESSAGE);
                searchFileBtn.setEnabled(true);
            }
            else
            {
                stream = list.stream().filter(word -> word.toLowerCase().contains(res));
                stream.forEach(word -> searchFileTA.append(word.concat("\n")));

                searchFileBtn.setEnabled(false);
                textField.setEnabled(false);
            }
        }
    }
}
