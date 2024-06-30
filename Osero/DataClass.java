package Osero;

import java.util.ArrayList;

public class DataClass {

    public record Tuple(int y, int x) {}

    private ArrayList<Tuple> vector;
    private int windowSize;
    private int blockSize;
    private int blockNum;
    private int padding;
    private int[][] fieald;
    private int player;
    private int indexX;
    private int indexY;
    private boolean doublePass;
    private int countOfBlackStones;
    private int countOfWhiteStones;

    public DataClass(){
        //8方向のベクトル
        this.vector = new ArrayList<>();
        for (int i=-1; i<=1; i++){
            for (int j=-1; j<=1; j++){
                if (!((i == 0) & (j == 0))){
                    Tuple vec = new Tuple(i, j);
                    this.vector.add(vec);
                }
            }
        }

        this.windowSize = 800;
        this.blockSize = 80;
        this.blockNum = 8;
        this.padding = (this.windowSize-this.blockSize*this.blockNum)/2;

        //フィールドデータの初期化
        this.fieald = new int[this.blockNum][this.blockNum];
        for (int i=0; i<this.fieald.length; i++){
            for (int j=0; j<this.fieald[i].length; j++){
                this.fieald[i][j] = 0;
            }
        }
        //初期の石を配置
        //○ ●
        //● ○
        this.fieald[3][3] = -1;
        this.fieald[4][4] = -1;
        this.fieald[3][4] = 1;
        this.fieald[4][3] = 1;

        this.player = 1; //1が先手，-1が後手
        this.indexX = -1;
        this.indexY = -1;
        this.doublePass = false;
        this.countOfBlackStones = 0;
        this.countOfWhiteStones = 0;
    }

    public void resetGame() {
        this.fieald = new int[this.blockNum][this.blockNum];
        for (int i=0; i<this.fieald.length; i++){
            for (int j=0; j<this.fieald[i].length; j++){
                this.fieald[i][j] = 0;
            }
        }
        //初期の石を配置
        this.fieald[3][3] = -1;
        this.fieald[4][4] = -1;
        this.fieald[3][4] = 1;
        this.fieald[4][3] = 1;

        this.player = 1; //1が先手，-1が後手
        this.indexX = -1;
        this.indexY = -1;
        this.doublePass = false;
        this.countOfBlackStones = 0;
        this.countOfWhiteStones = 0;
    }

    public void reverseStones(int currentX, int currentY, int currentPlayer){
        //上下左右，右上右下，左上左下の計8方向に対して，リバースできる石があるか確認する．
        //最初に石を配置したマスから指定方向に何色の石が配置されているか確認していく．
        //最初の石とは逆の色の石がある座標を記録するArrayを作成する
        //最初に置かれた石の色とは逆の色の石が配置されている場合はArrayに追加，最初に置かれた石と同じ色の石が見つかったら，
        //それまでの石をリバースする．もし同じ色の石が見つからなかったり，何もないマスが見つかればそこで検索を終了する．

        int x = currentX;
        int y = currentY;
        ArrayList<Tuple> reverseStones = new ArrayList<>();

        for (Tuple vec: vector){
            while ((0<=x)&(x<this.fieald.length) & (0<=y)&(y<this.fieald.length)){
                y += vec.y();
                x += vec.x();
                if ((0<=x)&(x<this.fieald.length) & (0<=y)&(y<this.fieald.length)){
                    if ((this.fieald[y][x] == currentPlayer*-1)){
                        Tuple pos = new Tuple(y, x);
                        reverseStones.add(pos);
                    }
                    else if ((this.fieald[y][x] == currentPlayer) & (reverseStones.size()>0)){
                        for (Tuple pos: reverseStones){
                            this.fieald[pos.y()][pos.x()] = currentPlayer;
                        }
                        break;
                    }
                    else {
                        break;
                    }
                }
            }
            x = currentX;
            y = currentY;
            reverseStones.clear();
        }
    }

    public boolean canPutStoneThere(int puttingX, int puttingY, int currentPlayer){
        int x = puttingX;
        int y = puttingY;
        int countOfCanReverseStones = 0;

        for (Tuple vec: vector){
            while ((0<=x)&(x<this.fieald.length) & (0<=y)&(y<this.fieald.length)){
                y += vec.y();
                x += vec.x();
                if ((0<=x)&(x<this.fieald.length) & (0<=y)&(y<this.fieald.length)){
                    if ((this.fieald[y][x] == currentPlayer*-1)){
                        countOfCanReverseStones += 1;
                    }
                    else if ((this.fieald[y][x] == currentPlayer) & (countOfCanReverseStones>0)){
                        return true;
                    }
                    else {
                        countOfCanReverseStones = 0;
                        break;
                    }
                }
            }
            x = puttingX;
            y = puttingY;
            countOfCanReverseStones = 0;
        }
        return false;
    }

    //石の配置
    public void putStoneOnFieald(int x, int y, int currentPlayer) {
        //currentPlayerは1 or -1
        boolean canPut = canPutStoneThere(x, y, currentPlayer);

        if ((this.fieald[y][x] == 0) & (canPut) & (0<=x)&(x<this.fieald.length)&(0<=y)&(y<this.fieald.length)){
            this.fieald[y][x] = currentPlayer;
            reverseStones(x, y, currentPlayer);
            changePlayer();
        }
    }

    //Passの判定
    public boolean isPass(int currentPlayer) {
        for (int i=0; i<this.fieald.length; i++){
            for (int j=0; j<this.fieald.length; j++){
                if (canPutStoneThere(j, i, currentPlayer)){
                    return false;
                }
            }
        }
        return true;
    }

    //マスが全て埋まっているかの判定
    public boolean isFillAll() {
        int countOfFillCell = 0;
        for (int i=0; i<this.fieald.length; i++){
            for (int j=0; j<this.fieald[i].length; j++){
                if ((this.fieald[i][j] == 1) | (this.fieald[i][j] == -1)){
                    countOfFillCell += 1;
                }
            }
        }
        if (countOfFillCell == this.blockNum*this.blockNum){
            return true;
        } else {
            return false;
        }
    }

    //どちらかの色の石のみになったかの判定
    public boolean isOnlyOneColor(){
        //両方の石の数を計算する．片方が0だった場合，tureをリターンする．
        int countOfBlack = 0;
        int countOfWhite = 0;
        for (int i=0; i<this.fieald.length; i++){
            for (int j=0; j<this.fieald.length; j++){
                if (this.fieald[i][j] == 1) {
                    countOfBlack += 1;
                } else if (this.fieald[i][j] == -1){
                    countOfWhite += 1;
                }
            }
        }
        if ((countOfBlack == 0) | (countOfWhite == 0)){
            return true;
        } else {
            return false;
        }
    }

    //それぞれの石の数を計測
    public void checkWinner(){
        for (int i=0; i<this.fieald.length; i++){
            for (int j=0; j<this.fieald.length; j++){
                if (this.fieald[i][j] == 1){
                    this.countOfBlackStones += 1;
                } else if (this.fieald[i][j] == -1){
                    this.countOfWhiteStones += 1;
                }
            }
        }
    }

    public int getCountOfBlackStones(){
        return this.countOfBlackStones;
    }
    public int getCountOfWhiteStones(){
        return this.countOfWhiteStones;
    }

    public int getWindowSize() {
        return this.windowSize;
    }
    public int getBlockSize() {
        return this.blockSize;
    }
    public int getBlockNum() {
        return this.blockNum;
    }
    public int getPadding() {
        return this.padding;
    }

    public int[][] getField() {
        return this.fieald;
    }

    public int getPlayer() {
        return this.player;
    }
    public void changePlayer() {
        this.player *= -1;
    }

    public int getIndexX(int x){
        this.indexX = (x-this.padding)/this.blockSize;
        return this.indexX;
    }
    public int getIndexY(int y){
        this.indexY = (y-this.padding)/this.blockSize;
        return this.indexY;
    }

    public boolean getDoublePass(){
        return this.doublePass;
    }
    public void setDoublePass(boolean flag){
        this.doublePass = flag;
    }
}