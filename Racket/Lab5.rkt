;Collin Atkins
;3/4/2015
;Programming Languages
;Lab 5

;To run the program simply do (run n) where n is the number of prime numbers you want
;Prints the first n prime numbers

;creates a list of elements for numbers 3 - n, excludes 1 and all even numbers
(define (makeList n)
  (define (iter n result)
    (cond
      ((< n 3) (deleteMults result '() 2))
      (else (iter (- n 1) (cons n result)))
      )
    )
  (iter n '())
  )

;removes all the multiples of ele in lst by moving all the non-multiples to retList
(define (deleteMults lst retList ele)
  (cond ((null? lst) (reverse retList))
        ((= (modulo (car lst) ele) 0)
         (deleteMults (cdr lst) retList ele))
        (else 
         (deleteMults (cdr lst) (cons (car lst) retList) ele))
        )
  )

;finds the first number of p_num_needed of prime numbers in aList given.
(define (findPrimes p_num_needed aList)
  (define (findPrimeLikes n aList bList)
    (cond
      ;given list doesn't have evens so it must append the prime number 2
      ((null? aList) (append '(2)(cdr(reverse bList))))
      ((= (length bList) p_num_needed) (append '(2)(cdr(reverse bList))))
      (else(findPrimeLikes (car aList) (deleteMults aList '() (car aList)) (cons n bList)))
      )
    )
  (findPrimeLikes (car aList) aList '())
  )

;main: ;To run the program simply do (run n) where n is the number of prime numbers you want
;Prints the first n prime numbers
;n (0-2) are manually set because my list starts at 3.
(define (run n)
  (cond
    ((= n 0) '())
    ((= n 1) '(2))
    ((= n 2) '(2 3))
    ;creates a list of n^2 numbers because n^2 is always greater than nth prime number
    (else(findPrimes n (makeList (expt n 2))))
    )
  )