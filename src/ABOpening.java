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
			//System.out.println("Evaluating leaf");
			getOpenStatEst(node);
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

	char[] getBoardCopy(char[] board){
		char [] copy = new char [23];
		for(int i=0;i<23;i++){
			copy[i] = board[i];
		}
		return copy;
	}
	public void buildAddTree(TreeNode node){
		char c;
		int whiteCount=0,blackCount=0;
		ArrayList<Integer> emptyInd = new ArrayList<Integer>();
		for(int i = 0; i < 23; i++) {
			c = node.getBoard()[i];
			if(c=='x' || c=='X' || c==' '){
				emptyInd.add(i);
			}else if(c=='w' || c=='W'){
				whiteCount++;
			}else if(c=='b' || c=='B'){
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
				c='W';
				statEst=-1000000000;
			}else{
				c='B';
				statEst=1000000000;
			}

			for(int i=0;i<emptyInd.size();i++){
				allBoards = new ArrayList<char[]>();

				generateAdd(c,node.getBoard(),emptyInd.get(i),allBoards);

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

	public void generateAdd(char c,char[] board,int ind,ArrayList<char[]> allBoard){
		char[] newBoard;
		char[] tempBoard;
		newBoard = getBoardCopy(board);

		newBoard[ind] = c;
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
					}
				}
			}


		}else{
			allBoard.add(newBoard);
		}

	}

	public boolean isCloseMill(int ind,char[] board){
		boolean ret = false;
		switch(ind){

		case 0: if(board[ind] == board[1] && board[ind] == board[2] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[8] && board[ind] == board[20] && board[ind]!='x'){
			ret = true;
		}else if(board[ind] == board[3] && board[ind] == board[6] && board[ind]!='x'){
			ret = true;
		}
		break;

		case 1: if(board[ind] == board[0] && board[ind] == board[2] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 2: if(board[ind] == board[0] && board[ind] == board[1] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[13] && board[ind] == board[22] && board[ind]!='x'){
			ret = true;
		}else if(board[ind] == board[5] && board[ind] == board[7] && board[ind]!='x'){
			ret = true;
		}
		break;

		case 3: if(board[ind] == board[0] && board[ind] == board[6] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[4] && board[ind] == board[5] && board[ind]!='x'){
			ret = true;
		}else if(board[ind] == board[9] && board[ind] == board[17] && board[ind]!='x'){
			ret = true;
		}
		break;

		case 4: if(board[ind] == board[3] && board[ind] == board[5] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 5: if(board[ind] == board[7] && board[ind] == board[2] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[3] && board[ind] == board[4] && board[ind]!='x'){
			ret = true;
		}else if(board[ind] == board[12] && board[ind] == board[19] && board[ind]!='x'){
			ret = true;
		}
		break;

		case 6: if(board[ind] == board[10] && board[ind] == board[14] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[0] && board[ind] == board[3] && board[ind]!='x'){
			ret =true;
		}
		break;	

		case 7: if(board[ind] == board[11] && board[ind] == board[16] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[2] && board[ind] == board[5] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 8: if(board[ind] == board[9] && board[ind] == board[10] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[0] && board[ind] == board[20] && board[ind]!='x'){
			ret = true;
		}
		break;

		case 9: if(board[ind] == board[3] && board[ind] == board[17] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[8] && board[ind] == board[10] && board[ind]!='x'){
			ret = true;
		}
		break;

		case 10: if(board[ind] == board[8] && board[ind] == board[9] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[6] && board[ind] == board[14] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 11: if(board[ind] == board[12] && board[ind] == board[13] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[7] && board[ind] == board[16] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 12: if(board[ind] == board[11] && board[ind] == board[13] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[5] && board[ind] == board[19] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 13: if(board[ind] == board[11] && board[ind] == board[12] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[2] && board[ind] == board[22] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 14: if(board[ind] == board[15] && board[ind] == board[16] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[17] && board[ind] == board[20] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[6] && board[ind] == board[10] && board[ind]!='x'){
			ret =true;
		}
		break;		

		case 15: if(board[ind] == board[18] && board[ind] == board[21] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[14] && board[ind] == board[16] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 16: if(board[ind] == board[7] && board[ind] == board[11] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[19] && board[ind] == board[22] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[14] && board[ind] == board[15] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 17: if(board[ind] == board[3] && board[ind] == board[9] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[14] && board[ind] == board[20] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[18] && board[ind] == board[19] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 18: if(board[ind] == board[15] && board[ind] == board[21] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[17] && board[ind] == board[19] && board[ind]!='x'){
			ret =true;
		}
		break;	

		case 19: if(board[ind] == board[5] && board[ind] == board[12] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[16] && board[ind] == board[22] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[17] && board[ind] == board[18] && board[ind]!='x'){
			ret =true;
		}
		break;


		case 20: if(board[ind] == board[0] && board[ind] == board[8] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[14] && board[ind] == board[17] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[21] && board[ind] == board[22] && board[ind]!='x'){
			ret =true;
		}
		break;

		case 21: if(board[ind] == board[15] && board[ind] == board[18] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[20] && board[ind] == board[22] && board[ind]!='x'){
			ret =true;
		}
		break;	

		case 22: if(board[ind] == board[2] && board[ind] == board[13] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[16] && board[ind] == board[19] && board[ind]!='x'){
			ret =true;
		}else if(board[ind] == board[20] && board[ind] == board[21] && board[ind]!='x'){
			ret =true;
		}
		break;


		}
		return ret;
	}

	public void printTreeNode(TreeNode node){
		for(int i=0;i<node.getDepth();i++){
			System.out.print("|");
		}
		System.out.print("StatEst:" +node.getStatEst() + ";Board is:");
		for(int i=0;i<23;i++){
			System.out.print(node.getBoard()[i]);
		}
		System.out.println();

		if(node.getChilds()!=null){
			//System.out.println("Printing Childs");
			for(int i=0;i<node.getChilds().size();i++)
				printTreeNode(node.getChilds().get(i));
		}	
	}

	public void getOpenStatEst(TreeNode node){
		int whites =0;
		int blacks =0;
		char c;

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