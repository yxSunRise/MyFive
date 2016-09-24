package com.example.myfive;

import java.util.Stack;
import android.graphics.Point;
public class Chessboard implements Cloneable{
	private final int NONE = 0;
	private final int BORDER = -2;
	public int col,row;
	public Point lastPoint;
	public int lastType=0;
	private int[][] cb;
	private Stack<Point> stack;
	public int chessNum(){
		return stack.size();
	}
	public Chessboard(int col,int row) {
		this.col=col;
		this.row =row;
		stack=new Stack<Point>();
		cb=new int[row][col];
		for(int i=0;i<row;i++)
			for(int j=0;j<col;j++)
				cb[i][j]=NONE;
	}
	public void setChess(Point p,int type){
		lastType=type;
		lastPoint= p;
		cb[p.y][p.x]=type;
		stack.push(p);
	}
	public int getType(Point p){
		if(p==null) return BORDER;
		return cb[p.y][p.x];
	}
	public void clear(){
		for(int i=0;i<row;i++)
			for(int j=0;j<col;j++)
				cb[i][j]=NONE;
		stack.clear();
	}
	public Point back(int step){
		Point p=null;
		while(step-->0 && !stack.isEmpty()){
			p=stack.pop();
			cb[p.y][p.x]=NONE;
		}
		return p;
	}
	@SuppressWarnings("unchecked")
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Chessboard cb_clone=(Chessboard) super.clone();
		cb_clone.stack=(Stack<Point>) stack.clone();
		cb_clone.cb=new int[row][col];
		for(int i=0;i<row;i++)
			for(int j=0;j<col;j++)
				cb_clone.cb[i][j]=cb[i][j];
		return cb_clone;
	}
	@Override
	public String toString() {
		String s="";
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
				s=s+cb[i][j]+"  ";
			s+='\n';
		}
		return s;
	}
}

