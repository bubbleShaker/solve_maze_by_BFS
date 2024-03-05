package rep;

import java.awt.*;
import java.awt.event.*;
import java.util.Deque;
import java.util.ArrayDeque;

import javax.swing.*;

public class RepTest {
    public static void main(String[] args) {
        JFrame mainFrame=new MainJFrame(10,10);
        mainFrame.setTitle("BFS(幅優先探索)で迷路を最短で解く");
    }
}

class MainJFrame extends JFrame implements ActionListener{
    private int h,w;
    private XY start,goal;
    private int routeX,routeY,routeInd;
    private int routeStep;
    private String lastCommand="Wall";
    private Deque<XY> queue=new ArrayDeque<>();
    private Timer timer;
    private Color gridColor;
    private Color wallColor;
    private Color routeColor;
    private boolean isSolving=false;
    private JLabel solveAlertLabel;
    private JLabel wallAlertLabel;
    private JLabel helpAlertLabel;
    private char[][] grid;
    private int[][] visited;
    private JButton[][] buttons;
    private XY[] route;
    private int[] dx={-1,0,1,0};
    private int[] dy={0,1,0,-1};
    MainJFrame(int height,int width){
        h=height;
        w=width;
        grid=new char[h][w];
        visited=new int[h][w];
        start=new XY(0,0);
        goal=new XY(h-1,w-1);
        JMenuBar menuBar=new JMenuBar();
        JMenu menuMode=new JMenu("Mode");
        JMenuItem menuWall=new JMenuItem("Wall");
        JMenuItem menuSolve=new JMenuItem("Solve");
        JMenuItem menuClear=new JMenuItem("Clear");
        JMenuItem menuHelp=new JMenuItem("Help");

        menuBar.setLayout(new BorderLayout());
        menuWall.addActionListener(this);
        menuWall.setActionCommand("Wall");
        menuSolve.addActionListener(this);
        menuSolve.setActionCommand("Solve");
        menuClear.addActionListener(this);
        menuClear.setActionCommand("Clear");
        menuHelp.addActionListener(this);
        menuHelp.setActionCommand("Help");

        getRootPane().setJMenuBar(menuBar);
        menuBar.add("West",menuMode);
        menuMode.add(menuWall);
        menuMode.add(menuSolve);
        menuMode.add(menuClear);
        menuBar.add("East",menuHelp);

        buttons=new JButton[h][w];
        gridColor=Color.WHITE;
        wallColor=Color.BLACK;
        routeColor=Color.YELLOW;

        solveAlertLabel=new JLabel("攻略不可能な迷路です");
        wallAlertLabel=new JLabel("壁の配置はStart、Goal以外にしてください");
        helpAlertLabel=new JLabel("<html><body>Modeを押すと以下の3つを選択できます<br><br>Wall:壁を生成できます<br>Solve:迷路を解きます<br>Clear:全ての壁を消去します</body><html>");

        getContentPane().setLayout(new GridLayout(h,w));
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                if(i==0&&j==0){
                    buttons[i][j]=new JButton("Start");
                }else if(i==h-1&&j==w-1){
                    buttons[i][j]=new JButton("Goal");
                }else{
                    buttons[i][j]=new JButton();
                }
                buttons[i][j].setActionCommand(i+","+j);
                buttons[i][j].addActionListener(this);
                buttons[i][j].setBackground(gridColor);
                getContentPane().add(buttons[i][j]);
            }
        }
        timer=new Timer(50,this);
        timer.setActionCommand("timer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("JPanelTest");
        setSize(630,650);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
        this.gridInit();
        try{
            int sleepTime=300;
            Thread.sleep(sleepTime);
        }catch(InterruptedException e){
            System.out.println("Sleep Error");
        }
        JOptionPane.showMessageDialog(this, helpAlertLabel);
    }
    public void actionPerformed(ActionEvent e){
        String actionName=e.getActionCommand();
        if(actionName!="timer"&&isSolving==true){
            return;
        }
        if(actionName=="Solve"){
            this.BFS();
            if(visited[goal.getX()][goal.getY()]==-1){
                JOptionPane.showMessageDialog(this, solveAlertLabel);
                lastCommand="Wall";
                return;
            }
            route=makeRoute();
            routeStep=getGoalStep()+1;
            lastCommand="Solve";
            if(routeInd==routeStep&&routeStep!=0){
                gridInitWithoutWall();
            }
            isSolving=true;
            timer.start();
        }else if(actionName=="timer"){
            routeX=route[routeInd].getX();
            routeY=route[routeInd].getY();
            buttons[routeX][routeY].setBackground(routeColor);
            routeInd++;
            if(routeInd==routeStep){
                timer.stop();
                isSolving=false;
            }
        }else if(actionName=="Wall"){
            gridInitWithoutWall();
            lastCommand="Wall";
        }else if(actionName=="Clear"){
            this.gridInit();
            lastCommand="Wall";
        }else if(actionName=="Help"){
            JOptionPane.showMessageDialog(this, helpAlertLabel);
        }else{//Wall
            if(lastCommand=="Solve"){
                gridInitWithoutWall();
                lastCommand="Wall";
            }
            XY wallXY=comToXY(actionName);
            int x=wallXY.getX();
            int y=wallXY.getY();
            if((x==0&&y==0)||(x==h-1&&y==w-1)){
                JOptionPane.showMessageDialog(this, wallAlertLabel);
                return;
            }
            if(grid[x][y]=='.'){
                grid[x][y]='#';
                buttons[x][y].setBackground(wallColor);
            }else if(grid[x][y]=='#'){
                grid[x][y]='.';
                buttons[x][y].setBackground(gridColor);
            }
        }
    }
    public void BFS(){
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                visited[i][j]=-1;
            }
        }
        queue.add(start);
        visited[start.getX()][start.getY()]=0;
        while(!queue.isEmpty()){
            XY xy=queue.poll();
            int x=xy.getX();
            int y=xy.getY();
            for(int i=0;i<4;i++){
                int nx=x+dx[i];
                int ny=y+dy[i];
                if(nx<0||nx>=h||ny<0||ny>=w)continue;
                if(grid[nx][ny]=='#')continue;
                if(visited[nx][ny]!=-1)continue;
                visited[nx][ny]=visited[x][y]+1;
                queue.add(new XY(nx,ny));
            }
        }
    }
    public void gridInit(){
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                grid[i][j]='.';
                visited[i][j]=-1;
                buttons[i][j].setBackground(gridColor);
            }
        }
        routeX=0;
        routeY=0;
        routeInd=0;
    }
    public void gridInitWithoutWall(){
        for(int i=0;i<h;i++){
            for(int j=0;j<w;j++){
                visited[i][j]=-1;
                if(grid[i][j]=='.')buttons[i][j].setBackground(gridColor);
            }
        }
        routeX=0;
        routeY=0;
        routeInd=0;
    }
    public int getGoalStep(){
        return visited[goal.getX()][goal.getY()];
    }
    public XY[] makeRoute(){
        int n=getGoalStep()+1;
        XY[] res=new XY[n];
        int x=goal.getX(),y=goal.getY();
        int nowInd=n-1;
        res[nowInd]=new XY(x,y);
        nowInd--;
        while(x!=0||y!=0){
            for(int i=0;i<4;i++){
                int nx=x+dx[i],ny=y+dy[i];
                if(nx<0||nx>=h||ny<0||ny>=w)continue;
                if(visited[nx][ny]==visited[x][y]-1){
                    res[nowInd]=new XY(nx,ny);
                    nowInd--;
                    x=nx;
                    y=ny;
                    break;
                }
            }
        }
        return res;
    }
    XY comToXY(String actionName){
        XY resXY;
        int x,y;
        char[] charCommand=actionName.toCharArray();
        int n=actionName.length();
        int xSize=0,ySize=0;
        boolean isX=true;
        for(int i=0;i<n;i++){
            if(isX){
                if(charCommand[i]==','){
                    isX=false;
                    continue;
                }
                xSize++;
            }else{
                ySize++;
            }
        }
        int[] xVec=new int[xSize];
        int[] yVec=new int[ySize];
        isX=true;
        for(int i=0;i<n;i++){
            if(isX){
                if(charCommand[i]==','){
                    isX=false;
                    continue;
                }
                xVec[i]=Character.getNumericValue(charCommand[i]);
            }else{
                yVec[i-xSize-1]=Character.getNumericValue(charCommand[i]);
            }
        }
        x=this.toInt(xVec);
        y=this.toInt(yVec);
        resXY=new XY(x,y);
        return resXY;
    }
    int toInt(int[] vec){
        int n=vec.length;
        int res=0;
        for(int i=0;i<n;i++){
            res*=10;
            res+=vec[i];
        }
        return res;
    }
}

class XY{
    private int x;
    private int y;
    XY(int x,int y){
        this.x=x;
        this.y=y;
    }
    XY(){
        this.x=0;
        this.y=0;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}