import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;

class Main extends JFrame{
    static String [][]Questions = new String[10][5];
    static String []Answers = new String[10];
    {
        try{
            BufferedReader reader = new BufferedReader(new FileReader("questions.txt"));
            String row = reader.readLine();
            int q=0;
            int o=0;
            while (row != null){
                if (o < 5){
                    Questions[q][o]= row;
                    o+=1;
                    row = reader.readLine();
                }
                else{
                    o=0;
                    q+=1;
                }
            }
            reader.close();
        }
        catch(Exception e){}
    }
    
    JPanel bg;
    JPanel panel_bar;
    JPanel Question_panel;
    JPanel Btn_panel;
    JPanel btn_bar;

    CardLayout cl;

    JDialog profile;

    JLabel timer_label;
    JLabel btn_info;

    JButton exit_btn;
    JButton logout_btn;
    JButton profile_btn;
    JButton submit_btn;
    JButton prev_btn;
    JButton next_btn;
    JButton[] buttons = new JButton[10];

    JPanel[] card = new JPanel[10];
    JTextArea Question;
    JRadioButton btn1;
    JRadioButton btn2;
    JRadioButton btn3;
    JRadioButton btn4;
    ButtonGroup Btn_group;
    static boolean btn_color;
    static int currentCard=1;

    static Timer stopwatch;
    static int time_in_sec = 70;
    static int hours;
    static int minutes;
    static int seconds;
    static String timer_format;
    static String starttimer(int sec){
        if (time_in_sec >= 3600){
            hours = time_in_sec/3600;
            if ((time_in_sec % 3600)>=60){
                minutes = (time_in_sec % 3600)/60;
                seconds = (time_in_sec % 3600) % 60;
            }
            else{
                seconds = time_in_sec % 3600;
            }
        }
        else if (time_in_sec >=60){
            hours = 0;
            minutes = time_in_sec/60;
            seconds = time_in_sec % 60;
        }
        else{
            hours = 0;
            minutes = 0;
            seconds = time_in_sec;
        }
        timer_format = String.format("%02d",hours)+":"+String.format("%02d",minutes)+":"+String.format("%02d",seconds);
        return timer_format;
    }

    void SubmitAll(){
        Path filepath = Paths.get("answers.txt");
        try(BufferedWriter answer_file = Files.newBufferedWriter(filepath,StandardOpenOption.TRUNCATE_EXISTING)){
            for (int a=0;a<10;a++){
                answer_file.write("["+String.format("%02d",a+1)+"] "+Answers[a]+"\n");
            }
            answer_file.close();
            stopwatch.cancel();

        }
        catch(Exception ex){}

    }

    void MainWindow(){
        int Width = ((Toolkit.getDefaultToolkit()).getScreenSize()).width;
        int Height = ((Toolkit.getDefaultToolkit()).getScreenSize()).height;

        bg = new JPanel(new GridBagLayout());
        bg.setPreferredSize(new Dimension(Width,Height));
        bg.setBackground(Color.WHITE);
        add(bg);
        GridBagConstraints c= new GridBagConstraints();
        c.anchor= GridBagConstraints.NORTH;
        c.fill=GridBagConstraints.HORIZONTAL;

        panel_bar = new JPanel(new GridBagLayout());
        panel_bar.setPreferredSize(new Dimension(Width,60));
        panel_bar.setBackground(new Color(28, 80, 200));
        GridBagConstraints c1= new GridBagConstraints();
        c1.anchor= GridBagConstraints.WEST;

        exit_btn=new JButton("EXIT");
        exit_btn.setBackground(new Color(28, 80, 200));
        exit_btn.setForeground(Color.WHITE);
        exit_btn.setFont(new Font("Arial", Font.BOLD, 20));
        exit_btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exit_btn.setContentAreaFilled(false);
        exit_btn.setBorderPainted(false);
        exit_btn.setFocusPainted(false);
        exit_btn.addActionListener(e -> System.exit(0));
        c1.gridx=0;
        c1.gridy=0;
        panel_bar.add(exit_btn,c1);

        timer_label = new JLabel("00:00:00",JLabel.CENTER);
        timer_label.setPreferredSize(new Dimension(1500,25));
        timer_label.setBackground(new Color(28, 80, 200));
        timer_label.setForeground(Color.WHITE);
        timer_label.setFont(new Font("Arial", Font.BOLD, 25));
        c1.gridx=1;
        c1.gridy=0;
        panel_bar.add(timer_label,c1);

        stopwatch = new Timer();
        stopwatch.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (time_in_sec > 0){
                    if (time_in_sec < 60){
                        timer_label.setForeground(Color.RED);
                    }
                    timer_label.setText(starttimer(time_in_sec));
                    time_in_sec--;
                }
                else{
                    timer_label.setText(starttimer(time_in_sec));
                    JOptionPane.showMessageDialog(null, "Time Out ! \nAnswers Submitted Successfully!!","SUCCESS",JOptionPane.INFORMATION_MESSAGE);
                    SubmitAll();
                    System.exit(0);
                }
                
            }
        }, 1000, 1000);

        logout_btn=new JButton("LOG OUT");
        logout_btn.setBackground(new Color(28, 80, 200));
        logout_btn.setForeground(Color.WHITE);
        logout_btn.setFont(new Font("Arial", Font.BOLD, 20));
        logout_btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout_btn.setContentAreaFilled(false);
        logout_btn.setBorderPainted(false);
        logout_btn.setFocusPainted(false);
        logout_btn.addActionListener(e -> {this.dispose(); new OnlineExamination();});
        c1.gridx=2;
        c1.gridy=0;
        panel_bar.add(logout_btn,c1);

        profile_btn=new JButton("PROFILE");
        profile_btn.setBackground(new Color(28, 80, 200));
        profile_btn.setForeground(Color.WHITE);
        profile_btn.setFont(new Font("Arial", Font.BOLD, 20));
        profile_btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profile_btn.setContentAreaFilled(false);
        profile_btn.setBorderPainted(false);
        profile_btn.setFocusPainted(false);
        profile_btn.addActionListener(e -> {Profile(OnlineExamination.user,OnlineExamination.check);});
        c1.gridx=3;
        c1.gridy=0;
        panel_bar.add(profile_btn,c1);

        Question_panel =new JPanel();
        cl = new CardLayout();
        Question_panel.setLayout(cl);
        Question_panel.setPreferredSize(new Dimension(Width-500,Height-120));
        Question_panel.setBackground(Color.LIGHT_GRAY);
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                String ans = ((JRadioButton)e.getSource()).getText();
                Answers[currentCard-1] = ans;
                btn_color = true;
            }
        };

        GridBagConstraints cardgrid = new GridBagConstraints();
        cardgrid.anchor = GridBagConstraints.NORTHWEST;

        for (int i=0;i<10;i++){
            card[i] = new JPanel();
            card[i].setLayout(new GridBagLayout());
            card[i].setBackground(Color.LIGHT_GRAY);

            Question = new JTextArea();
            Question.setPreferredSize(new Dimension(Width-500,100));
            Question.setFont(new Font("Arial", Font.PLAIN, 25));
            Question.append("[Q] "+String.format("%02d",(i+1))+". "+Questions[i][0]);
            Question.setBorder(BorderFactory.createEmptyBorder(0,20,0,20));
            Question.setWrapStyleWord(true);
            Question.setLineWrap(true);
            Question.setEditable(false);
            Question.setBackground(Color.LIGHT_GRAY);

            btn1 = new JRadioButton(Questions[i][1]);
            btn1.setFont(new Font("Arial", Font.PLAIN, 20));
            btn1.setBackground(Color.LIGHT_GRAY);
            btn1.addActionListener(al);
            btn2 = new JRadioButton(Questions[i][2]);
            btn2.setFont(new Font("Arial", Font.PLAIN, 20));
            btn2.setBackground(Color.LIGHT_GRAY);
            btn2.addActionListener(al);
            btn3 = new JRadioButton(Questions[i][3]);
            btn3.setFont(new Font("Arial", Font.PLAIN, 20));
            btn3.setBackground(Color.LIGHT_GRAY);
            btn3.addActionListener(al);
            btn4 = new JRadioButton(Questions[i][4]);
            btn4.setFont(new Font("Arial", Font.PLAIN, 20));
            btn4.setBackground(Color.LIGHT_GRAY);
            btn4.addActionListener(al);
            
            Btn_group = new ButtonGroup();
            Btn_group.add(btn1);
            Btn_group.add(btn2);
            Btn_group.add(btn3);
            Btn_group.add(btn4);

            cardgrid.gridx=0;
            cardgrid.gridy=0;
            cardgrid.gridwidth=1;
            cardgrid.weightx=1;
            cardgrid.weighty=0.01;
            cardgrid.insets = new Insets(20,0,10,0);
            card[i].add(Question,cardgrid);
            
            cardgrid.gridx=0;
            cardgrid.gridy=1;
            cardgrid.gridwidth=1;
            cardgrid.insets = new Insets(0,50,0,0);
            cardgrid.weightx=1;
            cardgrid.weighty=0.02;
            card[i].add(btn1,cardgrid);
            
            cardgrid.gridx=0;
            cardgrid.gridy=2;
            cardgrid.gridwidth=1;
            cardgrid.insets = new Insets(0,50,0,0);
            cardgrid.weightx=1;
            cardgrid.weighty=0.02;
            card[i].add(btn2,cardgrid);
            
            cardgrid.gridx=0;
            cardgrid.gridy=3;
            cardgrid.gridwidth=1;
            cardgrid.insets = new Insets(0,50,0,0);
            cardgrid.weightx=1;
            cardgrid.weighty=0.02;
            card[i].add(btn3,cardgrid);
            
            cardgrid.gridx=0;
            cardgrid.gridy=4;
            cardgrid.gridwidth=1;
            cardgrid.insets = new Insets(0,50,0,0);
            cardgrid.weightx=1;
            cardgrid.weighty=1;
            card[i].add(btn4,cardgrid);

            Question_panel.add(String.valueOf(i+1),card[i]);
        }

        btn_bar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btn_bar.setPreferredSize(new Dimension(Width-500,60));
        btn_bar.setBackground(Color.LIGHT_GRAY);

        prev_btn = new JButton("PREVIOUS");
        prev_btn.setForeground(Color.LIGHT_GRAY);
        prev_btn.setBackground(Color.LIGHT_GRAY);
        prev_btn.setFont(new Font("Arial", Font.BOLD, 15));
        prev_btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        prev_btn.setContentAreaFilled(false);
        prev_btn.setFocusPainted(false);
        prev_btn.setBorderPainted(false);
        prev_btn.addActionListener(e -> {
            if (currentCard == 1){
                prev_btn.setForeground(Color.LIGHT_GRAY);
            }
            else{
                next_btn.setForeground(Color.BLUE);
                next_btn.setText("SAVE & NEXT");
                cl.previous(Question_panel);
                currentCard-=1;
                if (currentCard == 1){
                    prev_btn.setForeground(Color.LIGHT_GRAY);
                }
            }
        });
        btn_bar.add(prev_btn);

        next_btn = new JButton("SAVE & NEXT");
        next_btn.setForeground(Color.BLUE);
        next_btn.setBackground(Color.LIGHT_GRAY);
        next_btn.setFont(new Font("Arial", Font.BOLD, 15));
        next_btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        next_btn.setBorder(BorderFactory.createEmptyBorder(0,10,0,70));
        next_btn.setContentAreaFilled(false);
        next_btn.setFocusPainted(false);
        next_btn.setBorderPainted(false);
        next_btn.addActionListener(e -> {
            if (currentCard == 10){
                prev_btn.setForeground(Color.BLUE);
                next_btn.setText("SAVE");
                if (btn_color){
                    buttons[currentCard-1].setBackground(Color.GREEN);
                    btn_color = false;
                }
                else{
                    buttons[currentCard-1].setBackground(Color.ORANGE);
                }
            }
            else{
                prev_btn.setForeground(Color.BLUE);
                next_btn.setForeground(Color.BLUE);
                if (btn_color){
                    buttons[currentCard-1].setBackground(Color.GREEN);
                    btn_color = false;
                }
                else{
                    buttons[currentCard-1].setBackground(Color.ORANGE);
                }
                currentCard+=1;
                if (currentCard == 10){
                    prev_btn.setForeground(Color.BLUE);
                    next_btn.setText("SAVE");
                }
                cl.next(Question_panel);
                
            }
        });
        btn_bar.add(next_btn);


        Btn_panel = new JPanel(new GridBagLayout());
        Btn_panel.setBackground(Color.WHITE);
        Btn_panel.setBorder(BorderFactory.createEmptyBorder(0,10,0, 10));
        GridBagConstraints gc= new GridBagConstraints();
        gc.anchor= GridBagConstraints.NORTH;
        gc.insets= new Insets(5,5,5,5);

        ActionListener btn_card = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                currentCard = Integer.parseInt(e.getActionCommand());
                if (currentCard == 1){
                    prev_btn.setForeground(Color.LIGHT_GRAY);
                    next_btn.setForeground(Color.BLUE);
                    next_btn.setText("SAVE & NEXT");
                }
                else if (currentCard == 10){
                    prev_btn.setForeground(Color.BLUE);
                    next_btn.setForeground(Color.BLUE);
                    next_btn.setText("SAVE");
                }
                else{
                    prev_btn.setForeground(Color.BLUE);
                    next_btn.setForeground(Color.BLUE);
                    next_btn.setText("SAVE & NEXT");
                }
                cl.show(Question_panel,e.getActionCommand());
            }
        };

        for (int i=0;i<2;i++){
            for (int j=0;j<5;j++){
                buttons[(i*5)+j] = new JButton(String.valueOf((i*5)+j+1));
                buttons[(i*5)+j].setPreferredSize(new Dimension(80,80));
                buttons[(i*5)+j].setForeground(Color.WHITE);
                buttons[(i*5)+j].setBackground(Color.BLUE);
                buttons[(i*5)+j].setFont(new Font("Arial", Font.PLAIN, 25));
                buttons[(i*5)+j].setFocusPainted(false);
                buttons[(i*5)+j].addActionListener(btn_card);
                buttons[(i*5)+j].setActionCommand(String.valueOf((i*5)+j+1));
                gc.gridx=j;
                gc.gridy=i+1;
                Btn_panel.add(buttons[(i*5)+j],gc);
            }
        }

        btn_info = new JLabel("QUESTION PANEL",JLabel.CENTER);
        btn_info.setPreferredSize(new Dimension(440,28));
        btn_info.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        btn_info.setOpaque(true);
        btn_info.setBackground(new Color(170, 101, 37));
        btn_info.setForeground(Color.BLACK);
        btn_info.setFont(new Font("Times New Roman", Font.PLAIN, 25));
        gc.gridx=0;
        gc.gridy=0;
        gc.gridwidth=5;
        Btn_panel.add(btn_info,gc);

        submit_btn = new JButton("SUBMIT");
        submit_btn.setBackground(Color.GREEN);
        submit_btn.setForeground(Color.WHITE);
        submit_btn.setFont(new Font("Arial", Font.BOLD, 20));
        submit_btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submit_btn.setFocusPainted(false);
        submit_btn.addActionListener(e -> {
            SubmitAll();
            JOptionPane.showMessageDialog(null, "Submitted Successfully!!","SUCCESS",JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        });

        c.gridx=0;
        c.gridy=0;
        c.gridwidth=2;
        bg.add(panel_bar,c);
        c.gridx=0;
        c.gridy=1;
        c.gridwidth=1;
        bg.add(Question_panel,c);
        c.gridx=1;
        c.gridy=1;
        c.gridwidth=1;
        bg.add(Btn_panel,c);
        c.gridx=0;
        c.gridy=2;
        c.gridwidth=1;
        bg.add(btn_bar,c);
        c.gridx=1;
        c.gridy=2;
        c.gridwidth=1;
        c.insets = new Insets(0,100,0,100);
        bg.add(submit_btn,c);

        setExtendedState(MAXIMIZED_BOTH);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    public void Profile(String uservalue,String passvalue){
        JLabel username;
        JLabel password;
        JLabel newpassword;
        JLabel header;
        JTextField userTextField;
        JPasswordField passwordField;
        JPasswordField newpasswordField;
        JButton update;

        profile = new JDialog(this,"USER PROFILE");
        profile.setLayout(new GridBagLayout());
        GridBagConstraints c= new GridBagConstraints();
        c.anchor= GridBagConstraints.WEST;
        c.fill=GridBagConstraints.HORIZONTAL;

        header = new JLabel("PROFILE",JLabel.CENTER);
        header.setPreferredSize(new Dimension(440,28));
        header.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        header.setOpaque(true);
        header.setBackground(new Color(28, 80, 200));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Times New Roman", Font.PLAIN, 25));
        c.gridx=0;
        c.gridy=0;
        c.gridwidth=2;
        profile.add(header,c);

        username=new JLabel("USENAME:",JLabel.LEFT);
        username.setFont(new Font("Arial", Font.BOLD, 20));
        username.setForeground(Color.BLACK);
        c.gridx=0;
        c.gridy=1;
        c.ipady=10;
        c.gridwidth=1;
        profile.add(username,c);

        userTextField = new JTextField(20);
        userTextField.setText(uservalue);
        userTextField.setFont(new Font("Arial", Font.PLAIN, 20));
        userTextField.setForeground(Color.BLACK);
        c.gridx=1;
        c.gridy=1;
        c.ipady=0;
        c.weighty=0.5;
        profile.add(userTextField,c);

        password=new JLabel("NEW PASSWORD:",JLabel.LEFT);
        c.gridx=0;
        c.gridy=2;
        c.ipady=10;
        password.setFont(new Font("Arial", Font.BOLD, 20));
        password.setForeground(Color.BLACK);
        profile.add(password,c);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setForeground(Color.BLACK);
        passwordField.setText(passvalue);
        passwordField.setEchoChar('*');
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e){
                passwordField.setEchoChar('\0');
                passwordField.setText(String.valueOf(passwordField.getPassword()));
            }
            @Override
            public void focusLost(FocusEvent e){
                passwordField.setEchoChar('*');
                passwordField.setText(String.valueOf(passwordField.getPassword()));
            }

        });
        c.gridx=1;
        c.gridy=2;
        c.ipady=0;
        c.weighty=0.5;
        profile.add(passwordField,c);

        newpassword=new JLabel("RE-TYPE PASSWORD:",JLabel.LEFT);
        newpassword.setFont(new Font("Arial", Font.BOLD, 20));
        newpassword.setForeground(Color.BLACK);
        c.gridx=0;
        c.gridy=3;
        c.ipady=0;
        c.weighty=0.5;
        profile.add(newpassword,c);

        newpasswordField = new JPasswordField(20);
        newpasswordField.setFont(new Font("Arial", Font.PLAIN, 20));
        newpasswordField.setForeground(Color.BLACK);
        newpasswordField.setEchoChar('\0');
        newpasswordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e){
                newpasswordField.setEchoChar('\0');
                newpasswordField.setText(String.valueOf(newpasswordField.getPassword()));
            }
            @Override
            public void focusLost(FocusEvent e){
                newpasswordField.setEchoChar('*');
                newpasswordField.setText(String.valueOf(newpasswordField.getPassword()));
            }

        });
        c.gridx=1;
        c.gridy=3;
        c.ipady=0;
        c.weighty=0.5;
        profile.add(newpasswordField,c);

        update=new JButton("SAVE");
        update.setBackground(new Color(28, 80, 200));
        update.setForeground(Color.WHITE);
        update.setPreferredSize(new Dimension(450,35));
        update.setBorder(BorderFactory.createLineBorder(new Color(170, 101, 37)));
        update.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        update.setCursor(new Cursor(Cursor.HAND_CURSOR));
        update.setFocusPainted(false);
        update.addActionListener(e -> {
            if ((String.valueOf(passwordField.getPassword())).equals((String.valueOf(newpasswordField.getPassword())))){
                if (OnlineExamination.check.equals(String.valueOf(passwordField.getPassword()))){
                    JOptionPane.showMessageDialog(null, "Can not change to old password!!","QUERY",JOptionPane.INFORMATION_MESSAGE);
                }
                else if ((String.valueOf(passwordField.getPassword())).length() == 0){
                    JOptionPane.showMessageDialog(null, "Can not change password!!","QUERY",JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    OnlineExamination.user = userTextField.getText();
                    OnlineExamination.check = String.valueOf(passwordField.getPassword());
                    JOptionPane.showMessageDialog(null, "Password changed Successfully!!","SUCCESS",JOptionPane.INFORMATION_MESSAGE);
                    profile.dispose();
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "Please match both Password!!","QUERY",JOptionPane.INFORMATION_MESSAGE);
            }
        });
        c.gridx=0;
        c.gridy=4;
        c.gridwidth=2;
        c.weighty=1.5;
        profile.add(update,c);
        profile.pack();
        profile.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        profile.setLocationRelativeTo(null);
        profile.setVisible(true);
    }
}

public class OnlineExamination extends JFrame{
    static JButton login_btn;
    static JButton forgot_btn;
    
    static JPanel login_panel;
    
    static JLabel Iconlabel;
    static JLabel header;
    static JLabel username;
    static JLabel password;

    static JTextField userTextField;
    static JPasswordField passwordField;

    static String user = "robot";
    static String check = "pass";

    OnlineExamination(){
        int Height = ((Toolkit.getDefaultToolkit()).getScreenSize()).height;
        int Width = ((Toolkit.getDefaultToolkit()).getScreenSize()).width;

        ImageIcon Img = new ImageIcon("background1.jpg");
        Image obj=Img.getImage();
        Image temp=obj.getScaledInstance(Width,Height,Image.SCALE_SMOOTH);
        Img = new ImageIcon(temp);
        JLabel background=new JLabel("",Img,JLabel.CENTER);
        background.setBounds(0,0,Width,Height);
        add(background);

        background.setLayout(new GridBagLayout());
        GridBagConstraints c= new GridBagConstraints();
        c.anchor= GridBagConstraints.WEST;
        c.fill=GridBagConstraints.HORIZONTAL;

        login_panel = new JPanel(new GridBagLayout());
        login_panel.setBackground(new Color(0,0,0,80));
        login_panel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        GridBagConstraints constraints1= new GridBagConstraints();
        constraints1.anchor= GridBagConstraints.NORTH;
        constraints1.insets= new Insets(10,10,10,10);

        Iconlabel = new JLabel(new ImageIcon("icon.png"));
        Iconlabel.setPreferredSize(new Dimension(90,120));
        constraints1.gridx=0;
        constraints1.gridy=0;
        constraints1.ipady=50;
        constraints1.gridwidth=2;
        login_panel.add(Iconlabel,constraints1);

        header=new JLabel("SIGN IN",JLabel.CENTER);
        constraints1.gridx=0;
        constraints1.gridy=1;
        constraints1.ipady=5;
        constraints1.gridwidth=2;
        header.setFont(new Font("Arial", Font.BOLD, 25));
        header.setForeground(Color.WHITE);
        login_panel.add(header,constraints1);

        username=new JLabel("USENAME:",JLabel.LEFT);
        constraints1.gridx=0;
        constraints1.gridy=2;
        constraints1.ipady=0;
        constraints1.gridwidth=1;
        username.setFont(new Font("Arial", Font.BOLD, 20));
        username.setForeground(Color.WHITE);
        login_panel.add(username,constraints1);

        userTextField = new JTextField(20);
        constraints1.gridx=1;
        constraints1.gridy=2;
        constraints1.ipady=0;
        userTextField.setText("Username or Email");
        userTextField.setFont(new Font("Arial", Font.PLAIN, 20));
        userTextField.setForeground(Color.DARK_GRAY);
        userTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e){
                if (userTextField.getText().equals("Username or Email")){
                    userTextField.setText("");
                    userTextField.setForeground(Color.BLACK);
                }
                else{
                    userTextField.setText(userTextField.getText());
                    userTextField.setForeground(Color.BLACK);
                }
                
            }
            @Override
            public void focusLost(FocusEvent e){
                if (userTextField.getText().equals("Username or Email")|| userTextField.getText().length()==0){
                    userTextField.setText("Username or Email");
                    userTextField.setForeground(Color.DARK_GRAY);
                }
                else{
                    userTextField.setText(userTextField.getText());
                    userTextField.setForeground(Color.BLACK);
                }
            }

        });
        userTextField.setFocusable(false);
        login_panel.add(userTextField,constraints1);

        password=new JLabel("PASSWORD:",JLabel.LEFT);
        constraints1.gridx=0;
        constraints1.gridy=3;
        constraints1.ipady=0;
        password.setFont(new Font("Arial", Font.BOLD, 20));
        password.setForeground(Color.WHITE);
        login_panel.add(password,constraints1);

        passwordField = new JPasswordField(20);
        constraints1.gridx=1;
        constraints1.gridy=3;
        constraints1.ipady=0;
        passwordField.setText("Password");
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setForeground(Color.DARK_GRAY);
        passwordField.setEchoChar('\0');
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e){
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('*');
                
            }
            @Override
            public void focusLost(FocusEvent e){
                if (String.valueOf(passwordField.getPassword()).equals("")){
                    passwordField.setEchoChar('\0');
                    passwordField.setText("Password");
                    passwordField.setForeground(Color.DARK_GRAY);
                }
            }

        });
        passwordField.setFocusable(false);
        login_panel.add(passwordField,constraints1);

        login_btn=new JButton("LOG IN");
        login_btn.setBackground(new Color(170, 101, 37));
        login_btn.setForeground(Color.WHITE);
        login_btn.setPreferredSize(new Dimension(450,35));
        login_btn.setBorder(BorderFactory.createLineBorder(new Color(170, 101, 37)));
        login_btn.setFont(new Font("Times New Roman", Font.PLAIN, 25));
        login_btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        login_btn.setFocusPainted(false);
        login_btn.addActionListener(e -> {
            if (user.equals(userTextField.getText()) && check.equals(String.valueOf(passwordField.getPassword()))){
                this.dispose();
                Main m = new Main();
                m.MainWindow();
            }
            else{
                JOptionPane.showMessageDialog(null, "Invalid Username or Password!!","QUERY",JOptionPane.INFORMATION_MESSAGE);
            }
        });
        constraints1.gridx=0;
        constraints1.gridy=4;
        constraints1.gridwidth=2;
        login_panel.add(login_btn,constraints1);

        forgot_btn=new JButton("Forgot password ?");
        forgot_btn.setBackground(new Color(255,255,255));
        forgot_btn.setPreferredSize(new Dimension(450,32));
        forgot_btn.setForeground(Color.BLACK);
        forgot_btn.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        forgot_btn.addActionListener(e -> {Main M = new Main(); M.Profile("","");});
        forgot_btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgot_btn.setFocusPainted(false);
        constraints1.gridx=0;
        constraints1.gridy=5;
        constraints1.gridwidth=2;
        login_panel.add(forgot_btn,constraints1);

        c.gridx=0;
        c.gridy=1;
        background.add(login_panel,c);

        addMouseMotionListener(new MouseMotionAdapter(){
            public void mouseDragged(MouseEvent e){
                userTextField.setFocusable(true);
                passwordField.setFocusable(true);
            }
            public void mouseMoved(MouseEvent e){
                userTextField.setFocusable(true);
                passwordField.setFocusable(true);
            }
        });
        setTitle("ONLINE EXAMINATION");
        setExtendedState(MAXIMIZED_BOTH);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    public static void main(String []args){
        new OnlineExamination();
    }
}
