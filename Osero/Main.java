package Osero;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main extends JFrame implements MouseListener {
    DataClass data = new DataClass();
    public static void main(String[] args) {
        new Main();
    }

    public Main(){
        int windowSize = data.getWindowSize();
        int blockSize = data.getBlockSize();
        int blockNum = data.getBlockNum();
        int padding = data.getPadding();
        JLabel teban = new JLabel();

        JPanel panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);

                //BaseLine
                g.setColor(Color.BLACK);
                for (int i=0; i<=data.getBlockNum(); i++) {
                    //縦
                    g.drawLine(padding + i*blockSize, padding, padding + i*blockSize, padding + blockNum*blockSize);
                    //横
                    g.drawLine(padding, padding + i*blockSize, padding + blockNum*blockSize, padding + i*blockSize);
                }

                //石の描画
                int[][] fieald = data.getField();
                for (int i=0; i<fieald.length; i++){
                    for (int j=0; j<fieald[i].length; j++){
                        if (fieald[i][j] == 1) {
                            //1が入っている場合，先手なので黒の石を配置
                            g.setColor(Color.BLACK);
                            g.fillArc(padding + j*blockSize + 8, padding + i*blockSize + 8, blockSize*4/5, blockSize*4/5, 0, 360);
                        } else if (fieald[i][j] == -1) {
                            //-1が入っている場合，後手なので白の石を配置
                            g.setColor(Color.WHITE);
                            g.fillArc(padding + j*blockSize + 8, padding + i*blockSize + 8, blockSize*4/5, blockSize*4/5, 0, 360);
                        }
                    }
                }
                //先手番か後手番かの表示
                if (data.getPlayer() == 1){
                    teban.setText("先手番");
                } else {
                    teban.setText("後手番");
                }
                teban.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
            }
        };
        panel.add(teban);
        panel.addMouseListener(this);
        panel.setBackground(new Color(70,155,100));
        add(panel);
        setSize(windowSize, windowSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int currentIndexX = data.getIndexX(x);
        int currentIndexY = data.getIndexY(y);


        if (data.isPass(data.getPlayer())){
            //1.両者共にパスとなる状態．
            if (data.getDoublePass()){
                //end game
                ShowResult();
            } else {
                data.changePlayer();
                data.setDoublePass(true);
            }
        } else {
            data.putStoneOnFieald(currentIndexX, currentIndexY, data.getPlayer());
            data.setDoublePass(false);
        }

        //2.全てのマスが石で埋まった状態．
        if (data.isFillAll()){
            //end game
            ShowResult();
        //3.どちらかの色の石のみになった状態．
        } else if (data.isOnlyOneColor()){
            //end game
            ShowResult();
        } else {
        //4.ゲーム続行
        }
        repaint();
    }

    public void ShowResult(){
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JLabel result1 = new JLabel();
        JLabel result2 = new JLabel();
        panel.setLayout(null);
        result1.setLayout(null);
        result2.setLayout(null);

        data.checkWinner();
        int countOfBlackStones = data.getCountOfBlackStones();
        int countOfWhiteStones = data.getCountOfWhiteStones();

        result1.setText("先手："+countOfBlackStones+"  "+"後手："+countOfWhiteStones);
        result1.setFont(new Font(Font.DIALOG, Font.PLAIN, 30));
        result1.setBounds(0,50,400,50);
        result1.setHorizontalAlignment(JLabel.CENTER);

        if (countOfBlackStones > countOfWhiteStones){
            result2.setText("勝者：先手");
        } else if (countOfBlackStones < countOfWhiteStones){
            result2.setText("勝者：後手");
        } else {
            result2.setText("引き分け");
        }
        result2.setFont(new Font(Font.DIALOG, Font.PLAIN, 30));
        result2.setBounds(0,120,400,50);
        result2.setHorizontalAlignment(JLabel.CENTER);

        JButton resetbutton = new JButton("RESET");
        resetbutton.setBounds(150,270,100,20);
        resetbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                data.resetGame();
                repaint();
                frame.dispose();
            }
        });

        JButton exitButton = new JButton("EXIT");
        exitButton.setBounds(150, 300, 100, 20);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                data.resetGame();
                System.exit(0);
            }
        });
        panel.add(result1);
        panel.add(result2);
        panel.add(exitButton);
        panel.add(resetbutton);
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}