#!/usr/bin/env python

import sys
import os.path
import xml.dom.minidom as xml
import re
import string


from XmlFile import XmlFile
from XmlFile import XmlOption

class OutputXmlFile(XmlFile):
    """Parse Output.xml, report errors"""

    def __init__(self,dir):
        """Init, passed directory name containing xml files,looking for
        Output.xml"""
        XmlFile.__init__(self,dir)
        self.ValidFieldNames = ['Label','#text']
        self.inFileName = self.inDir+'Output.xml'
        self.ParamsFound=[]
        self.MyDom = xml.parse(self.inFileName)
        self.validFieldNames=['Label','#text']
        self.validFieldTypes={ 'Header':[XmlOption('type',False),
                                         XmlOption('text',False)],
                               'SubHeader':[XmlOption('type',False),
                                            XmlOption('text',False)],
                               'InfoHeader':[XmlOption('type',False),
                                             XmlOption('text',False)]}
        self.validParamTypes=[XmlOption('seq',False),XmlOption('otype',False),
                              XmlOption('ref',False)]
        self.validOtypes=["String","Integer","Float","Double","Long"]


    def checkFields(self,node,validFieldOptions,lno):
        """Check field options for a node"""
        attrNames=[]
        for ii in range(0,node.attributes.length):
            attrNames.append(node.attributes.item(ii).name)
        for kk in validFieldOptions:
            if (not kk.isOptional) and (kk.name not in attrNames):
                self.isValid=False
                self.addErrorString('Missing required attribute: %s at %d' %
                (kk.name,lno))
        possibleAttrs=[]
        for kk in range(0,len(validFieldOptions)):
            possibleAttrs.append(validFieldOptions[kk].name)
        for ll in attrNames:
            if ll not in possibleAttrs:
                self.isValid = False
                self.addErrorString('Unknown attribute: %s at %d' % 
                    (ll,lno))

    def check_otype(self,otype,lno):
        """Validate an otype param"""
        if otype not in self.validOtypes:
            self.isValid = False
            self.addErrorString('Unknown output type: %s at %d' % (otype,lno))

    def validateParam(self,paramNode,lno):
        """Validate a parameter node"""            
        self.checkFields(paramNode,self.validParamTypes,lno)
        theType=paramNode.getAttribute('otype')
        if ('' != theType):
            self.check_otype(theType,lno)
        theRef=paramNode.getAttribute('ref')
        if('' != theRef):
            self.ParamsFound.append(theRef)
                
    def validateNode(self,node,lno):
        """Validate a node"""
        theType=node.getAttribute('type')
        if theType == '':
            self.isValid = False
            self.addErrorString('Missing type at %d' % (lno))            
        elif theType not in self.validFieldTypes:
            self.isValid = False
            self.addErrorString('Unknown type: %s at %d' % 
            (theAttr,lno))
        else:
            validFieldOptions=self.validFieldTypes[theType]
            self.checkFields(node,validFieldOptions,lno)
        for kk in node.childNodes:
            if(kk.nodeName == '#text'):
                ++lno
            else:
                self.validateParam(kk,lno)

    def validateChildren(self):
        """Validate nodes of the Output file"""
        line_no=0
        theroot=self.MyDom.documentElement
        for kk in theroot.childNodes:
            if kk.nodeName not in self.ValidFieldNames:
                self.isValid=False
                self.addErrorString('Invalid node: %s at %d' %
                (kk.nodeName,line_no))
            if kk.nodeName == '#text':
                line_no = line_no+1
            else:
                self.validateNode(kk,line_no)
        return self.isValid

    def getParamsFound(self):
        return self.ParamsFound

if __name__ == '__main__':
    
    Input = OutputXmlFile(sys.argv[1])
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
