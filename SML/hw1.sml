(* Authored by: Collin Atkins
   Completed on: 1/21/2016
   Class: Programming Languages

   Program for finding various list of primes*)

(*use "C:\\Users\\atkin_000\\Emacs\\Programs\\hw1.sml";*)

(*Takes in list of reals and sums them, slightly altered to work with filter from sumOfRecipsThrough by taking in a func*)
fun total func [] = 0.0
 | total func (x::xs) = (func x) + (total func xs);

(*Gives range of ints from lo int to hi int*)
fun range lo hi =
    if hi < lo
       then []
    else
	lo::(range (lo+1) hi);

(*Finds all primes in a given list and outputs a list*)
fun findAllPrimes [] = []
| findAllPrimes (x::xs) =
  if x = 1
    then x::(findAllPrimes xs)
  else
    x::(findAllPrimes (List.filter (fn y => (y mod x <> 0)) xs));

(*Finds all primes from 2 to hi, outputs list*)
fun primesThrough hi =
    findAllPrimes (range 2 hi);

(*Takes in a list of the limits and a list of the primes.
  Outputs a list of sums of primes for each limit on the list of primes.*)
fun sumOfRecipsThrough [] listOfPrimes = []
 |  sumOfRecipsThrough (x::xs) listOfPrimes =
    let val primeRecips = (fn primeInt => 1.0/(Real.fromInt(primeInt)))
    	val limitedPrimes = (List.filter (fn limit => limit < (x + 1)) listOfPrimes)
	val sumLimitedPrimes = total primeRecips limitedPrimes
    in sumLimitedPrimes::(sumOfRecipsThrough xs listOfPrimes)
    end;

(*Helper function to find quad primes within 10 of eachother.
  Takes in curPrimesList that reduces in size each call and
  a finalList that keeps track of the prime quads found.*)
(*Dangit, can't figure out why this can't get past the first iteration of first four primes*)
fun fourPrimesClose [] finalList = []
 |  fourPrimesClose curPrimesList =
    if ((length curPrimesList) < 4)
       then []
    else if ((List.nth(curPrimesList, 0)) > (List.nth(curPrimesList, 3) - 9))
       then ((List.take(curPrimesList, 4))::(List.drop(curPrimesList, 1)))
    else fourPrimesClose (List.drop(curPrimesList, 1));

(*Takes in int to find quadurples primes within 10 of eachother
  up to the number inputed*)
fun primeQuadruplesThrough primesUpTo =
    fourPrimesClose (primesThrough primesUpTo) [];