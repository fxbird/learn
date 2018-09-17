from subprocess import Popen,PIPE

text=open('messy.html').read()
tidy=Popen(r'm:\download\soft\python\tidy-5.6.0-vc14-64b\bin\tidy.exe',stdin=PIPE,stdout=PIPE,stderr=PIPE)

tidy.stdin.write(text.encode())
tidy.stdin.close()

print(tidy.stdout.read().decode())