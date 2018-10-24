import wx

class RatingsPanel(wx.Panel):
    def __init__(self,parent):
        super(RatingsPanel,self).__init__(parent)

        self._chLabel=wx.StaticText(self,label='Ratings:')
        choices=['Excellent','Good','Average','Poor']
        self._choice=wx.Choice(self,choices=choices)

        self._cmtLabel=wx.StaticText(self,label='Comment:')
        self._comment=wx.TextCtrl(self,style=wx.TE_MULTILINE)
        self._submit=wx.Button(self,label='Submit')

        self._doLayout()

    def _doLayout(self):
        main=wx.BoxSizer(wx.VERTICAL)
        topRow=wx.BoxSizer(wx.HORIZONTAL)
        topRow.Add(self._chLabel,0,wx.RIGHT|wx.ALIGN_CENTER_VERTICAL,10)
        topRow.Add(self._choice,1,wx.EXPAND|wx.ALIGN_CENTER_VERTICAL)
        main.Add(topRow,0,wx.EXPAND|wx.ALL,5)
        commentRow=wx.BoxSizer(wx.HORIZONTAL)
        commentRow.Add(self._cmtLabel,flag=wx.RIGHT,border=10)
        commentRow.Add(self._comment,1,wx.EXPAND)
        main.Add(commentRow,1,wx.EXPAND|wx.ALL,5)
        main.Add(self._submit,0,wx.ALL|wx.ALIGN_RIGHT,5)
        self.SetSizer(main)

class MyFrame(wx.Frame):
    def __init__(self,parent,title=''):
        super(MyFrame,self).__init__(parent,title=title)

        sizer=wx.BoxSizer()
        self.panel=RatingsPanel(self)
        sizer.Add(self.panel,1,wx.EXPAND)
        self.SetSizer(sizer)
        self.SetInitialSize()

class MyApp(wx.App):
    def OnInit(self):
        self.frame=MyFrame(None,title='controlling layout behaviour')
        self.frame.Show()
        return True

if __name__ == '__main__':
    MyApp(False).MainLoop()