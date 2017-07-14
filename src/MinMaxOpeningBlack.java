import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MinMaxOpeningBlack {

	char [] inBoard = new char [23];
	char [] outBoard = new char [23];
	String inPath;
	String outPath;	
	int treeDepth=0;
	int inWhiteCount=0;
	int inBlackCount=0;
	TreeNode root;
	int nodesEvaluated=0;

	public static void main(String[] args){

		MinMaxOpeningBlack gameOpening = new MinMaxOpeningBlack(); 
		if(args.length>1){
			gameOpening.addCoin(args); // Call to AddCoin Function
		}else{
			System.out.println("Please enter both input and output file names and re-execute the program");
		}
	}

	public void addCoin(String [] args){
			inPath = args[0];
			outPath = args[1];
			treeDepth = Integer.parseInt(args[2]);
		try{
			File inFile = new File(inPath);
			Scanner reader = new Scanner(inFile);
			String line = reader.nextLine();
			char c;
			//sSystem.out.println("number of characters are "+ line.length());
			if(line.length()>=23){
				for(int i=0;i<23;i++){
					c = line.charAt(i);
					if(c=='W' ||c=='w' ){
						inBoard[i] = 'W';
						inWhiteCount++;
					}else if(c=='B' ||c=='b'){
						inBoard[i] = 'B';
						inBlackCount++;
					}else{
						inBoard[i] = 'x';
					}
				}
			}else{
				System.out.println("Please enter all places in board");
			}

			char [] tempBoard = getBoardCopy(inBoard);
			for(int i=0;i<23;i++){
				c = tempBoard[i];
				if(c=='W' ||c=='w' ){
					tempBoard[i] = 'B';
				}else if(c=='B' ||c=='b'){
					tempBoard[i] = 'W';
				}
			}

			//printBoard(tempBoard);
			root =new TreeNode();
			root.setDepth(0);

			root.setBoard(tempBoard);

			//System.out.println("input board black");
			//printBoard(tempBoard);

			buildAddTree(root);

			outBoard = getBoardCopy(root.getBoard());
			//System.out.println("Board before Swapping");
			//printBoard(tempBoard);
			for(int i=0;i<23;i++){
				c = outBoard[i];
				if(c=='W' ||c=='w' ){
					outBoard[i] = 'B';
				}else if(c=='B' ||c=='b'){
					outBoard[i] = 'W';
				}
			}

			//root.setBoard(tempBoard);

			System.out.print("Input board is: ");
			System.out.print(inBoard);
			System.out.println();

			System.out.print("Board Position: ");
			for(int i=0;i<23;i++){
				System.out.print(outBoard[i]);
			}
			System.out.println();

			System.out.println("Positions evaluated by static estimation:"+nodesEvaluated);
			System.out.println("MINIMAX estimate: "+root.getStatEst());


			writeToFile(outBoard);

		}catch (IOException exp){
			System.out.println("Exception Occurred !! ");
			exp.printStackTrace();	
		}

	}


	public void writeToFile(char[] board){

		File outFile =  new File(outPath);
		String strContent  = "";


		for(int i=0;i<23;i++){
			strContent += board[i];
		}
		try{

			if(!outFile.exists())
				outFile.createNewFile();

			FileOutputStream outStream = new FileOutputStream(outPath);

			outStream.write(strContent.getBytes());
			outStream.close();

		}catch(IOException exp){
			System.out.println("Exception Occurred");
			exp.printStackTrace();
		}
	}


	char[] getBoardCopy(char[] board){
		char [] copy = new char [23];
		//System.out.println("inside print board printing original board");
		//printBoard(board);
		for(int i=0;i<23;i++){
			copy[i] = board[i];
		}

		//System.out.println("printing copy of board ");
		//printBoard(copy);
		return copy;
	}
	public void buildAddTree(TreeNode node){
		char c;
		ArrayList<Integer> emptyInd = new ArrayList<Integer>();
		int whiteCount=0,blackCount=0;
		// System.out.println("looking for empty spaces");
		for(int i = 0; i < 23; i++) {
			c = node.getBoard()[i];
			//System.out.println("c is "+ c + " node value "+node.getBoard()[i]);
			if(c=='x' || c=='X' || c==' '){
				emptyInd.add(i);
			}else if(c=='w' || c=='W'){
				whiteCount++;
			}else if(c=='b' || c=='B'){
				blackCount++;
			}

		}

		//System.out.println("Empty spaces are " + emptyInd);
		//System.out.println("inside buildTree depth " + node.getDepth());
		if(node.getDepth() == treeDepth || whiteCount <0 || blackCount <0 ){
			//System.out.println("inside if ");
			getOpenStatEst(node);
			nodesEvaluated++;
			//System.out.println("Static Estimate is " + node.getStatEst());
		}else{
			double statEst;
			//System.out.println("inside else ");
			ArrayList<TreeNode> childs = new ArrayList<TreeNode>();
			ArrayList<char[]> allBoards = new ArrayList<char[]>();
			char[] bestBoard = null;

			if(node.getDepth()%2==0){
				c='W';
				statEst=-1000000000;
			}else{
				c='B';
				statEst=1000000000;
			}

			for(int i=0;i<emptyInd.size();i++){
				allBoards = new ArrayList<char[]>();

				generateAdd(c,node.getBoard(),emptyInd.get(i),allBoards);

				//System.out.println("all Boards size is " + allBoards.size());

				for(int j=0;j<allBoards.size();j++){

					//building tree
					TreeNode newNode = new TreeNode();
					newNode.setDepth(node.getDepth()+1);
					newNode.setParent(node);


					newNode.setBoard(allBoards.get(j));
					getOpenStatEst(newNode); //NEW
					//System.out.println("Add new board to child " + allBoards.get(j));
					//System.out.print(allBoards.get(j));
					//System.out.println("-"+newNode.getStatEst()); 
					buildAddTree(newNode);

					if(node.getDepth()%2==0){
						if(newNode.getStatEst() > statEst){
							statEst = newNode.getStatEst(); 
							bestBoard = newNode.getBoard();
						}

					}else{
						if(newNode.getStatEst() < statEst){
							statEst = newNode.getStatEst(); 
							bestBoard = newNode.getBoard();
						}
					}

					childs.add(newNode);

				}
			}


			node.setChilds(childs);
			node.setStatEst(statEst);
			if(node.getDepth() ==0){
				//node.setIndex(bestInd);
				node.setBoard(bestBoard);
				//node.setStatEst(statEst); //NEW
			}
		}



	}

	public void generateAdd(char c,char[] board,int ind,ArrayList<char[]> allBoard){
		char[] newBoard;
		char[] tempBoard;
		newBoard = getBoardCopy(board);

		newBoard[ind] = c;
		//System.out.println("Placing " + c + " at ind " + ind);

		if(isCloseMill(ind,newBoard)){
			for(int i=0;i<23;i++){
				if(newBoard[i]!=c && newBoard[i]!='x'){
					tempBoard = getBoardCopy(newBoard);
					if(!isCloseMill(i,tempBoard)){
						tempBoard[i] = 'x';
						allBoard.add(tempBoard);
					}else{

						int tempWhite = 0;
						int tempBlack = 0;
						char temp;

						tempWhite = 0;
						tempBlack = 0;

						for(int k = 0; k < 23; k++) {
							temp = tempBoard[k];
							if(temp=='w' || temp=='W'){
								tempWhite++;
							}else if(temp=='b' || temp=='B'){
								tempBlack++;
							}

						}

						if((tempBlack == 3 && c=='W')||(tempWhite == 3 && c=='B') ){
							tempBoard[i] = 'x';
							//System.out.println("Removing ind " + i);
							allBoard.add(tempBoard);
						}

						//System.out.println("mill encountered so not removing");

					}
				}
			}


		}else{
			allBoard.add(newBoard);
		}

	}

	public boolean isCloseMill(int Position,char[] board){
		boolean isMill = false;
		switch(Position){

		case 0: if(board[Position] == board[1] && board[Position] == board[2] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[8] && board[Position] == board[20] && board[Position]!='x'){
			isMill = true;
		}else if(board[Position] == board[3] && board[Position] == board[6] && board[Position]!='x'){
			isMill = true;
		}
		break;

		case 1: if(board[Position] == board[0] && board[Position] == board[2] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 2: if(board[Position] == board[0] && board[Position] == board[1] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[13] && board[Position] == board[22] && board[Position]!='x'){
			isMill = true;
		}else if(board[Position] == board[5] && board[Position] == board[7] && board[Position]!='x'){
			isMill = true;
		}
		break;

		case 3: if(board[Position] == board[0] && board[Position] == board[6] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[4] && board[Position] == board[5] && board[Position]!='x'){
			isMill = true;
		}else if(board[Position] == board[9] && board[Position] == board[17] && board[Position]!='x'){
			isMill = true;
		}
		break;

		case 4: if(board[Position] == board[3] && board[Position] == board[5] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 5: if(board[Position] == board[7] && board[Position] == board[2] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[3] && board[Position] == board[4] && board[Position]!='x'){
			isMill = true;
		}else if(board[Position] == board[12] && board[Position] == board[19] && board[Position]!='x'){
			isMill = true;
		}
		break;

		case 6: if(board[Position] == board[10] && board[Position] == board[14] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[0] && board[Position] == board[3] && board[Position]!='x'){
			isMill =true;
		}
		break;	

		case 7: if(board[Position] == board[11] && board[Position] == board[16] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[2] && board[Position] == board[5] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 8: if(board[Position] == board[9] && board[Position] == board[10] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[0] && board[Position] == board[20] && board[Position]!='x'){
			isMill = true;
		}
		break;

		case 9: if(board[Position] == board[3] && board[Position] == board[17] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[8] && board[Position] == board[10] && board[Position]!='x'){
			isMill = true;
		}
		break;

		case 10: if(board[Position] == board[8] && board[Position] == board[9] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[6] && board[Position] == board[14] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 11: if(board[Position] == board[12] && board[Position] == board[13] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[7] && board[Position] == board[16] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 12: if(board[Position] == board[11] && board[Position] == board[13] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[5] && board[Position] == board[19] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 13: if(board[Position] == board[11] && board[Position] == board[12] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[2] && board[Position] == board[22] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 14: if(board[Position] == board[15] && board[Position] == board[16] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[17] && board[Position] == board[20] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[6] && board[Position] == board[10] && board[Position]!='x'){
			isMill =true;
		}
		break;		

		case 15: if(board[Position] == board[18] && board[Position] == board[21] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[14] && board[Position] == board[16] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 16: if(board[Position] == board[7] && board[Position] == board[11] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[19] && board[Position] == board[22] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[14] && board[Position] == board[15] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 17: if(board[Position] == board[3] && board[Position] == board[9] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[14] && board[Position] == board[20] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[18] && board[Position] == board[19] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 18: if(board[Position] == board[15] && board[Position] == board[21] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[17] && board[Position] == board[19] && board[Position]!='x'){
			isMill =true;
		}
		break;	

		case 19: if(board[Position] == board[5] && board[Position] == board[12] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[16] && board[Position] == board[22] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[17] && board[Position] == board[18] && board[Position]!='x'){
			isMill =true;
		}
		break;


		case 20: if(board[Position] == board[0] && board[Position] == board[8] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[14] && board[Position] == board[17] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[21] && board[Position] == board[22] && board[Position]!='x'){
			isMill =true;
		}
		break;

		case 21: if(board[Position] == board[15] && board[Position] == board[18] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[20] && board[Position] == board[22] && board[Position]!='x'){
			isMill =true;
		}
		break;	

		case 22: if(board[Position] == board[2] && board[Position] == board[13] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[16] && board[Position] == board[19] && board[Position]!='x'){
			isMill =true;
		}else if(board[Position] == board[20] && board[Position] == board[21] && board[Position]!='x'){
			isMill =true;
		}
		break;


		}
		return isMill;
	}


	public void getOpenStatEst(TreeNode node){
		int whites =0;
		int blacks =0;
		char c;
		//System.out.print("Inside static est board is " );
		/*for(int i=0;i<23;i++){
		System.out.print("*"+node.getBoard()[i]);
	}
	System.out.println();
		 */

		for(int i = 0; i < 23; i++) {
			c = node.getBoard()[i];
			//System.out.println("c is " +c);
			if(c=='W' ||c=='w' ){
				whites++;
			}else if(c=='B' ||c=='b'){
				blacks++;
			}

		}
		//System.out.println("Whites :"+whites + "Blacks :"+blacks);
		node.setStatEst(whites - blacks);
	}


}