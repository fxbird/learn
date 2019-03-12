import json
import traceback
from openpyxl import Workbook
from openpyxl.styles import Font

# path=input("Please type in the view json file :")
# if path=='':
#     path='c:/temp/col-json/EMprice-view.json'
from openpyxl.styles.colors import RED

# basePath = 'M:/My-Documents/programming/github/learn/python/epricer-tool/view-json/'
basePath = 'C:/documents/programming/java/lib/github/learn/python/epricer-tool/view-json/'
jsonPathList = ['EMprice-view.json', 'JPprice-view.json','APprice-view.json', 'NAprice-view.json', 'LAprice-view.json','CNprice-view.json' ]


def parseOptAdditionalCols(jsonFilePath):
    keywords = ['adjhighprice', 'adjlowprice', 'optimal']
    jsonFile = json.load(open(jsonFilePath))

    print()
    print(jsonFilePath.split('/')[-1], '-' * 100)
    print('%d tables are there' % len(jsonFile['tables']))
    idx = 0
    for key, value in jsonFile['tables'].items():
        idx += 1
        # print('{} : {}' % (key,str(value)))
        additionalItems = value['additionalItems']
        additOptCols = ''
        # print(key,'='*0)
        lastSeq = 0
        for item in additionalItems:
            for kw in keywords:
                try:
                    if item['labelid'].find(kw) >= 0:
                        additOptCols += ',%s(%d)' % (item['labelid'], item['sequence'])
                    lastSeq = item['sequence']
                except Exception:
                    traceback.print_exc()
                    # print('error: ', key)
        if additOptCols != '':
            print('%s(%d) : %s, Max seq : %d' % (key, idx, additOptCols[1:], lastSeq))
        else:
            print('%s(%d) : ' % (key, idx))


def parseOptCols(jsonFilePath):
    keywords = ['adjhighprice', 'adjlowprice', 'optimal']
    jsonFile = json.load(open(jsonFilePath))

    print()
    print(jsonFilePath.split('/')[-1], '-' * 100)
    print('%d tables are there' % len(jsonFile['tables']))

    idx = 0
    for key, value in jsonFile['tables'].items():
        idx += 1
        # print('{} : {}' % (key,str(value)))
        cols = value['tabview']
        optCols = ''
        # print(key,'='*0)
        lastSeq = 0
        for item in cols:
            for kw in keywords:
                try:
                    if item['labelid'].find(kw) >= 0:
                        optCols += ',%s(%d)' % (item['labelid'], item['sequence'])
                    lastSeq = item['sequence']
                except Exception:
                    traceback.print_exc()
                    # print('error: ', key)
        if optCols != '':
            print('%s(%d) : %s, Max seq : %d' % (key, idx, optCols[1:], lastSeq))
        else:
            print('%s(%d) : ' % (key, idx))


def genExcel(jsonFilePathArr, outFilePath):
    fontSize=13
    excelCols = 'ABCDEFGHIJKLMNOPQRSTUVW'
    wb = Workbook()
    wb.remove_sheet(wb.get_sheet_by_name(wb.get_sheet_names()[0]))
    keywords = ['adjhighprice', 'adjlowprice', 'optimal']
    normalFont=Font(size=fontSize)

    for jsonFilePath in jsonFilePathArr:
        jsonFile = open(jsonFilePath, 'r')
        jsonObj = json.load(jsonFile)

        sheet = wb.create_sheet(jsonFilePath.split('/')[-1])

        colIdx = 0
        sheet['A1'] = 'No.'
        maxDefaultRow = 2

        for name, value in jsonObj['tables'].items():
            rowIdx = 1
            colIdx += 1
            sheet[excelCols[colIdx] + '1'] = name

            for colItem in value['tabview']:
                rowIdx += 1
                if maxDefaultRow < rowIdx:
                    maxDefaultRow = rowIdx
                sheet[excelCols[colIdx] + str(rowIdx)] = '%s(%s)' % (colItem['labelid'],colItem['sequence'])
                sheet[excelCols[colIdx] + str(rowIdx)].font=normalFont
                for kw in keywords:
                    if (colItem['labelid'].find(kw) > -1):
                        sheet[excelCols[colIdx] + str(rowIdx)].font = Font(color=RED, bold=True,size=fontSize)

        for i in range(0, colIdx + 1):
            sheet[excelCols[i] + '1'].font = Font(bold=True,size=fontSize)

        for r in range(2, maxDefaultRow + 1):
            sheet['A' + str(r)] = r - 1
            sheet['A' + str(r)].font=normalFont

        startExtraRow=maxDefaultRow+2
        #generate additional columns
        colStart=0
        maxGroupExtraRow=0

        for name, value in jsonObj['tables'].items():
            rowStart=maxDefaultRow+1
            colStart += 1
            groupRow = 0

            for colItem in value['additionalItems']:
                groupRow+=1
                if maxGroupExtraRow<groupRow:
                    maxGroupExtraRow=groupRow
                rowStart+=1
                sheet[excelCols[colStart]+str(rowStart)]= '%s(%s)' % (colItem['labelid'],colItem['sequence'])
                sheet[excelCols[colStart]+str(rowStart)].font=normalFont


        row=0
        for i in range(startExtraRow,startExtraRow+maxGroupExtraRow):
            row+=1
            sheet['A'+str(i)]=row
            sheet['A'+str(i)].font=normalFont

        sheet['B'+str(maxDefaultRow+1)]='Addtional Columns'
        sheet['B'+str(maxDefaultRow+1)].font=Font(color=RED, bold=True,size=fontSize)


    wb.save(outFilePath)


# parseOptAdditionalCols(path)
for p in jsonPathList:
    jsonFilePath = basePath + p
    excelOutPath = basePath + 'allCols.xlsx'

    # parseOptAdditionalCols(basePath + p)
    # parseOptCols(basePath + p)

    jsonFilePathArr = [basePath + p for p in jsonPathList]
    genExcel(jsonFilePathArr, excelOutPath)
