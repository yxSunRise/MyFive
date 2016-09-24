package com.example.myfive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {
	
	private final static float scaBorder=0.35f;
	private final static float scaChess=0.45f;
	private final int BLACK = 1;
	private final int WHITE = -1;
	
	private Paint p;
	private Context context;
	private int viewLen;
	
	private Bitmap white;
	private Bitmap black;
	
	public float len;
	public float pad_top;
	public float pad_left;
	private float x1,x2,y1,y2;
	private RectF[][] recf;
	
	private Chessboard chessboard;
	private int row;
	private int col;
	
	private SoundPool sp;
	private int soundId;
	
	public void setChessboard(Chessboard chessboard) {
		this.chessboard = chessboard;
		row=chessboard.row;
		col=chessboard.col;
		recf=new RectF[row][col];
		init();
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context,attrs);
		this.context=context;
		setBackgroundResource(R.drawable.chessboard_bg);
		p=new Paint();
		black=BitmapFactory.decodeResource(context.getResources(), R.drawable.black);
		white=BitmapFactory.decodeResource(context.getResources(), R.drawable.white);
		sp=new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundId=sp.load(context, R.raw.luozi, 1);
	}
	
	public GameView(Context context) {
		super(context);
		this.context=context;
		setBackgroundResource(R.drawable.chessboard_bg);
		p=new Paint();
		black=BitmapFactory.decodeResource(context.getResources(), R.drawable.black);
		white=BitmapFactory.decodeResource(context.getResources(), R.drawable.white);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int smallMeasureSpec;
		if(MeasureSpec.getSize(heightMeasureSpec)>MeasureSpec.getSize(widthMeasureSpec)){
			viewLen=MeasureSpec.getSize(widthMeasureSpec);
			smallMeasureSpec=widthMeasureSpec;
		}
		else {
			viewLen=MeasureSpec.getSize(heightMeasureSpec);
			smallMeasureSpec=heightMeasureSpec;
		}
		init();
		super.onMeasure(smallMeasureSpec, smallMeasureSpec);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		p.setStrokeWidth(10);
		canvas.drawLines(new float[]{
				x1-5,y1,x2+5,y1,
				x2,y1,x2,y2,
				x1-5,y2,x2+5,y2,
				x1,y1,x1,y2,}, p);
		p.setStrokeWidth(0);
		for(int i=0;i<row;i++)
			canvas.drawLine(pad_left,pad_top+i*len,pad_left+(col-1)*len, pad_top+i*len, p);
		for(int i=0;i<col;i++)
			canvas.drawLine(pad_left+i*len,pad_top,pad_left+i*len,pad_top+(row-1)*len, p);
		if(col==row && col>10 && col%2==1){
			canvas.drawCircle(pad_left+(col-1)*len/2, pad_top+(row-1)*len/2, len/5, p);
			canvas.drawCircle(pad_left+(col-9)*len/2, pad_top+(row-9)*len/2, len/6, p);
			canvas.drawCircle(pad_left+(col-9)*len/2, pad_top+(row+7)*len/2, len/6, p);
			canvas.drawCircle(pad_left+(col+7)*len/2, pad_top+(row-9)*len/2, len/6, p);
			canvas.drawCircle(pad_left+(col+7)*len/2, pad_top+(row+7)*len/2, len/6, p);
		}
		for(int i=0;i<row;i++)
			for(int j=0;j<col;j++)
				if(chessboard.getType(new Point(j, i))==WHITE)
					canvas.drawBitmap(white, null, recf[i][j], p);
				else if(chessboard.getType(new Point(j, i))==BLACK)
					canvas.drawBitmap(black, null, recf[i][j], p);
		if(Configure.humanType+chessboard.lastType==0){
			p.setColor(Color.RED);
			canvas.drawCircle(pad_left+chessboard.lastPoint.x*len,pad_top+chessboard.lastPoint.y*len,len/5,p);
			p.setColor(Color.BLACK);
		}
		if(chessboard.chessNum()!=0)
			sp.play(soundId,(float) (1.0*Configure.volume/100),(float) (1.0*Configure.volume/100), 1, 0, 1);
	}
	private void init(){
		if(row<=col)
			len=1.0f*viewLen/(col+1);
		else
			len=1.0f*viewLen/(row+1);
		
		pad_left=(1.0f*viewLen-(col-1)*len)/2;
		pad_top=(1.0f*viewLen-(row-1)*len)/2;
		x1=pad_left-scaBorder*len;
		x2=pad_left+(col-1)*len+scaBorder*len;
		y1=pad_top-scaBorder*len;
		y2=pad_top+(row-1)*len+scaBorder*len;
		for(int i=0;i<row;i++)
			for(int j=0;j<col;j++)
				recf[i][j]=new RectF(
						pad_left+j*len-scaChess*len, 
						pad_top+i*len-scaChess*len, 
						pad_left+j*len+scaChess*len, 
						pad_top+i*len+scaChess*len);
	}
}
