from configparser import ConfigParser

CONFIGFILE='area.ini'
config=ConfigParser()
config.read(CONFIGFILE)

print(config['messages'].get('greeting'))

radius=float(input(config['messages'].get('question')+' '))

print(config['messages'].get('result_message'),end=' ')

print(config['numbers'].getfloat('pi')*radius**2)