def flatten(nested):
    try:
        try:
            nested+''
        except TypeError:# is list
            pass
        else:   # is not list
            raise TypeError
        for sublist in nested: # is list
            for element in flatten(sublist):
                yield element
    except TypeError:   # is not list
        yield nested

# print(list(flatten(['foo',['bar',['baz']]])))
print(list(flatten(['foo','bar'])))

# print(['a']+'b')