// Collin Atkins & Brian Justice
// Programming Languages with Professor Schlipf
// Programming Assignment 6: Java Iterators

/* Changelog:
   4/12/16, Collin
   + Set up the class and some variables
   + initList()
   4/14/16, Collin
   + prettyPrintList()
   + printCurVal(ListIt)
   + getCurVal(ListIt)
   + getCurIndex(ListIt)
   + moveToEnd(ListIt)
   + moveToFront(ListIt)
   4/15/16, Collin
   + Thoroughly commented the program
   + moveToX(ListIt, int)
   + printCurIndex(ListIt)
   + quicksort(ListIt, ListIt)
   + partition(ListIt, ListIt)
   + modified printCurVal(ListIt)
   4/19/16, Brian
   + Fixed moveToX
   + Adjusted quicksort and partition plenty
   + Began swap(ListIt, ListIt) and worked on it
   + Fixed size
   4/19/15, Collin
   + Adjusted quicksort and partition
   + Finished swap(ListIt, ListIt)
   4/20/16, Collin
   + Adjusted scope of most of the variables for better security
   + Figured out some logic errors I was having in various functions
   +  These logic errors solved most of the problems we were having.
   4/21/16, Collin
   + Changed it so it works with equal values in the array.
   + Completed the program and commented everything.
*/

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

public class AtkinsJustice {

    ListIterator<Integer> startOfList;
    ListIterator<Integer> endOfList;
    // Used by partition as copys of loIter and hiIter
    //  so the originals are not moved.
    ListIterator<Integer> newLoIter;
    ListIterator<Integer> newHiIter;
    int size;
    
    // Constructor
    public AtkinsJustice() {
	
    }

    // Initializes list with inputs from the user
    // Also initializes the listIts startOfList and endOfList
    //   at their respective place on the list
    public void initList(ArrayList<Integer> list) {
	Scanner input = new Scanner(System.in);
	endOfList = list.listIterator();
	System.out.println("Enter all the integers in the list then indicate the ending with a alphanumerical character. ");
	// Scanner inputing each integer individually and stopping when
	//   a non-integer is entered.
	while(input.hasNextInt()) {
	    endOfList.add(input.nextInt());
	}
	startOfList = list.listIterator();
	size = getCurIndex(endOfList) + 1;
	quicksort(list, startOfList, endOfList);
    }

    // Prints list in a pretty array format
    public void prettyPrintList(ArrayList<Integer> list) {
	ListIterator<Integer> tempIter = list.listIterator();
	// Doesn't print if it is empty
        if (!tempIter.hasNext()) return;
        
	System.out.print("[" + tempIter.next());
	while(tempIter.hasNext())
        {
	    System.out.print(", " + tempIter.next());
	}
        
	System.out.println("]");
    }

    // Just for testing; prints value at iter position
    public void printCurVal(ListIterator<Integer> tempIter) {
	int val = getCurVal(tempIter);
	System.out.println("Current value at iter: " + val);
    }

    // Returns the value next to an iterator
    // If it can't return next, it returns previous
    public int getCurVal(ListIterator<Integer> tempIter) {
	int val = -1;
	if(tempIter.hasNext()) {
	    val = tempIter.next();
	    tempIter.previous();
	}
	if(tempIter.hasPrevious()) {
	    val = tempIter.previous();
	    tempIter.next();
	}
	return val;
    }

    // Just for testing; prints index of iter
    public void printCurIndex(ListIterator<Integer> tempIter) {
	int ind = getCurIndex(tempIter);
	System.out.println("Current index of iter: " + ind);
    }

    // Returns the index of the iterator
    public int getCurIndex(ListIterator<Integer> tempIter) {
       int index = 0;
       if (tempIter.previousIndex() == -1) { index += 1; }
       return tempIter.previousIndex() + index;
    }

    // Moves iterator to the end of the list
    public void moveToEnd(ListIterator<Integer> tempIter) {
	while(tempIter.hasNext()) { tempIter.next(); }
    }

    // Moves iterator to the front of the list
    public void moveToFront(ListIterator<Integer> tempIter) {
	while(tempIter.hasPrevious()) { tempIter.previous(); }
    }

    // Moves iterator to index moveTo
    public void moveToX(ListIterator<Integer> tempIter, int moveTo) {
	// First three ifs are for special cases of moveTo
	if(moveTo < 1) { moveToFront(tempIter); return; }
	if(moveTo >= size) { moveToEnd(tempIter); return; }
	int startInd = getCurIndex(tempIter);
	if(startInd == moveTo) { return; }
	// Moves iter to the right
	if(startInd < moveTo) {
	    while(getCurIndex(tempIter) < moveTo) {
		tempIter.next();
	    }
	    
	}
	// Moves iter to the left
	if(startInd > moveTo) {
	    while(getCurIndex(tempIter) > moveTo) {
		tempIter.previous();
	    }
	}
    }
    
    // Swaps values of array at loIter and hiIter
    // loIter must be at a lower index than hiIter
    public void swap(ArrayList<Integer> list,
		     ListIterator<Integer> loIter,
		     ListIterator<Integer> hiIter) {
	// If loIter is at a lower index than hiIter it flips them
	//  and recalls swap again
        if(getCurIndex(loIter) > getCurIndex(hiIter)) {
            swap(list, hiIter, loIter);
	}
	// Same index returns
        else if(getCurIndex(loIter) == getCurIndex(hiIter)) {
            return;
        }

        int loVal = getCurVal(loIter); int hiVal = getCurVal(hiIter);

	// Makes sure the iter has room to move back and forth
	//  then sets the hiVal at the loIter position
        if (loIter.hasPrevious()) {
            loIter.previous();
            loIter.next();
            loIter.set(hiVal);
        }
        else {
            loIter.next();
            loIter.previous();
            loIter.set(hiVal);
        }
           
        if (hiIter.hasPrevious()) {
            hiIter.previous();
            hiIter.next();
            hiIter.set(loVal);
        }
        else {
            hiIter.next();
            hiIter.previous();
            hiIter.set(loVal);
        }
    }

    // Quicksorts list using the hoare partition method
    public void quicksort(ArrayList<Integer> list,
			  ListIterator<Integer> loIter,
			  ListIterator<Integer> hiIter) {
        if(getCurIndex(loIter) < getCurIndex(hiIter)) {
	    int p = partition(list, loIter, hiIter);
            int startLo = getCurIndex(loIter);
            int startHi = getCurIndex(hiIter);
            
            if (startLo != startHi) {
                moveToX(hiIter, p - 1);
                if (getCurIndex(hiIter) < size && getCurIndex(loIter) > -1) {
                    quicksort(list, loIter, hiIter);
                }

                moveToX(loIter, p + 1);
                moveToX(hiIter, startHi);
                if (getCurIndex(hiIter) < size && getCurIndex(loIter) > -1) {
                    quicksort(list, loIter, hiIter);  
                }
            }
	}
    }
    
    // Partitions an array at give the iterator positions by quicksort
    public int partition(ArrayList<Integer> list,
			 ListIterator<Integer> newLoIter,
			  ListIterator<Integer> newHiIter) {
	// These are simple values but are named for clarity
	int pivotVal = getCurVal(newLoIter);
	int loValOriginal = getCurIndex(newLoIter);
        int hiValOriginal = getCurIndex(newHiIter);
        int newPartIndex = getCurIndex(newLoIter);
        boolean pivotOnLeft = true;
        
        while(getCurIndex(newLoIter) < getCurIndex(newHiIter))  {
	    // Again simple values named for clarity
	    int loVal = getCurVal(newLoIter); 
            int hiVal = getCurVal(newHiIter);
            int loIndex = getCurIndex(newLoIter);
            int hiIndex = getCurIndex(newHiIter);
            
            if (pivotOnLeft) {
		// When hiVal is less than pivotVal it swaps and moves the Iters
                if (hiVal < pivotVal) {
                    swap(list, newLoIter, newHiIter);
                    moveToX(newLoIter, loIndex + 1);  
                    moveToX(newHiIter, hiIndex);

		    // Sets pivotOnLeft to False and gives new partIndex at hi
                    pivotOnLeft = !pivotOnLeft;
                    newPartIndex = hiIndex;
                }
		// If it does not find a hiVal < pivotVal is moves left
                else { newHiIter.previous(); }
            }
            else {
		// When loVal is less the pivotVal it swaps and moves the Iters
                if (loVal > pivotVal) {
                    swap(list, newLoIter, newHiIter);
                    moveToX(newLoIter, loIndex);  
                    moveToX(newHiIter, hiIndex - 1);

		    // Sets pivotOnLeft to True and gives new partIndex at lo
                    pivotOnLeft = !pivotOnLeft;
                    newPartIndex = loIndex;
                }
		// If it does not find a loVal > pivotVal is moves right
                else { newLoIter.next(); }
            }
	}

	// After values have been swapped about it moves the newLoIter
	//  and newHiIter back to the original positions
        moveToX(newLoIter, loValOriginal);
        moveToX(newHiIter, hiValOriginal);

	// Returns the partition for quicksort to split the array at
        return newPartIndex;
    }
    
    public static void main(String args[]){
	ArrayList<Integer> list = new ArrayList<Integer>(100);
	AtkinsJustice AJ = new AtkinsJustice();
	// This calls quicksort which runs the whole program
	AJ.initList(list);
	AJ.prettyPrintList(list);
    }
}
