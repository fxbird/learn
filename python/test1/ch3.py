import math

print("{num:10}".format(num=3))
print("{name:10}".format(name='Bob'))

print("One googol is {:,}".format(10 ** 100))

print('{0:<10.2f}\n{0:^10.2f}\n{0:10.2f}'.format(3.1415926))

pi = math.pi
print('{0:+.2}\n{1:+.2}'.format(pi, -pi))

print('the middle by jimmy eat world'.center(39))

subject = '$$$ Get rich now!!! $$$'
print(subject.find('!!!', 0, 16))

seq = ['1', '2', '3', '4', '5']
sep = '+'
print(sep.join(seq))

print('*** SPAM for * everyone!!! ***'.strip(' *!'))

phonebook = {'Beth': '9102', 'Alice': '2341', 'Cecil': '3258'}
print("Cecil's phone number is {Cecil}.".format_map(phonebook))

print('copy..... demo')
x = {'username': 'admin', 'machines': ['foo', 'bar', 'baz']}
y = x.copy()
y['username'] = 'mlh'
y['machines'].remove('bar')
print(y)
print(x)

d = {'title': 'Python web site', 'url': 'http://www.python.org', 'spam': 0}
print(d.items())

for item in d.items(): print(item[0] + ' : ' + str(item[1]))

d = {
    'title': 'python web site',
    'url': 'http://www.python.org',
    'changed':'Mar 14 22:09:15 MET 2016'
}

x={'title':'python language website'}
d.update(x)
print(d)