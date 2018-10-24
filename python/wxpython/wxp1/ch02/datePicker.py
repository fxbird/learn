import wx
import wx.adv
import datetime

class DatePicker(wx.adv.DatePickerCtrl):
    def __init__(self,parent,dt,style=wx.adv.DP_DEFAULT):
        super(DatePicker,self).__init__(parent,dt=dt,style=style)
        self.SetInitialSize((120,-1))   #设置宽度，高度，-1 代表默认大小

class MyPanel(wx.Panel):
    def __init__(self,parent):
        super(MyPanel,self).__init__(parent)
        sizer=wx.BoxSizer(wx.VERTICAL)
        now=wx.DateTime.Now()
        self._dp=DatePicker(self,now,wx.adv.DP_DROPDOWN|wx.adv.DP_SHOWCENTURY)

        #20代表边框的空白，相当于css里的pad或margin的概念
        #0，代表占用的宽度或高度相对于所有的比例，0代表原始大小
        sizer.Add(self._dp,0,wx.ALL,20)
        # sizer.Add(self._dp)
        self.SetSizer(sizer)

class MyFrame(wx.Frame):
    def __init__(self, parent,title):
        super(MyFrame,self).__init__(parent,title=title)
        self.panel=MyPanel(self)
        self.Bind(wx.adv.EVT_DATE_CHANGED,self.OnDateChange)

    def OnDateChange(self, evt):
        date=evt.GetDate()
        self.Title = date.Format()

class MyApp(wx.App):
    def OnInit(self):
        self.frame=MyFrame(None,title='Picking Dates')
        self.frame.Show()
        return True

if __name__ == '__main__':
    app=MyApp(False)
    app.MainLoop()