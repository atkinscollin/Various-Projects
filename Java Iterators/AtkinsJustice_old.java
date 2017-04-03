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
   + Began swap(ListIt, ListIt)
   + Fixed size
   4/19/15, Collin
   + Adjusted quicksort and partition
   + Finished swap(ListIt, ListIt)
   4/20/16, Collin
   + Adjusted scope of most of the variables for better security
*/

/* TODO
   $ Finish quicksort
   $ Finish partition
 */

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

public class AtkinsJustice {

    //ArrayList<Integer> list = new ArrayList<Integer>(100);
    ListIterator<Integer> startOfList;
    ListIterator<Integer> endOfList;
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
	prettyPrintList(list);
	quicksort(list, startOfList, endOfList);
	prettyPrintList(list);
    }

    // Prints list in a pretty array format
    public void prettyPrintList(ArrayList<Integer> list) {
	ListIterator<Integer> tempIter = list.listIterator();
	System.out.print("[");
	while(tempIter.nextIndex() != size-1){
	    System.out.print(tempIter.next() + ", ");
	}
	System.out.println(tempIter.next() + "]");
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
	int ind = 0;
	if(tempIter.hasPrevious()) {
	    tempIter.previous();
	    ind = tempIter.nextIndex();
	    tempIter.next();
	}
	if(tempIter.hasNext()) {
	    tempIter.next();
	    ind = tempIter.previousIndex();
	    tempIter.previous();
	}
	return ind;
    }

    // Moves iterator to the end of the list
    public void moveToEnd(ListIterator<Integer> tempIter) {
	while(tempIter.hasNext()) {
	    tempIter.next();
	}
    }

    // Moves iterator to the front of the list
    public void moveToFront(ListIterator<Integer> tempIter) {
	while(tempIter.hasPrevious()) {
	    tempIter.previous();
	}
    }

    // Moves iterator to index moveTo
    public void moveToX(ListIterator<Integer> tempIter, int moveTo) {
	if(moveTo <= 1) { moveToFront(tempIter); return; }
	if(moveTo >= size) { moveToEnd(tempIter); return; }
	int startInd = getCurIndex(tempIter);
	if(startInd == moveTo) { return; }
	if(startInd < moveTo) {
	    while(getCurIndex(tempIter) < moveTo) {
		tempIter.next();
	    }
	    
	}
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
	if(getCurIndex(loIter) >= getCurIndex(hiIter)) {
	    System.out.println("Must swap lower element with higher element");
	    return;
	}
	System.out.print("Swapping ");
	printCurVal(loIter); printCurVal(hiIter);
	prettyPrintList(list);
	int loVal = getCurVal(loIter); int hiVal = getCurVal(hiIter);
	if(loIter.hasPrevious()){ loIter.previous(); }
	loIter.next();
	loIter.set(hiVal);
	
	hiIter.previous();
	hiIter.set(loVal);
	hiIter.next();
	prettyPrintList(list);
    }

    // quicksorts list using the hoare method with swaps
    public void quicksort(ArrayList<Integer> list,
			  ListIterator<Integer> loIter,
			  ListIterator<Integer> hiIter) {
	System.out.println("Quicksort entered");
	printCurVal(loIter); printCurVal(hiIter);
	ListIterator<Integer> tempIter = list.listIterator();
	if(getCurIndex(loIter) < getCurIndex(hiIter)) {
	    int p = partition(list, loIter, hiIter);
	    moveToX(tempIter, p);
	    quicksort(list, loIter, tempIter);
	    tempIter.next();
	    quicksort(list, tempIter, hiIter);
	}
	System.out.println("Quicksort complete");
    }
    
    // Partitions an array at pivot
    public int partition(ArrayList<Integer> list,
			 ListIterator<Integer> loIter,
			  ListIterator<Integer> hiIter) {
	System.out.println("Partition entered");
	printCurVal(loIter); printCurVal(hiIter);
	newLoIter = list.listIterator(loIter.nextIndex());
	newHiIter = list.listIterator(hiIter.nextIndex());
	int pivotVal = getCurVal(newLoIter);
	System.out.println("Pivot Val " + pivotVal);
	int loVal; int hiVal; int count = 0;
	while(getCurIndex(newLoIter) < getCurIndex(newHiIter)) {
	    loVal = getCurVal(newLoIter); hiVal = getCurVal(newHiIter);
	    if(loVal > pivotVal && hiVal < pivotVal) {
		swap(list, newLoIter, newHiIter);
		moveToFront(newLoIter); moveToEnd(newHiIter);
		count++;
	    }
	    if(loVal <= pivotVal) { newLoIter.next(); }
	    if(hiVal >= pivotVal) { newHiIter.previous(); }
	}
	System.out.println("Partition complete");
	Scanner scan = new Scanner(System.in);
	scan.nextLine();
	int newPartIndex = getCurIndex(newHiIter);
	moveToFront(startOfList);
	if(count > 0){ swap(list, startOfList, newHiIter); }
	return newPartIndex;
    }
    
    public static void main(String args[]){
	ArrayList<Integer> list = new ArrayList<Integer>(100);
	AtkinsJustice AJ = new AtkinsJustice();
	AJ.initList(list);
	AJ.prettyPrintList(list);
    }
}
