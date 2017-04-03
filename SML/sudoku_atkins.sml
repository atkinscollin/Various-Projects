(* Authored by: Collin Atkins
   Due on: 2/18/2016
   Class: Programming Languages
   Programming Assignment 3
   
   Sudoku Solver

   To Use: solve board;
      Inputed board must be a list of list of ints *)

(* --- Helper/simple functions --- *)

(* fn : 'a list list -> int -> int -> 'a -> 'a list list
   Helper function to replace value at pos x,y with newVal on board *)
fun replace [] posx posy newVal = []
 |  replace board posx posy newVal =
    let val tempRow = List.nth(board, posx)
    	val tempRowTaken = List.take(tempRow, posy)
    	val tempRowDropped = List.drop(tempRow, posy+1)
	val newRow = tempRowTaken @ (newVal :: tempRowDropped)
	val tempBoardTaken = List.take(board, posx)
	val tempBoardDropped = List.drop(board, posx+1)
    in tempBoardTaken @ (newRow :: tempBoardDropped)
    end;	

(* fn : ''a list -> ''a -> bool
   If searchVal exists in vals then true, otherwise false *)
fun sudoFind vals searchVal =
    if (List.exists (fn y => y = searchVal) vals)
       then true
    else false;

(* fn : int list list -> int -> int
   Finds the next blank space on the board and returns the x coord
   These XY functions could have been combined to output a pair
   	 but it would have been a chore to change it all. *)
fun findNextBlankX board posx posy = 
    if ((posx > 8) orelse (posy > 8))
       then 9
    else if (List.nth(List.nth(board, posx), posy) = 0)
       then posx
    else let val newx = if (posy = 8) then posx+1 else posx
             val newy = if (newx > posx) then 0 else posy+1
	 in findNextBlankX board newx newy
	 end;

(* fn : int list list -> int -> int
   Finds the next blank space on the board and returns the y coord *)
fun findNextBlankY board posx posy = 
    if ((posx > 8) orelse (posy > 8))
       then 9
    else if (List.nth(List.nth(board, posx), posy) = 0)
       then posy
    else let val newx = if (posy = 8)
	       	           then posx+1
	                else posx
             val newy = if (newx > posx)
    	       	           then 0
	                else posy+1
	 in findNextBlankY board newx newy
	 end;

(* fn: int list list -> int -> int list list
   If then board is complete then it outputs the board,
      otherwise it will output an empty board *)
fun checkCompleteBoard [] posx = []
 |  checkCompleteBoard board posx =
    if (posx > 8)
       then board
    else if (sudoFind (List.nth(board, posx)) 0)
       then []
    else checkCompleteBoard board (posx+1);

(* fn : 'a list list -> int -> int -> 'a list -> 'a list
   Finds the column posy on the board *)
fun findCol [] posy iter outCol = []
 |  findCol board posy iter outCol =
    if (iter > 8)
       then outCol
    else findCol board posy (iter+1)
    	 	 ((List.nth(List.nth(board, iter), posy)) :: outCol);

(* Helper function for findSmallBoard *)
fun findRange x =
    if (x < 3) then 0
    else if ((x >= 3) andalso (x < 6)) then 3
    else 6;

(* fn : 'a list list -> int -> 'a list
   Finds the 3 by 3 board for value at posx posy *)
fun findSmallBoard [] posx posy = []
 |  findSmallBoard board posx posy =
    let val smallBoard = []
	val x = findRange posx
    	val y = findRange posy
    in ( ((List.nth(List.nth(board, x), y)) ::
       (List.nth(List.nth(board, x), y+1)) ::
       (List.nth(List.nth(board, x), y+2)) :: smallBoard)
       @ ((List.nth(List.nth(board, x+1), y)) ::
       (List.nth(List.nth(board, x+1), y+1)) ::
       (List.nth(List.nth(board, x+1), y+2)) :: smallBoard)
       @ ((List.nth(List.nth(board, x+2), y)) ::
       (List.nth(List.nth(board, x+2), y+1)) ::
       (List.nth(List.nth(board, x+2), y+2)) :: smallBoard) )
    end;

(* --- Advanced functions  --- *)

(* fn : int list -> int -> int -> int list -> int list
   Finds all candidates for a blank sudoku square
   	 by checking if searchVal is not part of vals.
   Vals is the list of values in searchVal 3x3, row, and col.
   badVal is the last candidate filled in, so it does not repeat it. *)
fun findCandidates vals searchVal badVal output =
    if (searchVal > 9)
       then output
    else if ((sudoFind vals searchVal) orelse (searchVal = badVal))
       then findCandidates vals (searchVal+1) badVal output
    else findCandidates vals (searchVal+1) badVal (searchVal :: output);

(* fn : int list list -> int -> int -> int list list
   Fills in all forced positions on a sudoku board.
   Does so by checking if the amount of candidates on an empty square is one,
   	if it is just one then it is forced. *)
fun fillForced [] posx posy = []
 |  fillForced board posx posy =
    if ((posx > 8) orelse (posy > 8))
       then board
    else if ((List.nth(List.nth(board, posx), posy)) <> 0)
       then let val newx = if (posy = 8) then posx+1 else posx
	   	val newy = if (newx > posx) then 0 else posy+1
	    in fillForced board newx newy
	    end
    else let val curRow = List.nth(board, posx)
       	     val curCol = findCol board posy 0 []
	     val curSmallBoard = findSmallBoard board posx posy
	     val curValsToCheck = curRow @ curCol @ curSmallBoard
	     val curCandidates = findCandidates curValsToCheck 1 0 []
	     val newx = if (posy = 8) then posx+1 else posx
	     val newy = if (newx > posx) then 0 else posy+1
         in if ((List.length curCandidates) = 1)
       	       then fillForced (replace board posx posy (List.hd curCandidates)) newx newy
	    else fillForced board newx newy
         end;

(* fn : int list list -> int -> int -> int -> int list list
   Helper function for fillBlankAll; fills in blank square with a candidate.
   Iterates through all available candidates by using count with fillBlankAll. *)
fun fillBlank [] posx posy count = []
 |  fillBlank board posx posy count =
    if ((posx > 8) orelse (posy > 8))
       then board
    else let val newCount = if (count = 0) then count else (count-1)
       	     val curVal = List.nth(List.nth(board, posx), posy)
       	     val curRow = List.nth(board, posx)
       	     val curCol = findCol board posy 0 []
	     val curSmallBoard = findSmallBoard board posx posy
	     val curValsToCheck = curRow @ curCol @ curSmallBoard
	     val curCandidates = findCandidates curValsToCheck 1 curVal []
         in if (curCandidates = nil)
       	       then board
	    else if(newCount >= (List.length curCandidates))
	       then board
	    else replace board posx posy (List.hd(List.drop (curCandidates, newCount)))
         end;

(* fn : int list list -> int list list list -> int -> int -> int -> int -> int list list list
   Uses fillBlank to fill a blank square with all possible candidates.
   Outputs a list of boards with the blank square filled with all possible candidates.*)
fun fillBlankAll [] outBoards posx posy count = []
 |  fillBlankAll inBoard outBoards posx posy count = 
    if (((findNextBlankX inBoard 0 0) > 8) andalso ((findNextBlankY inBoard 0 0) > 8))
       then outBoards
    else if (count = 0)
       then let val newx = findNextBlankX inBoard 0 0
	   	val newy = findNextBlankY inBoard 0 0
		val newBoard = fillBlank inBoard newx newy count
	    in fillBlankAll inBoard outBoards newx newy (count+1)
	    end
    else if (count > 0)
       then let val newBoard = fillBlank inBoard posx posy count
	    in if (inBoard <> newBoard)
		  then fillBlankAll inBoard (newBoard :: outBoards) posx posy (count+1)
	       else outBoards
	    end
    else [];

(* fn : int list list list -> int list list
   Keeps track of all boards to be solved in boards.
   Steps:
	1 If a board can be filled with forced positions
	   it fills it, removes the old board, and appends the new.
	2 If the board is complete it outputs it.
	   This could be easily changed to output all solutions.
	3 If a blank position can be filled
	   it fills the position with all possibilites
	   then removes the analyzed board and appends all possible boards
	4 If none of the criteria is met it restarts with the next board. *)
fun solver [] = []
 |  solver boards =
    if ((fillForced (List.hd boards) 0 0) <> (List.hd boards))
       then solver ((fillForced (List.hd boards) 0 0) :: (List.tl boards))
    else if ((checkCompleteBoard (List.hd boards) 0) = (List.hd boards))
       then (List.hd boards)
    else if ((fillBlankAll (List.hd boards) [] 0 0 0) <> [])
       then solver ((fillBlankAll (List.hd boards) [] 0 0 0) @ (List.tl boards))
    else solver (List.tl boards);

(* fn : int list list -> int list list
   Top function, uses solver because solve must input a list of list. *)
fun solve [] = []
 |  solve board =
    solver (board :: []);