from atexit import register
from random import randrange
from threading import BoundedSemaphore,Lock,Thread
from time import ctime,sleep

lock=Lock()
MAX=5
candytray=BoundedSemaphore(MAX)

def refill():
    lock.acquire()
    print('Refilling candy...',end=' ')
    try:
        candytray.release()
    except Exception as e:
        print('full, skipping')
        raise e
    else:
        print("OK")

    lock.release()

def buy():
    lock.acquire()
    print('Buying candy...', end=' ')
    if candytray.acquire(False):
        print('OK')
    else:
        print('Empty, skipping')
    lock.release()

def produce(loops):
    for i in range(loops):
        refill()
        sleep(randrange(3))

def consume(loops):
    for i in range(loops):
        buy()
        sleep(randrange(3))

def main():
    print('starting at:',ctime())
    nloops=randrange(2,6)
    print('nloops =',nloops)
    print('THE CANDY MACHINE (full with %d bars)!' % MAX)
    # Thread(target=consume,args=(randrange(nloops,nloops+MAX+2),)).start()
    Thread(target=produce,args=(nloops,)).start()

@register
def _atexit():
    print('all done at:',ctime())

if __name__ == '__main__':
    main()

