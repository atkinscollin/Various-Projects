(* Authored by: Collin Atkins
   Due on: 2/4/2016
   Class: Programming Languages
   Programming Assignment 2
   Worked alongside Ryan Tackett on this assignment
   
   Reduced Ordered Binary Decision Diagrams (ROBDD)
   Program with many functions to manipulate datatype ROBDD that
     represents a decision tree. Functions here like conjunction
     and implies to make decisions but also some like bddEvalToTrue
     which evaluates a tree with all decisions set to True.

     use "C:\\Users\\atkin_000\\Emacs\\Programs\\hw2.sml";

     *)

datatype ROBDD = True
  | False
  | IfThenElse of string * ROBDD * ROBDD;

(*Input: string Output: ROBDD
Creates a BDD with given propLetter*)
fun bddAssert propLetter = IfThenElse (propLetter, True, False);

(*Input: BDD Output: bool
Returns true if BDD input is simply True or False*)
fun isConstantBDD True = true
 |  isConstantBDD False = true
 |  isConstantBDD (IfThenElse(_,_,_)) = false;

(*Input: BDD Output: string
Returns propLetter of BDD input*)
fun pullVar True = "T"
 |  pullVar False = "F"
 |  pullVar (IfThenElse(str, _, _)) = str;

(*Input: BDD Output: BDD
Returns second element of BDD input*)
fun pullBDDThen True = True
 |  pullBDDThen False = False
 |  pullBDDThen (IfThenElse(_, bdd, _)) = bdd;

(*Input: BDD Output: BDD
Returns third element of BDD input*)
fun pullBDDElse True = True
 |  pullBDDElse False = False
 |  pullBDDElse (IfThenElse(_, _, bdd)) = bdd;

(*Input: BDD Output: ROBDD
  Reduces down a tree of inputed BDD to find True or False at base*)
fun bddReduce True = True
 |  bddReduce False = False
 |  bddReduce (IfThenElse(str, t, f)) =
    if t = f
      then t
    else
      let
        val reducedT = (bddReduce t)
        val reducedF = (bddReduce f)
        val root = (IfThenElse(str, reducedT, reducedF))
        val normal = (IfThenElse(str, t, f))
      in
        if (reducedT = reducedF)
          then (bddReduce reducedT)
        else if (root = normal)
          then normal
        else
          bddReduce root
      end;

(*Input: BDD BDD Output: ROBDD
Given two BDDs, it returns the ROBDD of their conjunction*)
infix bddAnd;
fun True bddAnd True = True
 |  True bddAnd False = False
 |  False bddAnd True = False
 |  False bddAnd False = False
 |  bddOne bddAnd bddTwo =
    let val bddOneRed = bddReduce bddOne
        val bddTwoRed = bddReduce bddTwo
	
    in if (isConstantBDD bddOneRed andalso isConstantBDD bddTwoRed)
       	  then bddOneRed bddAnd bddTwoRed
       else if (bddOneRed = bddTwoRed)
       	    then bddOneRed
	else
          (IfThenElse (p, ite q, f)
    end;

(*Input: BDD BDD Output: ROBDD
Given two BDDs, it returns the ROBDD of their disjunction*)
infix bddOr;
fun True bddOr True = True
 |  True bddOr False = True
 |  False bddOr True = True
 |  False bddOr False = False
 |  bddOne bddOr bddTwo =
    let val bddOneRed = bddReduce bddOne
    	val bddTwoRed = bddReduce bddTwo
    in bddOneRed bddOr bddTwoRed
    end;

(*Input: BDD Output: ROBDD
Given a BDD, returns the negation of the ROBDD function*)
fun bddNot True = False
 |  bddNot False = True
 |  bddNot bdd =
    let val bddRed = bddReduce bdd
    in bddNot bddRed
    end;

(*Input: BDD BDD Output: ROBDD 
Given two BDDs, it returns ROBDD: bddOne -> bddTwo*)
infix bddImplies;
fun True bddImplies True = True
 |  True bddImplies False = False
 |  False bddImplies True = True
 |  False bddImplies False = True
 |  bddOne bddImplies bddTwo =
    let val bddOneRed = bddReduce bddOne
    	val bddTwoRed = bddReduce bddTwo
    in bddOneRed bddImplies bddTwoRed
    end;

(*Input: BDD BDD BDD Output: ROBDD
Given three BDDs, returns ROBDD of if-then-else func of three args
Reduces and orders the three BDDs, and changes propBDD to a string*)
fun bddIfThenElse (propBDD, trueBDD, falseBDD) =
    let val propBDDRed = bddReduce propBDD
    	val strBDD = if(propBDDRed = True)
	    	     then "T"
		     else "F";
	val trueBDDRed = bddReduce trueBDD
	val falseBDDRed = bddReduce falseBDD
    in IfThenElse(strBDD, trueBDDRed, falseBDDRed)
    end;

(*Input: BDD Output: ROBDD
Changes ROBDD given so that every propLetter is True*)
fun bddEvalToTrue True = True
 |  bddEvalToTrue False = False
 |  bddEvalToTrue bdd = bddEvalToTrue (pullBDDThen bdd);

(*Input: BDD Output: ROBDD
Changes ROBDD given so that every propLetter is False*)
fun bddEvalToFalse True = True
 |  bddEvalToFalse False = False
 |  bddEvalToFalse bdd = bddEvalToFalse (pullBDDElse bdd);