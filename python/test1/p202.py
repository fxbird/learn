import re

pat='[a-zA-Z]+'
text='"Hm... Err -- are you sure?" he said, sounding insecure.'
list=re.findall(pat,text)
print(list)

pat='{name}'
text='Dear {name}...'
print(re.sub(pat,'Mr.Gumby',text))