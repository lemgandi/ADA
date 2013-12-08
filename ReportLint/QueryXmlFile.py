#!/usr/bin/env python

import sys
import os.path
import xml.dom.minidom as xml
import re
import string


from XmlFile import XmlFile
from XmlFile import XmlOption

class QueryXmlFile(XmlFile):
    def __init__(self,dir):
        """Init, passed directory name containing xml files,looking for
        Query.xml"""
        XmlFile.__init__(self,dir)
        self.ValidFieldNames = ['Query','Param','#text']
        self.inFileName = self.inDir+'Query.xml'
        self.ParamsFound=[]
        self.MyDom = xml.parse(self.inFileName)

        self.ValidAttributes={'Param':[XmlOption('seq',False),XmlOption('ref',False)]}


    def validateParamNode(self,node,lno):
        """Validate a Param in the xmlfile"""
        attrNames=[]
        param_isValid=True
        for kk in range(0,node.attributes.length):
            attrNames.append(node.attributes.item(kk).name)
        for ll in self.ValidAttributes[node.nodeName]:
            if (not ll.isOptional) and (ll.name not in attrNames):
                param_isValid = False
                self.addErrorString('Missing required attribute: %s at %d' % 
                                    (ll.name,lno))
        possibleAttrs = []
        for kk in (self.ValidAttributes[node.nodeName]):
            possibleAttrs.append(kk.name)
        for ll in attrNames:
            if ll not in possibleAttrs:
                param_isValid = False
                self.addErrorString('Unknown attribute: %s at %d' % (ll,lno))
        return param_isValid

    def validateChildren(self):
        """Validate nodes in Query.xml"""
        line_no = 0
        theroot = self.MyDom.documentElement
        numQueries = 0
        for kk in theroot.childNodes:
            if kk.nodeName not in self.ValidFieldNames:
                self.isValid = False
                self.addErrorString('Invalid node: %s at %d' % (kk.nodeName,line_no))
            if kk.nodeName == '#text':
                line_no = line_no + 1
            if ('Query' == kk.nodeName):
                numQueries = numQueries + 1
            if ('Param' == kk.nodeName):               
                if (True == self.validateParamNode(kk,line_no)):
                    self.ParamsFound.append(kk.getAttribute('ref'))
                else:
                    self.isValid = False

        if numQueries != 1:
            if(numQueries < 1):
                self.addErrorString("Cannot find an SQL query in this file")
            else:
                self.addErrorString("Only one SQL query per report please")
            self.isValid = False
        return self.isValid

    def getParamsFound(self):
        """Get params found in query file."""
        return self.ParamsFound


if __name__ == '__main__':
    
    Input = QueryXmlFile(sys.argv[1])
    isValid = Input.validateChildren()

    if True == isValid:
        print('%s is valid' % (Input.getInputFileName()))
    else:
        print('%s is not valid' % (Input.getInputFileName()))
        for kk in Input.getErrors():
            print('Error: %s' % (kk))

    print("Params:")
    for kk in Input.getParamsFound():
        print("Param: " + kk)
