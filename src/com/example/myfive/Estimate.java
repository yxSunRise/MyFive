package com.example.myfive;

import android.graphics.Point;

public class Estimate {
	private final int BORDER = -2;
	private final int NONE = 0;
	
	private Chessboard chessbord;
	private int estimateType;
	
	public int five=0;
	public int huofour=0;
	public int sifour=0;
	public int halffour=0;
	public int huothree=0;
	public int halfthree=0;
	public int sithree=0;
	public int huotwo=0;
	public int huotwo1=0;
	public int huotwo2=0;
	
	
	private double vfive=50000;
	private double vhuofour=20000;
	private double vhalffour=3000;
	private double vhuothree=500;
	private double vhuotwo=50;
	private double vhuotwo1=40;
	private double vhuotwo2=30;
	private double vsifour=200;
	private double vhalfthree=250;
	private double vsithree=15;
	private double coefficient;
	

	

	
	
	public Estimate(Chessboard chessbord,int estimateType,boolean isMax) {
		this.chessbord=chessbord;
		this.estimateType=estimateType;
		coefficient=isMax?0.1:-0.3;
	}
	
	public static Point getAbs(Point start,int dir,int dis,Chessboard chessbord){
		Point p=new Point(start.x, start.y);
		switch (dir) {
		case 0:		p.y-=dis;			break;
		case 1:		p.x+=dis;p.y-=dis;	break;
		case 2:		p.x+=dis;			break;
		case 3:		p.x+=dis;p.y+=dis;	break;
		case 4:		p.y+=dis;			break;
		case 5:		p.x-=dis;p.y+=dis;	break;
		case 6:		p.x-=dis;			break;
		case 7:		p.x-=dis;p.y-=dis;	break;
		default: break;
		}
		if(p.x<chessbord.col && p.y<chessbord.row && p.x>=0 && p.y>=0){
			return p;
		}else{
			return null;
		}
	}

	public static boolean hasfive(Point p,Chessboard cb){
		int type=cb.getType(p);
		for(int i=0;i<4;i++){
			int count=0;
			for(int j=1;type==cb.getType(getAbs(p,i,j,cb));count++,j++);
			for(int j=1;type==cb.getType(getAbs(p,i+4,j,cb));count++,j++);
			if(count>=4) return true;
		}
		return false;
	}
	
	public  void findTrait(int estimateType){
		five=0;
		huofour=0;
		sifour=0;
		halffour=0;
		huothree=0;
		halfthree=0;
		sithree=0;
		huotwo=0;
		huotwo1=0;
		huotwo2=0;
	out:	for(int r=0;r<chessbord.row;r++)
			for(int c=0;c<chessbord.col;c++){
				Point p=new Point(c, r);
				if(chessbord.getType(p)==estimateType){
					/**
					 * 对称结构
					 **/
				
					for(int i=0;i<4;i++){
						int j=0;
						if(chessbord.getType(getAbs(p,i+4,1,chessbord))!=estimateType)
							for(j=1;estimateType==chessbord.getType(getAbs(p,i,j,chessbord));j++);
						if(j>4) {five=1;break out;}
						switch(j){
						case 4:
						{
							if(chessbord.getType(getAbs(p,i,j,chessbord))==NONE && chessbord.getType(getAbs(p,i+4,1,chessbord))==NONE)
								huofour++; 
							else if((chessbord.getType(getAbs(p,i,j,chessbord))+chessbord.getType(getAbs(p,i+4,1,chessbord))!=0) &&
								(chessbord.getType(getAbs(p,i,j,chessbord))*chessbord.getType(getAbs(p,i+4,1,chessbord))==0))
								halffour++;
							else
								sifour++;
							break;
						}
						case 3:
						{
							/** ○○○  **/
							if(chessbord.getType(getAbs(p,i,j,chessbord))==NONE && chessbord.getType(getAbs(p,i+4,1,chessbord))==NONE)
							{
								huothree++;
								if(chessbord.getType(getAbs(p,i,j+1,chessbord))==estimateType) halffour++;
								if(chessbord.getType(getAbs(p,i+4,2,chessbord))==estimateType) halffour++;
							}
							/** ●○○○  **/
							else if(chessbord.getType(getAbs(p,i,j,chessbord))==NONE && 
									(chessbord.getType(getAbs(p,i+4,1,chessbord))==estimateType*(-1)) ||chessbord.getType(getAbs(p,i+4,1,chessbord))==BORDER)
								if(chessbord.getType(getAbs(p,i,j+1,chessbord))==estimateType) halffour++;
								else if(chessbord.getType(getAbs(p,i,j+1,chessbord))==NONE) halfthree++;
								else sithree++;
							/** ○○○●  **/
							else if(chessbord.getType(getAbs(p,i+4,1,chessbord))==NONE && 
									(chessbord.getType(getAbs(p,i,j,chessbord))==estimateType*(-1)) ||chessbord.getType(getAbs(p,i,j,chessbord))==BORDER)
								if(chessbord.getType(getAbs(p,i+4,2,chessbord))==estimateType) halffour++;
								else if(chessbord.getType(getAbs(p,i+4,2,chessbord))==NONE) halfthree++;
								else sithree++;
							/** ●○○○●  **/
							else 
								sithree++;
							break;
						}
						case 2:
						{
							/** ○○ **/
							if(chessbord.getType(getAbs(p,i,j,chessbord))==NONE && chessbord.getType(getAbs(p,i+4,1,chessbord))==NONE)
							{
								huotwo++;
								char a=0;
								/**
								 * 00代表 NONE
								 * 01代表 estimateType
								 * 10代表 反方或边界 
								 * (a.7,a.6)(a.5,a.4)_○○_(a.3,a.2)(a.1,a.0)
								 * **/
								int[] t={chessbord.getType(getAbs(p,i,j+2,chessbord)),chessbord.getType(getAbs(p,i,j+1,chessbord)),
										chessbord.getType(getAbs(p,i+4,2,chessbord)),chessbord.getType(getAbs(p,i+4,3,chessbord))};
								for(int k=0;k<4;k++){
									if(t[k]==estimateType) a+=0x01<<(2*k); 
									else if(t[k]==estimateType*(-1) || t[k]==BORDER) a+=0x02<<(2*k);
								}
								if((a & 0x3c)==0x28) huotwo--;
								else if((a & 0x3c)==0x24)
									switch(a & 0x03){
									case 0:huothree++;break;
									case 1:halffour++;break;
									case 2:halfthree++;huotwo--;break;
									default: break;
									}
								else if((a & 0x3c)==0x18)
									switch(a & 0xc0){
									case 0:huothree++;break;
									case 0x80:halfthree++;huotwo--;break;
									default: break;
									}
								else if((a & 0x3c)==0x14)
									/** ?○_○○_○? **/
									switch(a & 0xc3){
									case 0x00:;  
									case 0x02:;
									case 0x80:huothree++;halfthree++;huotwo--;break;
									case 0x01:;
									case 0x81:halffour++;halfthree++;break;
									case 0x82:huotwo--;halfthree++;break;
									default:break;
									}
								else if(((a & 0x0f)==0x01) || ((a & 0xf0)==0x40))
									halfthree++;
							}
							/** ●○○_○ **/
							else if(chessbord.getType(getAbs(p,i,j,chessbord))==NONE && 
									(chessbord.getType(getAbs(p,i+4,1,chessbord))==BORDER || 
									chessbord.getType(getAbs(p,i+4,1,chessbord))==estimateType*(-1)) &&
									chessbord.getType(getAbs(p,i,j+1,chessbord))==estimateType)
							{
								if(chessbord.getType(getAbs(p,i,j+2,chessbord))==estimateType) halffour++;
								else if(chessbord.getType(getAbs(p,i,j+2,chessbord))==NONE) halfthree++;
								else sithree++;
							}
							/** ○_○○● **/
							else if(chessbord.getType(getAbs(p,i+4,1,chessbord))==NONE && 
									(chessbord.getType(getAbs(p,i,j,chessbord))==BORDER || 
									chessbord.getType(getAbs(p,i,j,chessbord))==estimateType*(-1)) &&
									chessbord.getType(getAbs(p,i+4,2,chessbord))==estimateType)
							{
								if(chessbord.getType(getAbs(p,i+4,3,chessbord))==NONE) halfthree++;
								else if(chessbord.getType(getAbs(p,i+4,3,chessbord))!=estimateType) sithree++;
							}
							break;
						}
						case 1:
						{
							if(chessbord.getType(getAbs(p,i,j,chessbord))==NONE && chessbord.getType(getAbs(p,i+4,1,chessbord))==NONE){
								if(chessbord.getType(getAbs(p,i,j+1,chessbord))==estimateType && chessbord.getType(getAbs(p,i,j+2,chessbord))==NONE)
									huotwo1++;  /** ○_○ **/
								if(chessbord.getType(getAbs(p,i,j+1,chessbord))==NONE && chessbord.getType(getAbs(p,i,j+2,chessbord))==estimateType &&
										chessbord.getType(getAbs(p,i,j+3,chessbord))==NONE)
									huotwo2++;  /** ○__○ **/
							}
							break;
						}
						default:break;
						}
					}
				}
			}
	}
	
	private double getv(int estimateType){
		findTrait(estimateType);
		return  vfive*five+vhuofour*huofour+
				vhalffour*halffour+vsifour*sifour+
				vhuothree*huothree+vhalfthree*halfthree+
				vsithree*sithree+vhuotwo*huotwo+
				vhuotwo1*huotwo1+vhuotwo2*huotwo2;
	}
	public double getValue(){
		return getv(estimateType)*(1+coefficient)-getv(estimateType*(-1))*(1-coefficient);
	}
}
