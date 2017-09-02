package com.example.mohamed.sudoku;

import java.util.*;


/**
 * Created by Mohamed on 15/05/2017.
 * This class is for sudoku solving
 * @author M. Sana
 */
public class SudokuSolver{
	private int[][] grid;
	//private Iterator<EmptyBox> [][] itGrid;
	private ArrayList<EmptyBox> alist;
	
	
	/**
	* This constructor help create and instance of a sudoku solver
	* @parm grid the grid you want to solve
	*/
	public SudokuSolver(int[][] grid){
		this.grid = grid.clone(); //clone in order to don't write on the empty grid. I'll need the grid
		for(int i = 0; i < grid.length; i++){
			this.grid[i] = grid[i].clone();
		}
	}
	
	/**
	* Return the grid once solved
	* @return the solved grid
	*/
	public int[][] getGrid(){
		return this.grid;
	}
	
	
	/**
	* This method test if the specified value <val> exist on the specified <grid> line number <numLine>
	* @parm val the searched value
	* @parm grid the sudoku grid
	* @parm numLine the number of the line on which the search will be done
	* @return boolean true if val exist otherwise false
	*/
	public boolean isValuePresentOnLine(int val,int[][] grid, int numLine){
		for(int j = 0; j < grid.length; j++){
			if(grid[numLine][j] == val)
				return true;
		}
		
		return false;
	}
	
	
	/**
	* This method test if the specified value <val> exist on the specified <grid> column number <numCol>
	* @parm val the searched value
	* @parm grid the sudoku grid
	* @parm numCol the number of the column on which the search will be done
	* @return boolean true if val exist otherwise false
	*/
	
	public boolean isValuePresentOnColumn(int val,int[][] grid, int numCol){
		for(int i = 0; i < grid.length; i++){
			if(grid[i][numCol] == val)
				return true;
		}
		
		return false;
	}
	
	
	/**
	* This method test if the specified value <val> exist on the specified <grid> bloc number give by <k> and <l>
	* recall that the sudoku is divided in 9 blocs of 9 boxes and each bloc can be define by the coordinates of one of its box
	* @parm val the searched value
	* @parm grid the sudoku grid
	* @parm (k,l) a coordinate of a box that belongs to the bloc
	* @return boolean true if val exist otherwise false
	*/	
	public boolean isValuePresentOnBloc(int val, int[][] grid, int k, int l){
		//Sudoku grid have 9 blocs each bloc is localised by the coordinates of one of its box
		int m = k-(k%3), n = l-(l%3);
		for(int i = m; i < m+3; i++){
			for(int j = n; j < n+3; j++){
				if(grid[i][j] == val)
					return true;
			}
		}
		
		return false;
	}
	
	
	/**
	* For the sudoku resolution, the method used is backtracking. That is, for more efficiency each empty box will be stored in a list as an <EmptyBox>
	* Recall that in a sudoku grid, for a specific box there is a number of possible values that can filled this box according 
	* to what is already present on both line, column and bloc. And for more efficiency you must start filling the box with the minimun <numPossibleValues>
	* So the idea is: for each empty box, count the number of possible values and sort up all empty boxes according to this number 
	*/
	private class EmptyBox implements Comparable<EmptyBox>{

		private int i,j,numPossibleValues; // (i,j) the coordinate and numPossibleValues
		private EmptyBox next; // the next empty box in the list
		
		
		/**
		* Constructor of empty box
		* @parm (i,j) the coordinate of the boxes
		* @parm n the number of possible values
		*/
		public EmptyBox(int i, int j, int n){
			this.i = i;
			this.j = j;
			this.numPossibleValues = n;
		}
		
		/**
		* This method return the next empty box in the list after <this> if <this> exist otherwise null
		* @parm alist the list of empty boxes
		* @return the next element in alist after <this>
		*/
		public EmptyBox next(ArrayList<EmptyBox> alist){
			int i = alist.indexOf(this); //get the index of this
			if(i < 0) return null; //if <this> don't exist then i < 0 -> return null
			if(i < alist.size()-1) // if there is a next element return it otherwise return null
				return alist.get(i+1);
			return null;
		}
		
		/**
		* return the x-coordinate of the box
		*/
		public int getI(){return this.i;}
		
		/**
		* return the y-coordinate of the box
		*/
		public int getJ(){return this.j;}
		
		/**
		* return the number of possible values of the box
		*/
		public int getN(){return this.numPossibleValues;}
		
		
		/**
		* To sort, compareTo need to be redefine. Recall that elements are sorted up according to numPossibleValues
		*/
		public int compareTo(EmptyBox b){
			if(b == null) throw new NullPointerException();
			if(!(b.getI() == this.i && this.j == b.getJ())){
				if(this.numPossibleValues < b.getN()) return -1;
				else if(this.numPossibleValues > b.getN()) return 1;
				else return 1;
			}
			return 0;
		}
		
		
		/**
		* equals need to be redefine
		*/
		public boolean equals(Object o) {
			if(o instanceof EmptyBox == false) return false;
			EmptyBox b = (EmptyBox) o;
			return (this.i == b.getI() && this.j == b.getJ());
		}
		
		/**
		* hashCode need to be redefine
		* i*9+j is unique for each box (i,j) in the grid as the grid size is 9x9
		*/
		public int hashCode(){
			return this.i*9+this.j; // i*9+j is unique for each box in the grid as the grid size is 9x9 
		}
		
	}
	
	
	
	/**
	* This method take an empty box b and filled it with a number k between 1 and 9,
	* if k is not present in the line or column or bloc. For each k it fills b and recursively do the same with b.next(),
	* until the grid is completly filled
	* @parm grid
	* @parm b an empty box
	* @return boolean true if it succeed in filling the grid otherwise false
	*/
	public boolean isValid(int[][] grid, EmptyBox b){
		//each row in grid can be localized with a only one number :
		//position = i*length+j where i and j is the row coordinates
		if(b == null) return true;
		
		int i = b.getI(), j = b.getJ();

		for(int k =1; k<=9; k++){
			if(!isValuePresentOnLine(k,grid,i) && !isValuePresentOnColumn(k,grid,j) &&
			!isValuePresentOnBloc(k,grid,i,j)){
				grid[i][j] = k;
								
				if(isValid(grid,b.next(this.alist))) return true;
			}
		}
		
		grid[i][j] = 0; //in case it is not valid, don't forget to reset the box to let know it is still empty

		return false;		
	}
	
	
	/**
	* As its name let understand, this method count the number of possible values of a box
	* @parm grid empty grid
	* @parm (i,j) coordinate of the box
	* @return numPossibleValues
	*/
	public int numOfPossibleVal(int[][] grid,int i, int j){
		int m=0;
		for(int k =1; k <= 9; k++){
			if(!isValuePresentOnLine(k,grid,i) && !isValuePresentOnColumn(k,grid,j) &&
			!isValuePresentOnBloc(k,grid,i,j)){
				m++;
			}
		}
		return m;
	}
	
	
	/**
	* Solve the sudoku grid
	* @return boolean true if the grid is solvable and false otherwise
	*/
	public  boolean solve(){
		SortedSet<EmptyBox> list = new TreeSet<EmptyBox>();
		ArrayList<EmptyBox> alist = new ArrayList<EmptyBox>();
		
		//here I sort by adding boxes to SortedSet
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				if(this.grid[i][j] == 0){
					list.add(new EmptyBox(i,j,this.numOfPossibleVal(this.grid,i,j)));
					
				}
			}
		}
		Iterator<EmptyBox> position = list.iterator();
		
		//I copied it on and array list (the array list is then sorted and ready to be manipulated)
		while(position.hasNext()){
			EmptyBox b = position.next();
			alist.add(b);
		}
		this.alist = alist;
		//Iterator<EmptyBox> position = list.iterator();
		
		return isValid(this.grid,alist.get(0));
	}
}