values=list(range(1,11))+'Jack Queen King'.split()
suits='diamonds clubs hearts spades'.split()
deck=['{} of {}'.format(v,s) for v in values for s in suits]

from pprint import pprint
pprint(deck[:12])

from random import shuffle
shuffle(deck)
pprint(deck[:12])