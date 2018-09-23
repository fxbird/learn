from smtplib import SMTP
from poplib import POP3
from time import sleep

SMTPSVR='smtp.163.com'
POP3SVR='pop.163.com'

who='fxbird1978@163.com'
body='''\
From: %(who)s
To: %(who)s
Subject: test msg
Hello World!
''' % {'who':who}

userName='fxbird1978'
password='fuckgcd1'

sendSvr=SMTP(SMTPSVR)
sendSvr.login(userName,password)
errs=sendSvr.sendmail(who,[who],body)
sendSvr.quit()
assert len(errs)==0,errs
sleep(5)

recvSvr=POP3(POP3SVR)
recvSvr.user(userName)
recvSvr.pass_(password)
rsp,msg,size=recvSvr.retr(recvSvr.stat()[0])
sep=msg.index('')
