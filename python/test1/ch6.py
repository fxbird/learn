def square(x):
    'Calculates the square of the number x.'
    return x*x

print(square.__doc__)

def print_params_2(title,*params):
    print(title)
    print(params)

print_params_2('Params:',1,2,3)

def print_params_3(**params):
    print(params)
    for param in params.items():
        print(param[1])

print_params_3(x=1,y=2,z=3)

