import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MinMaxOpening {
	char [] inBoard = new char [23]; //Array to store the moves given in the input file
	char [] outBoard = new char [23]; //Array to store the moves after calculating the output sequence
	String inPath; // To store the file path of input file
	String outPath;	//To store file path of output file
	int treeDepth=0; //input by the user
	int inWhiteCount=0; //To keep track of white coins in the game
	int inBlackCount=0; //To keep track of black coins in the game
	TreeNode root;
	int nodesEvaluated=0; //How many nodes evaluated
	
	public static void main(String[] args){

		MinMaxOpening gameOpening = new MinMaxOpening(); 
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
			File inFile = new File(inPath); //Read the input from the input File
			Scanner reader = new Scanner(inFile);
			String line = reader.nextLine();
			char c;
			if(line.length()>=23){
				for(int i=0;i<23;i++){
					c = line.charAt(i);
					if(c=='W' ||c=='w' ){
						inBoard[i] = 'W'; //Fill the inboard array with the sequence provided
						inWhiteCount++; // Keep track of number of White coins
					}else if(c=='B' ||c=='b'){
						inBoard[i] = 'B';
						inBlackCount++;
					}else{
						inBoard[i] = 'x'; // Fill remaining board with 'X'
					}
				}
			}else{
				System.out.println("Please enter all places in board");
			}

			System.out.println("Printing input board");
			
			
			root =new TreeNode();
			root.setDepth(0);
			root.setBoard(getBoardCopy(inBoard)); //set the array with inboard array values
			buildAddTree(root); //Build the tree

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
		for(int i=0;i<23;i++){
			copy[i] = board[i];
		}
		return copy;
	}
	
	public void buildAddTree(TreeNode node){
		char c;
		ArrayList<Integer> emptyInd = new ArrayList<Integer>();
		int whiteCount=0,blackCount=0;
		for(int i = 0; i < 23; i++) {
			c = node.getBoard()[i]; // Get the position (W/B/X) at ith 
			if(c=='x' || c=='X' || c==' '){
				emptyInd.add(i); //Count Empty index
			}else if(c=='w' || c=='W'){
				whiteCount++; //Count number of whites in the tree
			}else if(c=='b' || c=='B'){
				blackCount++; //Count number of Black coins in the tree
			}

		}
		//If its the leaf node 
		if(node.getDepth() == treeDepth || whiteCount <0 || blackCount <0 ){
			getOpenStatEst(node); //Returns (whites-black) to node.setStatEst
			nodesEvaluated++; //Keep track of nodes evaluated
			/*for(int i = 0; i < 23; i++) { 
				System.out.print(node.getBoard()[i]);
			}
			System.out.println(); */
		}else{
			double statEst;
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
			// Of all the empty places available in the board find the best to add the next coin
			for(int i=0;i<emptyInd.size();i++){
				allBoards = new ArrayList<char[]>();
				generateAdd(c,node.getBoard(),emptyInd.get(i),allBoards);// c="W/B", Input Board, Empty Index position, New array list
				for(int j=0;j<allBoards.size();j++){

					//building tree
					TreeNode newNode = new TreeNode();
					newNode.setDepth(node.getDepth()+1);
					newNode.setParent(node);


					newNode.setBoard(allBoards.get(j));
					getOpenStatEst(newNode); //NEW
					//nodesEvaluated++;
					/* System.out.print(allBoards.get(j));
					System.out.println("-"+newNode.getStatEst()); */
					buildAddTree(newNode);

					if(node.getDepth()%2==0){
						if(newNode.getStatEst() > statEst){
							statEst = newNode.getStatEst(); 
							//System.out.println("-"+statEst);
							bestBoard = newNode.getBoard();
						}

					}else{
						if(newNode.getStatEst() < statEst){
							statEst = newNode.getStatEst(); 
							//System.out.println("-"+statEst);
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
				//node.setStatEst(statEst);
			}
		}



	}
	// Get c="W/B" , Get Board, Get Index (ind)
	public void generateAdd(char c,char[] board,int ind,ArrayList<char[]> allBoard){
		char[] newBoard;
		char[] tempBoard;
		newBoard = getBoardCopy(board); // Copy of the Input board sent 

		newBoard[ind] = c; // Place C="W/B" at Index of new board
		//If the newBoard[ind] is having two straight mills then
		if(isCloseMill(ind,newBoard))
		{
			for(int i=0;i<23;i++) // for each location in the board
			{
				//GENERATE REMOVE Function for other player's coin
				if(newBoard[i]!=c && newBoard[i]!='x')
				{
					tempBoard = getBoardCopy(newBoard);
					//If all the coins are not in Mills
					if(!isCloseMill(i,tempBoard)){
						tempBoard[i] = 'x';
						allBoard.add(tempBoard);
					}
					//If all the Coins of Other player are in Mills
					else
					{
						allBoard.add(tempBoard);
						/*int tempWhite = 0;
						int tempBlack = 0;
						char temp;
						
						//Search for all the whites and black coins in the board
						for(int k = 0; k < 23; k++) {
							temp = tempBoard[k];
							if(temp=='w' || temp=='W'){
								tempWhite++;
							}else if(temp=='b' || temp=='B'){
								tempBlack++;
							}

						}
						//Put X in the index i if putting a B or W in i forms a mill
						if((tempBlack == 3 && c=='W')||(tempWhite == 3 && c=='B') )
						{
							tempBoard[i] = 'x';
							allBoard.add(tempBoard); // Add the whole board with 'X' stored at index to all Board
						}*/
					}
				}
			}
		}
		else
		{
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

		for(int i = 0; i < 23; i++) {
			c = node.getBoard()[i];
			if(c=='W' ||c=='w' ){
				whites++;
			}else if(c=='B' ||c=='b'){
				blacks++;
			}

		}
		//System.out.println(whites - blacks);
		node.setStatEst(whites - blacks);
	}

}
