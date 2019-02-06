import wx
from wx import MenuBar
from com.fxbird.frame.ApplyParamToSQLFrame import ApplyParamToSQLFrame


class MainFrame(wx.Frame):
    def __init__(self, parent, title=''):
        super(MainFrame, self).__init__(parent, title=title)
        self.setUpMenu()

    def setUpMenu(self):
        menuBar = MenuBar()

        fileMenu = wx.Menu()
        applyParamItem = wx.MenuItem(fileMenu, 100, 'Apply parameters to sql')
        menuBar.Append(fileMenu, '&File')
        self.Bind(wx.EVT_MENU, self.openSqlFrame, fileMenu.AppendItem(applyParamItem))

        self.SetMenuBar(menuBar)

        # wx.EVT_MENU(self,100,self.openSqlFrame())

    def openSqlFrame(self, event):
        self.applyParamFrame = ApplyParamToSQLFrame(self)
        self.applyParamFrame.Show()
        print('opening sql frame ...')
        pass


class MyApp(wx.App):
    def OnInit(self):
        self.frame = MainFrame(None, 'ePricer Tool v0.1')
        self.frame.Show()
        return True


if __name__ == '__main__':
    MyApp(False).MainLoop()
