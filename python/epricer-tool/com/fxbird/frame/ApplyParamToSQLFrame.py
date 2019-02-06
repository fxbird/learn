import wx
from com.fxbird.util.SqlUtilMod import applyParamsToSql

class ApplyParamToSQLFrame(wx.Frame):
    def __init__(self, parent):
        super(ApplyParamToSQLFrame, self).__init__(parent, title='Apply Params to SQL')
        self.initUI()

    def initUI(self):
        self.panel = MyPanel(self)
        self.SetSize(1000,600)

class MyPanel(wx.Panel):
    def __init__(self, parent):
        self.frame=parent
        super(MyPanel,self).__init__(parent)
        allSizer = wx.BoxSizer(wx.VERTICAL)
        font=wx.Font(14, wx.MODERN, wx.NORMAL, wx.NORMAL, False, 'Consolas')

        firstSizer = wx.BoxSizer(wx.HORIZONTAL)
        firstSizer.Add(wx.StaticText(self, label='SQL:'), proportion=1,border=5)
        self.txtSql = wx.TextCtrl(self,size=(900,150), style=wx.TE_MULTILINE|wx.TE_RICH|wx.TE_PROCESS_ENTER)
        self.txtSql.SetFont(font)
        firstSizer.Add(self.txtSql,flag= wx.EXPAND | wx.ALL,border=5,)
        allSizer.Add(firstSizer,border=5)


        secondSizer = wx.BoxSizer(wx.HORIZONTAL)
        secondSizer.Add(wx.StaticText(self, label='Parameters:'),border=5)
        self.txtParams = wx.TextCtrl(self,size=(900,100), style=wx.TE_MULTILINE|wx.TE_RICH|wx.TE_PROCESS_ENTER)
        self.txtParams.SetFont(font)
        secondSizer.Add(self.txtParams,border=5)
        allSizer.Add(secondSizer,border=5)

        thirdSizer = wx.BoxSizer(wx.HORIZONTAL)
        thirdSizer.Add(wx.StaticText(self, label='Result:'),border=5)
        self.txtResult = wx.TextCtrl(self,size=(900,150), style=wx.TE_MULTILINE|wx.TE_RICH|wx.TE_PROCESS_ENTER)
        self.txtResult.SetFont(font)
        thirdSizer.Add(self.txtResult,border=5)
        allSizer.Add(thirdSizer,border=5)

        fourthSizer=wx.BoxSizer(wx.HORIZONTAL)
        self.btnApply=wx.Button(self,label='Apply')
        self.Bind(wx.EVT_BUTTON,self.onApply, self.btnApply)
        fourthSizer.Add(self.btnApply,border=25)
        allSizer.Add(fourthSizer)

        self.SetSizer(allSizer)

    def onApply(self,event):
        sql = self.txtSql.GetValue()
        params = self.txtParams.GetValue()
        appliedSql = applyParamsToSql(sql, params)
        self.txtResult.SetValue(appliedSql)

# if __name__ == '__main__':
#     frame = ApplyParamToSQLFrame(None)
#     frame.Show()
