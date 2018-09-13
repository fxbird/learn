path = r'c:\temp\somefile.txt'

f=open(path)
lines=f.readlines()
f.close()
lines[1]="isn't a\n"
f=open(path,'w')
f.writelines(lines)
f.close()