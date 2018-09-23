#!/usr/bin/env python
'email-examples.py - demo creation of email messages'

from email.mime.image import MIMEImage
from email.mime.multipart import MIMEMultipart
from email.mime.text import  MIMEText
from smtplib import SMTP


SENDER='fxbird1978@163.com'
RECIPS=SENDER
SOME_IMG_FILE=r'm:\My-Documents\pictures\IMG_20161022_070424.jpg'
userName='fxbird1978'
password='fuckgcd1'


def make_mpa_msg():
    email=MIMEMultipart('alternative')
    text=MIMEText('Hello world!\r\n','plain')
    email.attach(text)
    html=MIMEText('''
        <html><body>
        <h4>Hello World!</h4>
        </body></html>
    ''','html')
    email.attach(html)

    return email

def make_img_msg(fn):
    f=open(fn,'rb')
    data=f.read()
    f.close()
    email=MIMEImage(data,name=fn)
    email.add_header('Content-Disposition','attachment; filename="%s"' % fn)
    return email

def sendMsg(fr,to, msg):
    s=SMTP('smtp.163.com')
    s.login(userName,password)
    errs=s.sendmail(fr,to,msg)
    s.quit()

if __name__ == '__main__':
    print('Sending multipart alternative msg...')
    msg=make_mpa_msg()
    msg['From']=SENDER
    msg['To']=RECIPS
    msg['Subject']='Multipart alternative test'
    sendMsg(SENDER,RECIPS,msg.as_string())

    print('Sending image msg ...')
    msg=make_img_msg(SOME_IMG_FILE)
    msg['From']=SENDER
    msg['To']=RECIPS
    msg['Subject']='Image file test'
    sendMsg(SENDER,RECIPS,msg.as_string())