from flask import Flask
app=Flask(__name__)

@app.route('/')
def powers(n=10):
    return ', '.join(str(3**i) for i in range(n))
