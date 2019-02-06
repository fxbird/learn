
def applyParamsToSql(sql,paramStr):
    sqlList=sql.strip(' \n').split('?')
    sqlList.pop(-1)
    paramList=paramStr.strip().split(', ')

    rst = ''
    for index, paramPoint in enumerate(sqlList):
        if (index<len(sqlList)):
            rst+=paramPoint+"'"+paramList[index]+"'"
        else:
            rst+=paramPoint

    return rst

if __name__ == '__main__':
    sql="""
    SELECT * FROM (SELECT * FROM (SELECT EL_TEMP.*, ROWNUMBER() OVER() AS EL_ROWNM FROM (SELECT DISTINCT t0.QUOTEID AS a1, t0.CTRYCODE AS a2, t0.LEGACYQUOTEID AS a3, t0.CREATORTYPE AS a4, t1.COMPANYNAME1 AS a5, t1.COMPANYNAME2 AS a6, t1.LOC_COMPANYNAME2 AS a7, t1.STREETADDRESS2 AS a8, t1.LOC_STREETADDRESS2 AS a9, t2.TRANSCREATEDATE AS a10, t0.BPQUOTESTATUS AS a11, t0.TRANSACTIONSTATUS AS a12, t0.BIDSTANDARDFLAG AS a13, t0.LOCKCODE AS a14, t1.CUSTCODE AS a15, t0.BPREASONCODE AS a16, t1.LOC_COMPANYNAME1 AS a17, t0.APPROVALCODE AS a18, t0.DESCRIPTION AS a19, t3.EMAIL AS a20, t3."NAME" AS a21, t4.DLNAME AS a22, t4.REQREVIEWDATE AS a23, t2.APPROVALDATETIME AS a24, t3."ID" AS a25, t3.ACTIONSTATUS AS a26, t4.REVIEWERID AS a27, t3.USERCODE AS a28, t2.EXPIREDATE AS a29, t1.ISUCODE AS a30, t0.ORDERSTATUS AS a31 FROM epricer.CTMTTRN t0 LEFT OUTER JOIN epricer.CTMTTUS t3 ON (t3.QUOTEID = t0.QUOTEID) LEFT OUTER JOIN epricer.CTMTTRU t4 ON (t4.QUOTEID = t0.QUOTEID) LEFT OUTER JOIN epricer.CTMTTCU t1 ON (t1.QUOTEID = t0.QUOTEID) LEFT OUTER JOIN epricer.CTMTTDAT t2 ON (t2.QUOTEID = t0.QUOTEID) WHERE ((((((((t0.QUOTEID = ?) AND NOT ((t0.BPQUOTESTATUS IN (?, ?, ?, ?)))) AND ((t0.CREATORTYPE = ?) OR (t0.CREATORTYPE = ?))) AND NOT ((t0.BPREASONCODE IN (?, ?, ?, ?)))) AND ((((t3.USERCODE = ?) AND (t3.ACTIONSTATUS = ?)) AND (t3."ID" = t0.CURRENTREVIEWERID)) OR ((t3.USERCODE = ?) AND (t3."ID" = ?)))) AND ((((t0.CURRENTREVIEWERID = ?) AND NOT ((((t4.REVIEWUNITID IS NULL) AND (t4.REVIEWID IS NULL)) AND (t4.QUOTEID IS NULL)))) OR ((t4.COMPLETIONSTATUS = ?) AND (t4.REVIEWERID = t0.CURRENTREVIEWERID))) OR (((t4.REVIEWUNITID IS NULL) AND (t4.REVIEWID IS NULL)) AND (t4.QUOTEID IS NULL)))) AND (t0.WAITFORDELETE = ?)) AND (t0.CTRYCODE IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?))) ORDER BY t0.QUOTEID DESC) AS EL_TEMP) AS EL_TEMP2 WHERE EL_ROWNM <= ?) AS EL_TEMP3 WHERE EL_ROWNM > ?"""

    paramStr="""7184320, 21, 20, 22, 23, B, I, 261, 262, 269, 26A, V, R, R, 1, 0, 1, N, AU, BN, CN, HK, I1, ID, IN, KH, KR, LK, MY, NZ, PH, SG, TH, TW, VN, 1800, 0"""

    result=applyParamsToSql(sql,paramStr)
    print(result)