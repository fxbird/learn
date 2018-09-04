values=1,2,3
x,y,z=values
print(x,y,z)

a,b,*rest=[1,2,3,4]
print (rest)

a,*b,c='abc'
print(b)

names=['anne','beth','george','damon']
ages=[12,45,32,102]
for name,age in zip(names,ages):
    print(name , 'is',age,'years old')

print(list(zip(names,ages)))


squares={i:"{} sauared is {}".format(i,i**2) for i in range(10)}
print(squares)