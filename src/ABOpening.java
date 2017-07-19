 import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ABOpening {

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

		ABOpening gameOpening = new ABOpening(); 
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


			root =new TreeNode();
			root.setDepth(0);
			root.setBoard(getBoardCopy(inBoard));
			buildAddTree(root);


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

	public void applyABMinMax(TreeNode node)
	{
		//Not Leaf Nodes
		if(node.getChilds() != null &&node.getChilds().size()>0)
		{
			//Node Evaluated
			if(node.isStatEval())
			{
				//MAXMIN
				if(node.getDepth()%2==0) //MAX Level
				{
					if(node.getStatEst() < node.getParent().getStatLess() ){
						//Parent will be at MIN level.So pick the min value among children
						node.getParent().setStatLess(node.getStatEst());
					}
				}
				//MINMAX
				else //MIN Level
				{
					if(node.getStatEst() > node.getParent().getStatGreat() ){
						//Parent will be at MAX level.So pick the MAX value among children
						node.getParent().setStatGreat(node.getStatEst());
					}
				}
				node.setStatEval(true);
				return;
			}
			//Node Not Evaluated
			else
			{
				for(int i=0;i<node.getChilds().size();i++)
				{	
					//Recursively Call the Children Nodes
					applyABMinMax(node.getChilds().get(i));
					if(node.getDepth()!=0) //If Not the Root Node
					{
						//MAX Level
						if(node.getDepth()%2==0 ) 
						{
							//Parent at min Level. if Child value greater than Parent then IGNORE
							if(node.getStatGreat()>node.getParent().getStatLess())
							{
								return;
							}
						}
						//MIN Level
						else
						{
							if(node.getStatLess()<node.getParent().getStatGreat())
							{
								return;
							}
						}
					}	
				}
				//MAX Level
				if(node.getDepth()%2==0 )
				{
					node.setStatEst(node.getStatGreat());
					node.setStatEval(true);
					if(node.getDepth()!=0)
					{
						if(node.getStatEst() < node.getParent().getStatLess() ){
							//Parent will be at MIN level.So pick the min value among children
							node.getParent().setStatLess(node.getStatEst());
						}
					}	

				}
				//MIN Level
				else
				{
					node.setStatEst(node.getStatLess());
					node.setStatEval(true);
					if(node.getDepth()!=0){
						if(node.getStatEst() > node.getParent().getStatGreat() ){
							//Parent will be at MAX level.So pick the MAX value among children
							node.getParent().setStatGreat(node.getStatEst());
						}
					}	
				}
			} //End of Nodes not Evaluated
			//Root Node
			if(node.getDepth()==0)
			{
				for(int i=0;i<node.getChilds().size();i++)
				{
					if(node.getChilds().get(i).getStatEst() == node.getStatEst()){
						node.setBoard(node.getChilds().get(i).getBoard());
						break;
					}
				}
			}
		}
		//LEAF Nodes
		else
		{
			//Get stat of the Leaf nodes
			getOpenStatEst(node);
			//MAX Level
			if(node.getDepth()%2==0)
			{
				if(node.getStatEst() < node.getParent().getStatLess() ){
					//Parent will be at MIN level.So pick the min value among children
					node.getParent().setStatLess(node.getStatEst());
					nodesEvaluated++;
				}
			}
			//MIN Level
			else 
			{
				if(node.getStatEst() > node.getParent().getStatGreat() ){
					//Parent will be in MAX level.So pick the MAX value among children
					node.getParent().setStatGreat(node.getStatEst());
					nodesEvaluated++;
				}
			}
			//nodesEvaluated++;
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
		char Coin;
		int whiteCount=0,blackCount=0;
		ArrayList<Integer> emptyPosition = new ArrayList<Integer>();
		for(int i = 0; i < 23; i++) {
			Coin = node.getBoard()[i];
			if(Coin=='x' || Coin=='X' || Coin==' '){
				emptyPosition.add(i);
			}else if(Coin=='w' || Coin=='W'){
				whiteCount++;
			}else if(Coin=='b' || Coin=='B'){
				blackCount++;
			}

		}
		if(node.getDepth() == treeDepth || whiteCount <0 || blackCount <0 ){
			//System.out.println("inside if ");
		}else{
			double statEst;
			ArrayList<TreeNode> childs = new ArrayList<TreeNode>();
			ArrayList<char[]> allBoards = new ArrayList<char[]>();

			if(node.getDepth()%2==0){
				Coin='W';
				statEst=-1000000000;
			}else{
				Coin='B';
				statEst=1000000000;
			}

			for(int i=0;i<emptyPosition.size();i++){
				allBoards = new ArrayList<char[]>();

				generateAdd(Coin,node.getBoard(),emptyPosition.get(i),allBoards);

				for(int j=0;j<allBoards.size();j++){

					//building tree
					TreeNode newNode = new TreeNode();
					newNode.setDepth(node.getDepth()+1);
					newNode.setParent(node);
					newNode.setBoard(allBoards.get(j));
					buildAddTree(newNode);

					childs.add(newNode);

				}
			}
			node.setChilds(childs);
		}



	}

	public void generateAdd(char Coin,char[] board,int Position,ArrayList<char[]> allBoard){
		char[] newBoard;
		char[] tempBoard;
		newBoard = getBoardCopy(board);

		newBoard[Position] = Coin;
		if(isCloseMill(Position,newBoard)){
			for(int i=0;i<23;i++){
				if(newBoard[i]!=Coin && newBoard[i]!='x'){
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

						if((tempBlack == 3 && Coin=='W')||(tempWhite == 3 && Coin=='B') ){
							tempBoard[i] = 'x';
							//System.out.println("Removing Position " + i);
							allBoard.add(tempBoard);
						}
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
		char Coin;

		for(int i = 0; i < 23; i++) {
			Coin = node.getBoard()[i];
			//System.out.println("Coin is " +Coin);
			if(Coin=='W' ||Coin=='w' ){
				whites++;
			}else if(Coin=='B' ||Coin=='b'){
				blacks++;
			}

		}
		//System.out.println("Whites :"+whites + "Blacks :"+blacks);
		node.setStatEst(whites - blacks);
	}

}