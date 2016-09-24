package com.example.myfive;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

import android.graphics.Point;
import android.util.Log;

public class AIplayer {
	private Estimate es;
	private Chessboard cb,cbtemp;
	private int aiChestype;
	class Node{
		public int height;
		public boolean isMax;
		double value;
		public Node(int height, boolean isMax) {
			this.height = height;
			this.isMax = isMax;
			value=isMax?Double.NEGATIVE_INFINITY:Double.POSITIVE_INFINITY;
		}
		
	}
	class  Prepoint extends Point implements Comparable<Prepoint>{
		
		public double weight=0;
		public Prepoint(int x,int y){
			super(x, y);
			cbtemp.setChess(this,aiChestype);
			es=new Estimate(cbtemp, aiChestype,true);
			weight=es.getValue();
			cbtemp.back(1);
		}

		@Override
		public int compareTo(Prepoint another) {
			return Double.valueOf(weight).compareTo(Double.valueOf(another.weight));
		}
	}
	private LinkedList<Prepoint> poilist;
	public AIplayer(Chessboard cb, int aiChestype) {
		this.cb = cb;
		this.aiChestype = aiChestype;
		poilist= new LinkedList<AIplayer.Prepoint>();
	}
	public void reverseType(){
		aiChestype=aiChestype*(-1);
	}
	public Point move(int deep){
		Prepoint prep = null;
		cbtemp=null;
		try {
			cbtemp=(Chessboard) cb.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		if(cb.chessNum()==0)
			prep=new Prepoint(Configure.col/2, Configure.row/2);
		else if(cb.chessNum()==1){
			Point p=cb.back(1);
			int dir=((int)(Math.random()*80))/10;
			Point pt=Estimate.getAbs(p, dir, 1, cb);
			prep= new Prepoint(pt.x, pt.y);
			cb.setChess(p, aiChestype*(-1));
		}
		else{
			for(int i=0;i<cb.row;i++)
				for(int j=0;j<cb.col;j++)
					if(cb.getType(new Point(j, i))==0)
						poilist.add(new Prepoint(j,i));
			Collections.sort(poilist, Collections.reverseOrder());
			prep=search(new Node(deep, true), new Node(deep+1, false),poilist);
			poilist.clear();
		}
		cb.setChess(prep, aiChestype);
		return prep;
	}
	@SuppressWarnings("unchecked")
	public Prepoint search(Node node,Node nParent,LinkedList<Prepoint> searchlist){
		if(node.height==0){
			es=new Estimate(cbtemp, aiChestype,node.isMax);
			node.value=es.getValue();
			if((node.isMax && node.value<nParent.value)||
					(!node.isMax && node.value>nParent.value))
				nParent.value=node.value;
			return null;
		}else{
			Prepoint repoint = null;
			double vtemp=node.value;
			ListIterator<Prepoint> ite= searchlist.listIterator();
			while(ite.hasNext()){
				if((node.value>=nParent.value && node.isMax) ||
						(node.value<=nParent.value && !node.isMax))
					return null;
				Prepoint delpoint=ite.next();
				ite.remove();
				cbtemp.setChess(delpoint, node.isMax?aiChestype:aiChestype*(-1));
				search(new Node(node.height-1, !node.isMax),node,(LinkedList<Prepoint>)searchlist.clone());
				cbtemp.back(1);
				ite.add(delpoint);
				if((node.isMax && node.value>vtemp) || 
						(!node.isMax && node.value<vtemp)){
					repoint=delpoint;
					vtemp=node.value;
				}
			}
			nParent.value=vtemp;
			return repoint;
		}
	}
}
