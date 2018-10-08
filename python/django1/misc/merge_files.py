import os
from docx import Document

path=input('请输入完整的目录:\n').rstrip(r'\/')
# path=r'M:\My-Documents\programming\java\source\Python Django Practise\mysite\course'
result=path+'\n'
for fname in os.listdir(path):
   fullPath=os.path.join(path,fname)
   if os.path.isfile(fullPath) and not fullPath.find('init')>-1:
       result+=fname.center(80,'*')+'\n'
       f=open(fullPath,'r',encoding='utf-8')
       result+=f.read()
       result+='\n\n\n'
       f.close()

print(result)
folder_name=os.path.join('source',path.split('\\')[-1])
print('Folder name :',folder_name)
document =Document()
document.add_paragraph(result)
document.save(folder_name+'.docx')