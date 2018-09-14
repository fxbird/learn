from urllib.request import urlopen

import re

p=re.compile('<a href="(/jobs/\\d+)/">(.*?)</a>')
text=urlopen('http://python.org/jobs').read().decode('utf-8')
for url,name in p.findall(text):
    print('{} ({})'.format(name.strip(),url.strip()))