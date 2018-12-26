from flask_wtf import FlaskForm
from wtforms import StringField,PasswordField,BooleanField,SubmitField
from wtforms.validators import Required,Length,Email,Regexp,EqualTo
from wtforms import ValidationError
from .. models import User

class LoginForm(FlaskForm):
    email=StringField('Email',validators=[Required(),Length(1,64),Email()])
    password=PasswordField('Passoword',validators=[Required()])
    remember_me=BooleanField('Keep me logged in')
    submit=SubmitField('Log in')

class RegistrationForm(FlaskForm):
    email=StringField('Email',validators=[Required(),Length(1,64),Email()])
    username=StringField('User name',validators=[Required(),Length(1,64),Regexp('^[A-Za-z][A-Za-z0-9_.]*$',0,'User names must have only letters, numbers, dots or underscores')])
    passoword=PasswordField('Password',validators=[Required(),EqualTo('password2',message='Passwords must match.')])
    password2=PasswordField('Confirm passoword',validators=[Required()])
    submit=SubmitField('Register')

    def validate_email(self,field):
        if User.query.filter_by(email=field.data).first():
            raise ValidationError('Email already registered')

    def validate_username(self,field):
        if User.query.filter_by(username=field.data).first():
            raise ValidationError('User name already in use.')