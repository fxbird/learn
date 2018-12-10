from flask import Flask, render_template,session,url_for,flash,redirect
from flask_script import Manager
from flask_bootstrap import Bootstrap
from datetime import datetime
from flask_moment import Moment
from wtforms import StringField,SubmitField
from wtforms.validators import Required
from flask_wtf import FlaskForm

app = Flask(__name__)
app.config['SECRET_KEY']='fuck you'

manager = Manager(app)
bootstrap = Bootstrap(app)
moment=Moment(app)


@app.route('/user/<name>')
def user(name):
    return render_template('user.html', name=name)

@app.errorhandler(404)
def page_note_found(e):
    return render_template('404.html'),404

@app.errorhandler(500)
def internal_server_error(e):
    return render_template('500.html'),500

@app.route('/', methods=['GET','POST'])
def index():
    form=NameForm()
    if form.validate_on_submit():
        old_name=session.get('name')
        if old_name is not None and old_name!=form.name.data:
            flash('Looks like you have changed your name!')
        session['name']=form.name.data
        return redirect(url_for('index'))
    return render_template('index.html', form=form,name=session.get('name'))

class NameForm(FlaskForm):
    name=StringField('What is your name?',validators=[Required()])
    submit=SubmitField('Submit')

if __name__ == '__main__':
    manager.run(port=5000)
