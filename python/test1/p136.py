try:
    x=int(input('Enter the first number: '))
    y=int(input('Enter the second number: '))
    print(x/y)
except (ZeroDivisionError,TypeError,NameError):
    print('Your numbers were bogus ...')