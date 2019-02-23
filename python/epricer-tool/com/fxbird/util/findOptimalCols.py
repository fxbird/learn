import json

# path=input("Please type in the view json file :")
# if path=='':
#     path='c:/temp/col-json/EMprice-view.json'

path='c:/temp/col-json/EMprice-view.json'
keywords=['adjhighprice','adjlowprice','optimal']

jsonFile=json.load(open(path))

# print(jsonFile['tables'])


for key, value in jsonFile['tables'].items():
    # print('{} : {}' % (key,str(value)))
    additionalItems=value['additionalItems']
    additOptCols=''
    print(key,'='*0)
    for item in additionalItems:
        for kw in keywords:
            try:
                if item['labelid'].find(kw)>=0:
                    additionalItems+=','+item['labelid']

                if additOptCols!='':
                    print('{} : {}' % (key,additOptCols[1:]))
            except Exception:
                pass
                print('error: ', key)