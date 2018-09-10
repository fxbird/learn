class MuffledCalculator:
    muffled=False
    def calc(self,expr):
        try:
            return eval(expr)
        except ZeroDivisionError:
            if self.muffled:
                print('Division b zero is illegal')
            else:
                raise

calc=MuffledCalculator()
print(calc.calc('10/2'))
# print(calc.calc('10/0'))

calc.muffled=True
print(calc.calc('10/0'))