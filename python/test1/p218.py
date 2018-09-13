path = r'c:\temp\somefile.txt'
# f=open(path)
# for i in range(3):
#     print(str(i)+': '+f.readline(),end='')
#
# f.close()

import pprint
pprint.pprint(open(path).readlines())