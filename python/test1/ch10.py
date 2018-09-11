
print(set(range(10)))

print({0,0,1,2,2,5,2})

a={1,2,3}
b={2,3,4}

print('a : ',a)
print('b : ',b)
print('a.union(b) : ',a.union(b))
c=a & b
print('c.issubset(a) : ',c.issubset(a))
print('c<=a : ',c<=a)
print('c.issuperset(a) : ',c.issuperset(a))
print('a.intersection(b) : ',a.intersection(b))
print('a & b :',a & b)
print('a.difference(b) : ',a.difference(b))
print('a-b : ',a-b)
print('a.symmetric_difference(b) : ',a.symmetric_difference(b))
print('a^b : ',a^b)

