import re

oriCode='''
   if (quote.isBPTransactionQuote()) {
            endUserCustType = quoteCustomer.getCustomertypecode();
        } else {
            endUserCustType = quoteCustomer.getId().getCustcode();
        }
        
        //Find the appropriate DLs for the endUserCustType
        int filteredListSize = filteredList.size();
        for (int i = 0; i < filteredListSize; i++) {
            paramDistribListObj = (Ctmpdis) filteredList.get(i);
            List lDlCustType = getSpecificRestrictionElementList(paramDistribListObj, CT);
            if (lDlCustType.contains(endUserCustType)) {
                 dlFilteredList.add(paramDistribListObj);
            }
        } 
        dl_to_returnList = filterDLOnState(dlFilteredList, quote, quoteCustomer);
        
'''

oriCode=open(input('Please input the file full path.\n'),'r').read()

pattern=re.compile('for \(int\s+\w+\s*=\s*0;\s*\w\s*<\s*(\w+)Size\s*;\s*\w+.*\s*{\s*((\w+)\s*=\s*.+;)')

convertedCode=re.sub(pattern,r'for (Object \3 : \1){\n',oriCode)
convertedFile=