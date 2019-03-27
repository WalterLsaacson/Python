#! /usr/bin/python
#print string
print('first line in python')
#print multi whitespace
print('first line in python','second block')
#print math result
print('100 + 290 =',100+290)
#receive user input
print('plz enter your name:')
name=input()
print('lov you',name)
#express float
print('1.23 == 12.3e-1 ? ',1.23 == 12.3e-1)
print("'Hi Ray'==\"Hi Ray\" ? ",'Hi Ray'=="Hi Ray")
print('\\\t\\')
#keyword r means dont escape the flower string
print(r'\\\t\\')
print(3>1)
#use and、or、not
print('3>1 and 3>4 is :',3>1 and 3>4)
print('3>1 or 3>4 is :',3>1 or 3>4)
print('not 3>1 is :',not 3>1)
#logic judge
print('pls enter your age:')
age=input()
if age >= 18:
    print('adult')
else:
    print('teenager')
