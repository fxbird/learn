import wx

class MyPanel(wx.Panel):
    def __init__(self,parent):
        super(MyPanel,self).__init__(parent)
        sizer=wx.BoxSizer(wx.HORIZONTAL)

        button=wx.Button(self,wx.ID_OK)
        sizer.Add(button)

        button=wx.Button(self,label='Play')
        bitmap=wx.Bitmap('monkeyTime.png')
        button.SetBitmap(bitmap)
        sizer.Add(button)

        button=wx.Button(self,wx.ID_APPLY)
        # button.SetAuthNeeded()
        sizer.Add(button)

        self.SetSizer(sizer)
        self.Bind(wx.EVT_BUTTON,self.OnButton)

    def OnButton(self, event):
        button=event.EventObject
        print('%s was pushed!' % button.Label)
        if button.GetAuthNeeded():
            print('Action requires authorization to proceed!')
        event.Skip()

class MyFrame(wx.Frame):
    def __init__(self, parent, title=''):
        super(MyFrame,self).__init__(parent,title=title)
        self.panel=MyPanel(self)

class MyApp(wx.App):
    def OnInit(self):
        self.frame=MyFrame(None,title='Easy Button')
        self.frame.Show()
        return True

if __name__ == '__main__':
    app=MyApp(False)
    app.MainLoop()