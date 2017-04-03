;Collin Atkins
;3/4/2015
;Programming Languages
;Lab 6

;To run the program simply do (run n) where n is the number of prime numbers you want
;Prints the first n prime numbers

;First program went roughly but I solved it aptly. This one I was more intrigued and educated
;from lectures. I enjoyed this assignment a lot and look forward to more scheme.

;This is really cool. This really feels like something unique to scheme.
(define-syntax cons-stream
  (syntax-rules ()
    ((cons-stream x y)
     (cons x (delay y)))))

;tail is the promise at the end, when reached it creates a longer list
(define (tail stream) (force (cdr stream)))

;sieves the stream for prime numbers
(define (sieve stream)
   (cons-stream 
    (car stream)
    (sieve 
     (filter
      ;the actual line that checks if the number is prime
      ;I feel like I should have used lambda more than just this occurence.
      (lambda (check) (>= (remainder check (car stream)) 1))
      (tail stream)))
    )
  )

;called from sieve when after checking if the number was prime
;gets the number at the end of the list and filters it
(define (filter n lst)
  (cond 
    ((null? lst) '())
    ((n (car lst)) (cons-stream (car lst) (filter n (tail lst))))
    (else (filter n (tail lst))))
  )

;simply creates a number list starting from n
(define (numbers n)
 (cons-stream n 
  (numbers (+ n 1))))

;called sieve to sieve the numbers into only prime numbers
(define prime_numbers (sieve (numbers 2)))

;main: To run the program simply do (run n) where n is the number of prime numbers you want
;Prints the first n prime numbers
(define (run n)
  (define (keepUp n stream)
    (cond 
      ((= n 0) '())
      (else (cons (car stream) (keepUP (- n 1) (tail stream))))
      )
    )
  (keepUp n prime_numbers)
  )