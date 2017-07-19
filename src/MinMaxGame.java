import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MinMaxGame{

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

		MinMaxGame gameOpening = new MinMaxGame();
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
			char Coin;
			//sSystem.out.println("number of characters are "+ line.length());
			if(line.length()>=23){
				for(int i=0;i<23;i++){
					Coin = line.charAt(i);
					if(Coin=='W' ||Coin=='w' ){
						inBoard[i] = 'W';
						inWhiteCount++;
					}else if(Coin=='B' ||Coin=='b'){
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
				System.out.println("White lost the game .. !!");
			}
			if(inBlackCount<3){
				System.out.println("Black lost the game .. !!");
			}
			root =new TreeNode();
			root.setDepth(0);
			root.setBoard(getBoardCopy(inBoard));
			buildMoveTree(root);


			//System.out.println("Printing Tree");
			//printTreeNode(root);

			System.out.print("Input board is: ");
			System.out.print(inBoard);
			System.out.println();

			System.out.print("Board Position: ");
			for(int i=0;i<23;i++){
				System.out.print(root.getBoard()[i]);
			}
			System.out.println();

			System.out.println("Positions evaluated by static estimation:"+nodesEvaluated);
			System.out.println("MINIMAX estimate: "+root.getStatEst());

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
		//System.out.println("inside print board printing original board");
		//printBoard(board);
		for(int i=0;i<23;i++){
			copy[i] = board[i];
		}

		//System.out.println("printing copy of board ");
		//printBoard(copy);
		return copy;
	}
	public void buildMoveTree(TreeNode node){
		char Coin;
		int whiteCount=0,blackCount=0;
		ArrayList<Integer> whitePosition = new ArrayList<Integer>();
		ArrayList<Integer> blackPosition = new ArrayList<Integer>();
		ArrayList<Integer> itrPosition = new ArrayList<Integer>();

		// System.out.println("looking for empty spaces");
		for(int i = 0; i < 23; i++) {
			Coin = node.getBoard()[i];
			//System.out.println("Coin is "+ Coin + " node value "+node.getBoard()[i]);
			if(Coin=='x' || Coin=='X' || Coin==' '){
			}else if(Coin=='w' || Coin=='W'){
				whiteCount++;
				whitePosition.add(i);
			}else if(Coin=='b' || Coin=='B'){
				blackCount++;
				blackPosition.add(i);
			}

		}

		//System.out.println("Empty spaces are " + emptyPosition);

		//System.out.println("inside buildTree depth " + node.getDepth());
		if(node.getDepth() == treeDepth || whiteCount <=2 || blackCount <=2){
			//System.out.println("inside if ");
			getMidStatEst(node);
			nodesEvaluated++;
			//System.out.println("Static Estimate is " + node.getStatEst());
		}else{
			double statEst;

			//System.out.println("inside else ");
			ArrayList<TreeNode> childs = new ArrayList<TreeNode>();
			ArrayList<char[]> allBoards = new ArrayList<char[]>();
			char[] bestBoard = null;


			if(node.getDepth()%2==0){
				Coin='W';
				itrPosition = whitePosition;
				statEst=-1000000000;
			}else{
				Coin='B';
				itrPosition=blackPosition;
				statEst=1000000000;
			}


			for(int i=0;i<itrPosition.size();i++){
				allBoards = new ArrayList<char[]>();


				generateMove(Coin,node.getBoard(),itrPosition.get(i),allBoards);

				//System.out.println("all Boards size is " + allBoards.size());

				for(int j=0;j<allBoards.size();j++){

					//building tree
					TreeNode newNode = new TreeNode();
					newNode.setDepth(node.getDepth()+1);
					newNode.setParent(node);


					newNode.setBoard(allBoards.get(j));
					//System.out.println("Add new board to child " + allBoards.get(j));
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


			if(childs.size() == 0 ){
				//System.out.println("No more boards to generate");
				statEst =  -10000;
				node.setStatEval(true);
			}

			node.setChilds(childs);
			node.setStatEst(statEst);
			if(node.getDepth() ==0){
				//node.setPositionex(bestPosition);
				node.setBoard(bestBoard);
			}
		}



	}

	public ArrayList<Integer> getNeighbour(char[] board,int Position){
		ArrayList<Integer> my_neighbor = new ArrayList<Integer>();


		switch(Position){

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


	public void generateMove(char Coin,char[] board,int Position,ArrayList<char[]> allBoard){
		char[] newBoard;
		char[] tempBoard;
		ArrayList<Integer> NeighborPosition;

		char temp;
		ArrayList<Integer> emptyPosition = new ArrayList<Integer>();
		int whiteCount=0,blackCount=0;
		//System.out.println("looking for empty spaces");
		for(int i = 0; i < 23; i++) {
			temp = board[i];
			//System.out.println("Coin is "+ Coin + " node value "+node.getBoard()[i]);
			if(temp=='x' || temp=='X' || temp==' '){
				emptyPosition.add(i);
			}else if(temp=='w' || temp=='W'){
				whiteCount++;
			}else if(temp=='b' || temp=='B'){
				blackCount++;
			}

		}

		//System.out.println("Empty spaces are " + emptyPosition);
		// Check whether its End Game
		if(whiteCount < 3){
			return;
		}
		//Generate HOPPING //End Game
		else if((whiteCount == 3 && Coin =='W') || (blackCount == 3 && Coin =='B') ){
			NeighborPosition = emptyPosition;
		}
		else
		{
			NeighborPosition = getNeighbour(board,Position);

		}
		for(int j=0;j<NeighborPosition.size();j++){

			if(board[NeighborPosition.get(j)] == 'x'){
				newBoard = getBoardCopy(board);

				//Move Coin to Neighbor position
				newBoard[NeighborPosition.get(j)] = Coin; //Put Coin in the neighbour empty position
				newBoard[Position] = 'x'; //Delete Coin from the Actual Position


				//GenerateRemove
				if(isCloseMill(NeighborPosition.get(j),newBoard)){
					for(int i=0;i<23;i++){
						if(newBoard[i]!=Coin && newBoard[i]!='x'){
							//System.out.println("Mill Done");
							tempBoard = getBoardCopy(newBoard);
							if(!isCloseMill(i,tempBoard)){
								tempBoard[i] = 'x';
								//System.out.println("Removing Position " + i);
								allBoard.add(tempBoard);

							}else{

								int tempWhite = 0;
								int tempBlack = 0;

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

								if((tempBlack == 3 && Coin=='W')||(tempWhite == 3 && Coin=='B') ){
									tempBoard[i] = 'x';
									//System.out.println("Removing Position " + i);
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
		char Coin;
		int statEst;
		int blackMovesNo=0;
		ArrayList<char[]> allBoards;

		ArrayList<Integer>blackPosition = new ArrayList<Integer>();


		for(int i = 0; i < 23; i++) {
			Coin = node.getBoard()[i];
			if(Coin=='W' ||Coin=='w' ){
				whites++;
			}else if(Coin=='B' ||Coin=='b'){
				blacks++;
				blackPosition.add(i);
			}

		}

		allBoards = new ArrayList<char[]>();
		for(int i=0;i<blackPosition.size();i++){
			generateMove('B',node.getBoard(),blackPosition.get(i),allBoards);
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
