import os
from configparser import ConfigParser
import shutil

config=ConfigParser()
config.read('config.ini')
fileMappingStr=config['project-mapping'].get('p-f')
baseProjectPath=config['project-mapping'].get('ws.base.path')

fileMappingList=fileMappingStr.split(',')

def put(projPath,filePath):
    f=open(filePath,'r')
    firstLine=f.readline()
    f.close()
    firstLine=firstLine.replace('package ','').replace(';','').strip()
    destPath=projPath+'/src/'+firstLine.replace('.','/')
    shutil.copy(filePath,destPath)
    # print(destPath)

for obj in fileMappingList[1:]:
    put(baseProjectPath+'/'+fileMappingList[0],'replace-files/'+obj.strip())
    print('Putting %s done!' % obj )

