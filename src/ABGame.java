import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ABGame{

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

		ABGame gameOpening = new ABGame(); 
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

			if(inWhiteCount<3){
				System.out.println("Sorry dude you lost the game .. !!");
			}

			root =new TreeNode();
			root.setDepth(0);
			root.setBoard(getBoardCopy(inBoard));
			buildMoveTree(root);
			applyABMinMax(root);

			System.out.print("Input board is: ");
			System.out.print(inBoard);
			System.out.println();

			System.out.print("Board Position: ");
			for(int i=0;i<23;i++){
				System.out.print(root.getBoard()[i]);
			}
			System.out.println();

			System.out.println("Positions evaluated by static estimation:"+nodesEvaluated);
			System.out.println("Alpha-Beta MINIMAX estimate: "+root.getStatEst());


			writeToFile(root.getBoard());

		}catch (IOException e){
			System.out.println("Exception Occurred !! ");
			e.printStackTrace();	
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
		for(int i=0;i<23;i++){
			copy[i] = board[i];
		}
		return copy;
	}

	public void applyABMinMax(TreeNode node){
		if(node.getChilds() != null &&node.getChilds().size()>0){

			if(node.isStatEval()){

				if(node.getDepth()%2==0){
					if(node.getStatEst() < node.getParent().getStatLess() ){
						node.getParent().setStatLess(node.getStatEst());
					}
				}else{
					if(node.getStatEst() > node.getParent().getStatGreat() ){
						node.getParent().setStatGreat(node.getStatEst());
					}
				}


				node.setStatEval(true);
				return;
			}else{
				for(int i=0;i<node.getChilds().size();i++){


					applyABMinMax(node.getChilds().get(i));


					if(node.getDepth()!=0){
						if(node.getDepth()%2==0 ){
							if(node.getStatGreat()>node.getParent().getStatLess()){
								return;
							}
						}else{

							if(node.getStatLess()<node.getParent().getStatGreat()){
								return;
							}


						}
					}	
				}	


				if(node.getDepth()%2==0 ){

					node.setStatEst(node.getStatGreat());
					node.setStatEval(true);

					if(node.getDepth()!=0){
						if(node.getStatEst() < node.getParent().getStatLess() ){
							node.getParent().setStatLess(node.getStatEst());
						}
					}	

				}else{

					node.setStatEst(node.getStatLess());
					node.setStatEval(true);

					if(node.getDepth()!=0){
						if(node.getStatEst() > node.getParent().getStatGreat() ){
							node.getParent().setStatGreat(node.getStatEst());
						}
					}	

				}

			}

			if(node.getDepth()==0){
				for(int i=0;i<node.getChilds().size();i++){
					if(node.getChilds().get(i).getStatEst() == node.getStatEst()){
						node.setBoard(node.getChilds().get(i).getBoard());
						break;
					}
				}
			}



		}else{
			getMidStatEst(node);
			if(node.getDepth()%2==0){
				if(node.getStatEst() < node.getParent().getStatLess() ){
					node.getParent().setStatLess(node.getStatEst());
				}
			}else{
				if(node.getStatEst() > node.getParent().getStatGreat() ){
					node.getParent().setStatGreat(node.getStatEst());
				}
			}


			nodesEvaluated++;
		}
	}


	public void buildMoveTree(TreeNode node){
		char c;
		int whiteCount=0,blackCount=0;
		ArrayList<Integer> whiteInd = new ArrayList<Integer>();
		ArrayList<Integer> blackInd = new ArrayList<Integer>();
		ArrayList<Integer> itrInd = new ArrayList<Integer>();
		for(int i = 0; i < 23; i++) {
			c = node.getBoard()[i];
			if(c=='x' || c=='X' || c==' '){
			}else if(c=='w' || c=='W'){
				whiteCount++;
				whiteInd.add(i);
			}else if(c=='b' || c=='B'){
				blackCount++;
				blackInd.add(i);
			}

		}
		if(node.getDepth() == treeDepth || whiteCount <=2 || blackCount <=2){
		}else{
			double statEst;
			ArrayList<TreeNode> childs = new ArrayList<TreeNode>();
			ArrayList<char[]> allBoards = new ArrayList<char[]>();
			char[] bestBoard = null;


			if(node.getDepth()%2==0){
				c='W';
				itrInd = whiteInd;
				statEst=-1000000000;
			}else{
				c='B';
				itrInd=blackInd;
				statEst=1000000000;
			}


			for(int i=0;i<itrInd.size();i++){
				allBoards = new ArrayList<char[]>();


				generateMove(c,node.getBoard(),itrInd.get(i),allBoards);

				for(int j=0;j<allBoards.size();j++){

					//building tree
					TreeNode newNode = new TreeNode();
					newNode.setDepth(node.getDepth()+1);
					newNode.setParent(node);


					newNode.setBoard(allBoards.get(j));
					buildMoveTree(newNode);

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
				node.setBoard(bestBoard);
			}
		}



	}

	public ArrayList<Integer> getNeighbour(char[] board,int ind){
		ArrayList<Integer> my_neighbor = new ArrayList<Integer>();


		switch(ind){

		case 0: my_neighbor.add(1);
		my_neighbor.add(3);
		my_neighbor.add(8);
		break;

		case 1: my_neighbor.add(0);
		my_neighbor.add(2);
		my_neighbor.add(4);
		break;

		case 2: my_neighbor.add(1);
		my_neighbor.add(5);
		my_neighbor.add(13);
		break;

		case 3: my_neighbor.add(0);
		my_neighbor.add(4);
		my_neighbor.add(6);
		my_neighbor.add(9);
		break;

		case 4: my_neighbor.add(1);
		my_neighbor.add(3);
		my_neighbor.add(5);
		break;

		case 5: my_neighbor.add(2);
		my_neighbor.add(4);
		my_neighbor.add(7);
		my_neighbor.add(12);
		break;

		case 6: my_neighbor.add(3);
		my_neighbor.add(7);
		my_neighbor.add(10);
		break;	

		case 7: my_neighbor.add(5);
		my_neighbor.add(6);
		my_neighbor.add(11);
		break;

		case 8: my_neighbor.add(0);
		my_neighbor.add(9);
		my_neighbor.add(20);
		break;

		case 9: my_neighbor.add(3);
		my_neighbor.add(8);
		my_neighbor.add(10);
		my_neighbor.add(17);
		break;

		case 10:my_neighbor.add(6);
		my_neighbor.add(9);
		my_neighbor.add(14);	
		break;

		case 11:my_neighbor.add(7);
		my_neighbor.add(12);
		my_neighbor.add(16);
		break;

		case 12:my_neighbor.add(5);
		my_neighbor.add(11);
		my_neighbor.add(13);
		my_neighbor.add(19);
		break;

		case 13:my_neighbor.add(2);
		my_neighbor.add(12);
		my_neighbor.add(22);
		break;

		case 14:my_neighbor.add(10);
		my_neighbor.add(15);
		my_neighbor.add(17);
		break;		

		case 15:my_neighbor.add(14);
		my_neighbor.add(16);
		my_neighbor.add(18);
		break;

		case 16:my_neighbor.add(11);
		my_neighbor.add(15);
		my_neighbor.add(19);
		break;

		case 17:my_neighbor.add(9);
		my_neighbor.add(14);
		my_neighbor.add(18);
		my_neighbor.add(20);
		break;

		case 18:my_neighbor.add(15);
		my_neighbor.add(17);
		my_neighbor.add(19);
		my_neighbor.add(21);
		break;	

		case 19:my_neighbor.add(12);
		my_neighbor.add(16);
		my_neighbor.add(18);
		my_neighbor.add(22);
		break;


		case 20:my_neighbor.add(8);
		my_neighbor.add(17);
		my_neighbor.add(21);
		break;

		case 21:my_neighbor.add(18);
		my_neighbor.add(20);
		my_neighbor.add(22);
		break;	

		case 22:my_neighbor.add(13);
		my_neighbor.add(19);
		my_neighbor.add(21);
		break;

		}



		return my_neighbor;
	}


	public void generateMove(char c,char[] board,int ind,ArrayList<char[]> allBoard){
		char[] newBoard;
		char[] tempBoard;
		ArrayList<Integer> NighInd;
		int tempWhite = 0;
		int tempBlack = 0;

		char temp;
		ArrayList<Integer> emptyInd = new ArrayList<Integer>();
		int whiteCount=0,blackCount=0;
		for(int i = 0; i < 23; i++) {
			temp = board[i];
			if(temp=='x' || temp=='X' || temp==' '){
				emptyInd.add(i);
			}else if(temp=='w' || temp=='W'){
				whiteCount++;
			}else if(temp=='b' || temp=='B'){
				blackCount++;
			}

		}

		if(whiteCount < 3){
			return;
		}else if((whiteCount == 3 && c =='W') || (blackCount == 3 && c =='B') ){
			NighInd = emptyInd;
		}else{

			NighInd = getNeighbour(board,ind);

		}
		for(int j=0;j<NighInd.size();j++){

			if(board[NighInd.get(j)] == 'x'){	
				newBoard = getBoardCopy(board);

				newBoard[NighInd.get(j)] = c;
				newBoard[ind] = 'x';
				if(isCloseMill(NighInd.get(j),newBoard)){
					for(int i=0;i<23;i++){
						if(newBoard[i]!=c && newBoard[i]!='x'){
							//System.out.println("Mill Done");
							tempBoard = getBoardCopy(newBoard);
							if(!isCloseMill(i,tempBoard)){
								tempBoard[i] = 'x';
								//System.out.println("Removing ind " + i);
								allBoard.add(tempBoard);

							}else{
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


	public void getMidStatEst(TreeNode node){
		int whites =0;
		int blacks =0;
		char c;
		int statEst;
		int blackMovesNo=0;
		ArrayList<char[]> allBoards;

		ArrayList<Integer>blackInd = new ArrayList<Integer>();


		for(int i = 0; i < 23; i++) {
			c = node.getBoard()[i];
			if(c=='W' ||c=='w' ){
				whites++;
			}else if(c=='B' ||c=='b'){
				blacks++;
				blackInd.add(i);
			}

		}

		allBoards = new ArrayList<char[]>();
		for(int i=0;i<blackInd.size();i++){
			generateMove('B',node.getBoard(),blackInd.get(i),allBoards);
		}	

		blackMovesNo = allBoards.size();

		if(blacks<=2){
			statEst = 10000;
		}else if(whites<=2){
			statEst = -10000; 
		}else if(blackMovesNo ==0){
			statEst = 10000;
		}else{
			statEst = 1000*(whites - blacks);
			statEst = statEst - blackMovesNo;
		}
		node.setStatEst(statEst);
	}

}