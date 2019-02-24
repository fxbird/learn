import json

file=open('view-json/test.json')
jsonFile=json.load(file)

for item in jsonFile['additionalItems']:
    print('%d : %s' % (item['sequence'],item['labelid']))