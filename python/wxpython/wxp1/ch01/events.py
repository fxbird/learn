import wx

class MyApp(wx.App):
    def OnInit(self):
        self.frame=wx.Frame(None,title='Binding events')
        self.frame.Bind(wx.EVT_SHOW,self.OnFrameShow)
        self.frame.Bind(wx.EVT_CLOSE, self.OnFrameExit)

        self.frame.Show()

        return True

    def OnFrameShow(self,event):
        theFrame=event.EventObject
        print('Frame (%s) Shown!' % theFrame.Title)
        event.Skip()

    def OnFrameExit(self,event):
        theFrame=event.EventObject
        print('Frame (%s) is closing!' % theFrame.Title)
        event.Skip()

if __name__ == '__main__':
    app=MyApp(False)
    app.MainLoop()